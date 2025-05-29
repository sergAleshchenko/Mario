package org.example.jade.scenes;

import org.example.components.Sprite;
import org.example.components.SpriteRenderer;
import org.example.components.SpriteSheet;
import org.example.jade.Camera;
import org.example.jade.GameObject;
import org.example.jade.Transform;
import org.example.renderer.Texture;
import org.example.util.AssetPool;
import org.joml.Vector2f;

/**
 * @author Sergei Aleshchenko
 */
public class LevelEditorScene extends Scene {


  public LevelEditorScene() {
  }

  @Override
  public void init() {
    loadResources();

    camera = new Camera(new Vector2f());

    SpriteSheet sprites = AssetPool.getSpriteSheet("assets/images/spritesheet.png");

    Transform transform1 = new Transform(new Vector2f(400, 100), new Vector2f(180, 256));
    GameObject obj1 = new GameObject("Object 1", transform1);

    Texture texture1 = AssetPool.getTexture("assets/images/testImage.png");
    SpriteRenderer spriteRenderer1 = new SpriteRenderer(sprites.getSprite(0));
    obj1.addComponent(spriteRenderer1);
    addGameObjectToScene(obj1);

    Transform transform2 = new Transform(new Vector2f(700, 100), new Vector2f(180, 256));
    GameObject obj2 = new GameObject("Object 2", transform2);

    Texture texture2 = AssetPool.getTexture("assets/images/testImage2.png");
    SpriteRenderer spriteRenderer2 = new SpriteRenderer(sprites.getSprite(15));
    obj2.addComponent(spriteRenderer2);
    addGameObjectToScene(obj2);
  }

  private void loadResources() {
    AssetPool.getShader("assets/shaders/default.glsl");

    AssetPool.addSpriteSheet("assets/images/spritesheet.png",
            new SpriteSheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                    16, 16, 26, 0));
  }

  @Override
  public void update(float dt) {
    for (GameObject go : gameObjects) {
      go.update(dt);
    }

    renderer.render();
  }
}
