attribute vec4 vPosition;
uniform mat4 uMVPMatrix;
void main() {
    gl_Position = uMVPMatrix * vPosition;
    gl_PointSize = 5.0; // Size of the points
}
