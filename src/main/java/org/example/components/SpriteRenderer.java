package org.example.components;

import org.example.renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 * @author Sergei Aleshchenko
 */
public class SpriteRenderer extends Component {
  private Vector4f color;
  private Sprite sprite;

  public SpriteRenderer(Vector4f color) {
    this.color = color;
    this.sprite = new Sprite(null);
  }

  public SpriteRenderer(Sprite sprite) {
    this.sprite = sprite;
    this.color = new Vector4f(1, 1, 1, 1);
  }

  public Vector2f[] getTexCoords() {
    return sprite.getTexCoords();
  }

  @Override
  public void start() {
  }

  @Override
  public void update(float dt) {

  }

  public Vector4f getColor() {
    return color;
  }

  public Texture getTexture() {
    return sprite.getTexture();
  }

  public Vector2f[] getTextCoords() {
    return sprite.getTexCoords();
  }
}
