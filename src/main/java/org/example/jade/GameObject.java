package org.example.jade;

import org.example.components.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei Aleshchenko
 */
public class GameObject {
  private String name;
  private List<Component> components;
  public Transform transform;

  public GameObject(String name) {
    this.name = name;
    this.components = new ArrayList<>();
    this.transform = new Transform();
  }

  public GameObject(String name, Transform transform) {
    this.name = name;
    this.components = new ArrayList<>();
    this.transform = transform;
  }

  public <T extends Component> T getComponent(Class<T> componentClass) {
    for (Component c : components) {
      if(componentClass.isAssignableFrom(c.getClass())) {
        try {
          return componentClass.cast(c);
        } catch (ClassCastException e) {
          e.printStackTrace();
          assert false : "Error: Casting component.";
        }
      }
    }

    return null;
  }

  public <T extends Component> void removeComponent(Class<T> componentClass) {
    for (int i = 0; i < components.size(); i++) {
      Component component = components.get(i);
      if (componentClass.isAssignableFrom(component.getClass())) {
        components.remove(i);
        return;
      }
    }
  }

  public void addComponent(Component component) {
    components.add(component);
    component.gameObject = this;
  }

  public void update(float dt) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).update(dt);
    }
  }

  public void start() {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).start();
    }
  }
}
