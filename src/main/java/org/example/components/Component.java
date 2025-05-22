package org.example.components;

import org.example.jade.GameObject;

/**
 * @author Sergei Aleshchenko
 */
public abstract class Component {
  public GameObject gameObject = null;

  public abstract void update(float dt);

  public void start() {}
}
