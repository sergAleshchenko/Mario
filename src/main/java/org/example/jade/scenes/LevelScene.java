package org.example.jade.scenes;

import org.example.jade.Window;

/**
 * @author Sergei Aleshchenko
 */
public class LevelScene extends Scene {

  public LevelScene() {
    System.out.println("Inside level scene");
    Window.get().r = 1;
    Window.get().g = 1;
    Window.get().b = 1;
  }

  @Override
  public void update(float dt) {
  }

}
