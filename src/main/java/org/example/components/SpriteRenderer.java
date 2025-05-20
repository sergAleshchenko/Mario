package org.example.components;

import org.example.jade.Component;

/**
 * @author Sergei Aleshchenko
 */
public class SpriteRenderer extends Component {
  private boolean firstTime = false;

  @Override
  public void start() {
    System.out.println("SpriteRenderer.start(): I am starting");
  }

  @Override
  public void update(float dt) {
    if (!firstTime) {
      System.out.println("SpriteRenderer.update(): I am updating");
      firstTime = true;
    }
  }
}
