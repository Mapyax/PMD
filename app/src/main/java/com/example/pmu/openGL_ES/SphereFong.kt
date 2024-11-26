package com.example.pmu.openGL_ES

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class SphereFong(private val latitudeBands: Int = 40, private val longitudeBands: Int = 40, val radius: Float = 1.0f) {
    private lateinit var shaderProgram: ShaderProgram
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer

    private val vertices: FloatArray
    private val indices: ShortArray
    private val textureCoords: FloatArray
    private val normals: FloatArray

    init {
        val vertexList = mutableListOf<Float>()
        val indexList = mutableListOf<Short>()
        val textureList = mutableListOf<Float>()
        val normalList = mutableListOf<Float>()

        for (lat in 0..latitudeBands) {
            val theta = lat * Math.PI / latitudeBands
            val sinTheta = sin(theta).toFloat()
            val cosTheta = cos(theta).toFloat()

            for (long in 0..longitudeBands) {
                val phi = long * 2 * Math.PI / longitudeBands
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertexList.add(x * radius)
                vertexList.add(y * radius)
                vertexList.add(z * radius)

                normalList.add(x) // Нормали
                normalList.add(y)
                normalList.add(z)

                val u = 1f - (long / longitudeBands.toFloat())
                val v = 1f - (lat / latitudeBands.toFloat())
                textureList.add(u)
                textureList.add(v)
            }
        }

        for (lat in 0 until latitudeBands) {
            for (long in 0 until longitudeBands) {
                val first = (lat * (longitudeBands + 1) + long).toShort()
                val second = (first + longitudeBands + 1).toShort()

                indexList.add(first)
                indexList.add(second)
                indexList.add((first + 1).toShort())

                indexList.add(second)
                indexList.add((second + 1).toShort())
                indexList.add((first + 1).toShort())
            }
        }

        vertices = vertexList.toFloatArray()
        indices = indexList.toShortArray()
        textureCoords = textureList.toFloatArray()
        normals = normalList.toFloatArray()
    }

    fun initialize() {
        shaderProgram = ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(indices)
                position(0)
            }
        }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }

        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(normals)
                position(0)
            }
        }
    }

    fun draw(mvpMatrix: FloatArray, viewMatrix: FloatArray, lightPos: FloatArray, textureId: Int) {
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
        val normalHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Normal")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")
        val viewMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_ViewMatrix")
        val lightPosHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_LightPos")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, viewMatrix, 0)
        GLES20.glUniform3fv(lightPosHandle, 1, lightPos, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glEnableVertexAttribArray(normalHandle)

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            attribute vec3 a_Normal;
            uniform mat4 u_MVPMatrix;
            uniform mat4 u_ViewMatrix;
            uniform vec3 u_LightPos;
            varying vec2 v_TexCoord;
            varying float v_LightIntensity;

            void main() {
                gl_Position = u_MVPMatrix * a_Position;
                v_TexCoord = a_TexCoord;

                // Перевод нормалей в мировое пространство
                vec3 normal = normalize(mat3(u_ViewMatrix) * a_Normal);
                vec3 lightDir = normalize(u_LightPos - vec3(gl_Position));

                // Расчет интенсивности света по Фонгу
                v_LightIntensity = max(dot(normal, lightDir), 0.0);
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            varying vec2 v_TexCoord;
            varying float v_LightIntensity;
            uniform sampler2D u_Texture;

            void main() {
                vec4 texColor = texture2D(u_Texture, v_TexCoord);
                gl_FragColor = vec4(texColor.rgb * v_LightIntensity, texColor.a);
            }
        """
    }
}