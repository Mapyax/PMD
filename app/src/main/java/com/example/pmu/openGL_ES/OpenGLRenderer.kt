package com.example.pmu.openGL_ES

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.ETC1Util.loadTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.pmu.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private lateinit var sphere: Sphere
    private lateinit var square: TexturedSquare
    private var textureId: Int = 0
    var sphereAngle = 0f

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mVPMatrix = FloatArray(16)

    private lateinit var sun: Sphere
    private lateinit var planets: List<Sphere>
    private lateinit var planetTextures: IntArray
    private val planetAngles = FloatArray(9)
    private val orbitRadii = floatArrayOf(
        1.0f,  // Mercury
        1.8f,  // Venus
        2.6f,  // Earth
        3.0f,  // Moon (scaled value)
        3.5f,  // Mars
        4.1f,  // Jupiter
        5.8f,  // Saturn
        6.2f,  // Uranus
        6.9f   // Neptune
    ) // Increased distances


    private val rotationSpeeds = floatArrayOf(
        2f,  // Mercury
        1.5f, // Venus
        1.2f, // Earth
        1f,  // Mars
        0.9f, // Jupiter
        0.8f, // Saturn
        0.7f, // Uranus
        0.6f, // Neptune
        0.5f  // Pluto (if included)
    ) // Reduced speeds
    private val planetRotationSpeeds = FloatArray(9) { 10f } // Rotation speed for each planet

    private var lineProgram: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_ALWAYS)

        sun = Sphere(radius = 0.6f)
        sun.initialize()

        planets = listOf(
            Sphere(radius = 0.3f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.3f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.5f),
            Sphere(radius = 0.5f)
        )

        planets.forEach { it.initialize() }

        planetTextures = IntArray(9)
        planetTextures[0] = loadTexture(context, R.drawable.mercury)
        planetTextures[1] = loadTexture(context, R.drawable.venus)
        planetTextures[2] = loadTexture(context, R.drawable.earth)
        planetTextures[3] = loadTexture(context, R.drawable.moon)
        planetTextures[4] = loadTexture(context, R.drawable.mars)
        planetTextures[5] = loadTexture(context, R.drawable.jupiter)
        planetTextures[6] = loadTexture(context, R.drawable.saturn)
        planetTextures[7] = loadTexture(context, R.drawable.uranus)
        planetTextures[8] = loadTexture(context, R.drawable.neptune)

        textureId = loadTexture(context, R.drawable.sun)

        square = TexturedSquare(context)
        square.initialize()

        // Initialize line shaders
        lineProgram = loadLineShaderProgram()
    }




    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Draw galaxy background
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, 15f, 10f, 1f)
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        square.draw(mVPMatrix)

        // Draw sun
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -5f) // Sun at the center
        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
        sun.draw(mVPMatrix, textureId)

        // Draw planets with orbits
        for (i in planets.indices) {
            // Draw orbit
            drawOrbit(orbitRadii[i])

            Matrix.setIdentityM(modelMatrix, 0)

            // Rotate planet around the sun
            val angle = planetAngles[i]
            val radius = orbitRadii[i]
            val x = radius * cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = radius * sin(Math.toRadians(angle.toDouble())).toFloat()

            Matrix.translateM(modelMatrix, 0, x, y, -5f) // Position the planet
            Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)

            // Draw the planet with its texture
            planets[i].draw(mVPMatrix, planetTextures[i])

            // Update angle for orbiting
            planetAngles[i] = (planetAngles[i] + rotationSpeeds[i]) % 360

            // Rotate the planet around its own axis
            Matrix.rotateM(modelMatrix, 0, planetAngles[i] * planetRotationSpeeds[i], 0f, 1f, 0f)
            Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, mVPMatrix, 0, modelMatrix, 0)
            planets[i].draw(mVPMatrix, planetTextures[i]) // Draw again with the rotation
        }

    }
    private fun drawOrbit(radius: Float) {
        val numPoints = 100
        val orbitVertices = FloatArray(numPoints * 2)

        for (i in 0 until numPoints) {
            val angle = Math.toRadians((i * 360.0 / numPoints))
            orbitVertices[i * 2] = (radius * cos(angle)).toFloat()
            orbitVertices[i * 2 + 1] = (radius * sin(angle)).toFloat()
        }

        val orbitBuffer: FloatBuffer = createDirectFloatBuffer(orbitVertices)

        GLES20.glUseProgram(lineProgram)
        val positionHandle = GLES20.glGetAttribLocation(lineProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 99, GLES20.GL_FLOAT, false, 1, orbitBuffer)

        val colorHandle = GLES20.glGetUniformLocation(lineProgram, "vColor")
        GLES20.glUniform4f(colorHandle, 1.0f, 1.0f, 1.0f, 1.0f)

        GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, numPoints)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }



    private fun loadLineShaderProgram(): Int {
        // Load the vertex and fragment shaders for the lines and create a program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, readShaderFromFile("line_vertex_shader.glsl"))
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, readShaderFromFile("line_fragment_shader.glsl"))
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }



    // Create a direct FloatBuffer
    private fun createDirectFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(data.size * 4) // 4 bytes per float
            .order(ByteOrder.nativeOrder()) // Set to native byte order
            .asFloatBuffer()
        buffer.put(data)
        buffer.position(0)
        return buffer
    }


    private fun readShaderFromFile(fileName: String): String {
        // Function to read shader code from the file assets
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    // Load texture helper function
    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false

            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            bitmap.recycle()
        }

        return textureHandle[0]
    }
}