package org.example.renderer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author Sergei Aleshchenko
 */
public class Shader {
  private int shaderProgramID;
  private boolean beingUsed = false;
  private String vertexSource;
  private String fragmentSource;
  private String filePath;


  public Shader(String filepath) {
    this.filePath = filepath;
    try {
      String source = new String(Files.readAllBytes(Paths.get(filepath)));
      String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

      // Find the first pattern after #type 'pattern'
      int index = source.indexOf("#type") + 6;
      int eol = source.indexOf("\r\n", index);
      String firstPattern = source.substring(index, eol).trim();

      // Find the second pattern after #type 'pattern'
      index = source.indexOf("#type", eol) + 6;
      eol = source.indexOf("\r\n", index);
      String secondPattern = source.substring(index, eol).trim();

      if(firstPattern.equals("vertex")) {
        vertexSource = splitString[1];
      } else if (firstPattern.equals("fragment")) {
        fragmentSource = splitString[1];
      } else {
        throw new IOException("Unexpected token: '" + firstPattern + "'");
      }

      if(secondPattern.equals("vertex")) {
        vertexSource = splitString[2];
      } else if (secondPattern.equals("fragment")) {
        fragmentSource = splitString[2];
      } else {
        throw new IOException("Unexpected token: '" + secondPattern + "'");
      }

    } catch (IOException e) {
      e.printStackTrace();
      assert false : "Error: could not open file for shader : '" + filepath + "'";
    }
  }

  public void compileShaders() {
    int vertexID, fragmentID;
    // First load and compile the vertex shader
    vertexID = glCreateShader(GL_VERTEX_SHADER);

    // Pass the shader source to the GPU
    glShaderSource(vertexID, vertexSource);
    glCompileShader(vertexID);

    // Check for errors in compilation
    int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filePath + "''\n\tVertex shader compilation failed.");
      System.out.println(glGetShaderInfoLog(vertexID, len));
      assert false : "";
    }

    // First load and compile the vertex shader
    fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

    // Pass the shader source to the GPU
    glShaderSource(fragmentID, fragmentSource);
    glCompileShader(fragmentID);

    // Check for errors in compilation
    success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
    if (success == GL_FALSE) {
      int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filePath + "'\n\tFragment shader compilation failed.");
      System.out.println(glGetShaderInfoLog(fragmentID, len));
      assert false : "";
    }

    linkShaders(vertexID, fragmentID);
  }

  public void linkShaders(int vertexID, int fragmentID) {
    // Link shaders and check for errors
    shaderProgramID = glCreateProgram();
    glAttachShader(shaderProgramID, vertexID);
    glAttachShader(shaderProgramID, fragmentID);
    glLinkProgram(shaderProgramID);

    // Check for linking errors
    int success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
    if (success == GL_FALSE) {
      int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
      System.out.println("ERROR: '" + filePath + "'\n\tLinking of shaders failed.");
      System.out.println(glGetProgramInfoLog(shaderProgramID, len));
      assert false : "";
    }
  }

  public void useShaderProgram() {
    if (!beingUsed) {
      glUseProgram(shaderProgramID);
      beingUsed = true;
    }
  }

  public void detachShaderProgram() {
    glUseProgram(0);
    beingUsed = false;
  }

  public void uploadMat4f(String varName, Matrix4f mat4) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
    mat4.get(matBuffer);
    glUniformMatrix4fv(varLocation, false, matBuffer);
  }

  public void uploadMat3f(String varName, Matrix3f mat3) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
    mat3.get(matBuffer);
    glUniformMatrix3fv(varLocation, false, matBuffer);
  }

  public void uploadVec4f(String varName, Vector4f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
  }

  public void uploadVec3f(String varName, Vector3f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform3f(varLocation, vec.x, vec.y, vec.z);
  }

  public void uploadVec2f(String varName, Vector2f vec) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform2f(varLocation, vec.x, vec.y);
  }

  public void uploadFloat(String varName, float val) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform1f(varLocation, val);
  }

  public void uploadInt(String varName, int val) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform1i(varLocation, val);
  }

  public void uploadTexture(String varName, int slot) {
    int varLocation = glGetUniformLocation(shaderProgramID, varName);
    useShaderProgram();
    glUniform1i(varLocation, slot);
  }
}
