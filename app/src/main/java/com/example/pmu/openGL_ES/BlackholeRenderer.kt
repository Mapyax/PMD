package com.example.pmu.openGL_ES

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.content.Context
import com.example.pmu.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class BlackholeRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var square: Square
    private var programId = 0
    private var mvpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vertexBuffer: FloatBuffer
    private val vertexData = floatArrayOf(
        -1f, -1f, 0f, // Bottom-left
        1f, -1f, 0f,  // Bottom-right
        -1f, 1f, 0f,  // Top-left
        1f, 1f, 0f    // Top-right
    )

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexData)
                position(0)
            }
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, readShaderFromFile("StarNestVertexShader.glsl"))
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, readShaderFromFile("StarNestFragmentShader.glsl"))

        programId = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        Matrix.setIdentityM(mvpMatrix, 0)

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_ALWAYS)

        square = Square(context)
        square.initialize()
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    private fun readShaderFromFile(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, 15f, 10f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        square.draw(mvpMatrix)

        // GLES20.glUseProgram(programId)

        // val resolutionHandle = GLES20.glGetUniformLocation(programId, "iResolution")
        // val timeHandle = GLES20.glGetUniformLocation(programId, "iTime")

        // GLES20.glUniform2f(resolutionHandle, 1080f, 1920f)
        // GLES20.glUniform1f(timeHandle, System.currentTimeMillis() / 1000f)

        // GLES20.glEnableVertexAttribArray(0)
        // GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        // GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        // GLES20.glDisableVertexAttribArray(0)


        GLES20.glDisable(GLES20.GL_BLEND)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }
}