package com.example.pmu.openGL_ES
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {
    private lateinit var shaderProgram: ShaderProgram
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var colorBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer

    private val vertices = floatArrayOf(
        -1f, 1f, 1f,   // 0
        -1f, -1f, 1f,  // 1
        1f, -1f, 1f,   // 2
        1f, 1f, 1f,    // 3
        -1f, 1f, -1f,  // 4
        -1f, -1f, -1f, // 5
        1f, -1f, -1f,  // 6
        1f, 1f, -1f    // 7
    )

    private val colors = floatArrayOf(
        46f, 1f, 0f, 0.2f,    // 0
        46f, 0.8f, 0f, 0.2f,    // 1
        46f, 1f, 1f, 0.2f,    // 2
        46f, 0.8f, 0f, 0.2f,    // 3
        46f, 1f, 0f, 0.2f,    // 4
        46f, 0f, 0f, 0.2f,    // 5
        46f, 1f, 1f, 0.2f,    // 6
        46f, 0.8f, 0f, 0.2f     // 7
    )

    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3,    // Front face
        4, 5, 6, 4, 6, 7,    // Back face
        0, 1, 5, 0, 5, 4,    // Left face
        3, 2, 6, 3, 6, 7,    // Right face
        0, 3, 7, 0, 7, 4,    // Top face
        1, 2, 6, 1, 6, 5     // Bottom face
    )

    fun initialize() {
        shaderProgram = ShaderProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        colorBuffer = ByteBuffer.allocateDirect(colors.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(colors)
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
    }

    fun draw(mvpMatrix: FloatArray) {
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val colorHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Color")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glEnableVertexAttribArray(colorHandle)

        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            positionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            vertexBuffer
        )

        colorBuffer.position(0)
        GLES20.glVertexAttribPointer(
            colorHandle,
            4,
            GLES20.GL_FLOAT,
            false,
            4 * 4,
            colorBuffer
        )

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            indices.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            attribute vec4 a_Position;
            attribute vec4 a_Color;
            uniform mat4 u_MVPMatrix;
            varying vec4 v_Color;

            void main() {
                gl_Position = u_MVPMatrix * a_Position;
                v_Color = a_Color;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            varying vec4 v_Color;

            void main() {
                gl_FragColor = v_Color;
            }
        """
    }
}