package org.example.renderer;

import org.example.components.SpriteRenderer;
import org.example.jade.Window;
import org.example.util.AssetPool;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Sergei Aleshchenko
 */
public class RenderBatch {
  // Vertex
  // ======
  // Pos              Color                         tex coords      tex id
  // float, float     float, float, float, float    float, float    float
  private final int POS_SIZE = 2;
  private final int COLOR_SIZE = 4;
  private final int POS_OFFSET = 0;
  private final int TEX_COORDS_SIZE = 2;
  private final int TEX_ID_SIZE =  1;

  private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
  private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
  private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

  private final int VERTEX_SIZE = 9;
  private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

  private SpriteRenderer[] sprites;
  private int numSpriteRenderers;
  private boolean hasRoom;
  private float[] verices;

  private List<Texture> textures;
  private int vaoID, vboID;
  private int maxBatchSize;
  private Shader shader;
  private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

  public RenderBatch(int maxBatchSize) {
    shader = AssetPool.getShader("assets/shaders/default.glsl");
    this.shader.compileShader();
    this.sprites = new SpriteRenderer[maxBatchSize];
    this.maxBatchSize = maxBatchSize;

    // 4 vertices quads
    verices = new float[maxBatchSize * 4 * VERTEX_SIZE];

    this.numSpriteRenderers = 0;
    this.hasRoom = true;
    this.textures = new ArrayList<>();
  }

  public boolean hasRoom() {
    return hasRoom;
  }

  public boolean hasTextureRoom() {
    return textures.size() < 8;
  }

  public boolean hasTexture(Texture texture) {
    return textures.contains(texture);
  }

  public void start() {
    // Generate and bind a Vertex Array Object
    vaoID = glGenVertexArrays();
    glBindVertexArray(vaoID);

    // Allocate space for vertices
    vboID = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferData(GL_ARRAY_BUFFER, verices.length * Float.BYTES, GL_DYNAMIC_DRAW);

    // Create and upload indices buffer
    int eboID = glGenBuffers();
    int[] indices = generateIndices();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    // Enable the buffer attribute pointers
    glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false,
            VERTEX_SIZE_BYTES, POS_OFFSET);
    glEnableVertexAttribArray(0);

    glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false,
            VERTEX_SIZE_BYTES, COLOR_OFFSET);
    glEnableVertexAttribArray(1);

    glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false,
            VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
    glEnableVertexAttribArray(2);

    glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false,
            VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
    glEnableVertexAttribArray(3);

  }

  public void addSprite(SpriteRenderer spr) {
    // Get index and add renderObject
    int index = numSpriteRenderers;
    sprites[index] = spr;
    numSpriteRenderers++;

    Texture texture = spr.getTexture();
    if (texture != null) {
      if (!textures.contains(texture)) {
        textures.add(texture);
      }
    }

    // Add properties to local vertices array
    loadVertexProperties(index);

    if (numSpriteRenderers >= maxBatchSize) {
      hasRoom = false;
    }
  }

  public void render() {
    // For now, we will rebuffer all data every frame
    glBindBuffer(GL_ARRAY_BUFFER, vboID);
    glBufferSubData(GL_ARRAY_BUFFER, 0, verices);

    // Use shader
    shader.useShaderProgram();
    shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
    shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

    for (int i = 0; i < textures.size(); i++) {
      glActiveTexture(GL_TEXTURE0 + i + 1);
      textures.get(i).bind();
    }

    shader.uploadIntArray("uTextures", texSlots);

    glBindVertexArray(vaoID);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glDrawElements(GL_TRIANGLES, this.numSpriteRenderers * 6, GL_UNSIGNED_INT, 0);

    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindVertexArray(0);

    for (int i = 0; i < textures.size(); i++) {
      textures.get(i).unbind();
    }

    shader.detachShaderProgram();
  }

  public void loadVertexProperties(int index) {
    SpriteRenderer sprite = sprites[index];

    // Find offset within array (4 vertices per sprite)
    int offset = index * 4 * VERTEX_SIZE;

    // float float        float float float float
    Vector4f color = sprite.getColor();
    Vector2f[] texCoords = sprite.getTexCoords();

    int texId = 0;
    Texture texture = sprite.getTexture();

    // [0, tex, tex, tex, tex, ...]
    if (texture != null) {
      for (int i = 0; i < textures.size(); i++) {
        if (textures.get(i) == texture) {
          texId = i + 1;
          break;
        }
      }
    }

    // Add vertice with the appropriate properties
    float xAdd = 1.0f;
    float yAdd = 1.0f;

    // *    *
    // *    *
    for (int i = 0; i < 4; i++) {
      if (i == 1) {
        yAdd = 0.0f;
      } else if (i == 2) {
        xAdd = 0.0f;
      } else if (i == 3) {
        yAdd = 1.0f;
      }

      // Load position
      verices[offset] = sprite.gameObject.transform.position.x +
              (xAdd * sprite.gameObject.transform.scale.x);
      verices[offset + 1] = sprite.gameObject.transform.position.y +
              (yAdd * sprite.gameObject.transform.scale.y);

      // Load color
      verices[offset + 2] = color.x;
      verices[offset + 3] = color.y;
      verices[offset + 4] = color.z;
      verices[offset + 5] = color.w;

      // Load texture coordinates
      verices[offset + 6] = texCoords[i].x;
      verices[offset + 7] = texCoords[i].y;

      // Load texture id
      verices[offset + 8] = texId;


      offset += VERTEX_SIZE;
    }
  }

  private int[] generateIndices() {
    // 6 indices per quad (3 per triangle)
    int[] elements = new int[6 * maxBatchSize];

    for (int i = 0; i < maxBatchSize; i++) {
      loadElementIndices(elements, i);
    }

    return elements;
  }

  private void loadElementIndices(int[] elements, int index) {
    int offsetArrayIndex = 6 * index;
    int offset = 4 * index;

    // 3, 2, 0, 0, 2, 1       7, 6, 4, 4, 6, 5
    // Triangle 1
    elements[offsetArrayIndex] = offset + 3;
    elements[offsetArrayIndex + 1] = offset + 2;
    elements[offsetArrayIndex + 2] = offset + 0;

    // Triangle 2
    elements[offsetArrayIndex + 3] = offset + 0;
    elements[offsetArrayIndex + 4] = offset + 2;
    elements[offsetArrayIndex + 5] = offset + 1;
  }
}
