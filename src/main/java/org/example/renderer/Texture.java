package org.example.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.stb.STBImage.*;

/**
 * @author Sergei Aleshchenko
 */
public class Texture {
  private String filepath;
  private int textID;
  private int width;
  private int height;


  public Texture(String filepath) {
    this.filepath = filepath;

    textID = glGenTextures();
    glBindTexture(GL_TEXTURE_2D, textID);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    ByteBuffer image = loadImage();
    stbi_image_free(image);
  }

  private ByteBuffer loadImage() {
    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    stbi_set_flip_vertically_on_load(true);
    ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

    if (image != null) {
      this.width = width.get(0);
      this.height = height.get(0);

      if (channels.get(0) == 3) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,
                width.get(0), height.get(0), 0,
                GL_RGB, GL_UNSIGNED_BYTE, image);
      } else if (channels.get(0) == 4) {
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                width.get(0), height.get(0), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, image);
      } else {
        assert false : "Error: (Texture) Unknown number of channels '"
                + channels.get(0) + "'";
      }
    } else {
      assert false : "Error: (Texture) Could not load image '" + filepath + "'";
    }

    return image;
  }

  public void bind() {
    glBindTexture(GL_TEXTURE_2D, textID);
  }

  public void unbind() {
    glBindTexture(GL_TEXTURE_2D, 0);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
