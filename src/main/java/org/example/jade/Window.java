package org.example.jade;

import org.example.jade.controllers.KeyListener;
import org.example.jade.controllers.MouseListener;
import org.example.jade.scenes.LevelEditorScene;
import org.example.jade.scenes.LevelScene;
import org.example.jade.scenes.Scene;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Sergei Aleshchenko
 */
public class Window {
  private int width, height;
  private String title;
  private long glfwWindow;
  private boolean fadeToBlack = false;

  private static Window window = null;
  private static int currentSceneIndex = -1;
  private static Scene currentScene = null;

  public float r, g, b, a;

  private Window() {
    this.width = 1920;
    this.height = 1080;
    this.title = "Mario";
    this.r = 0;
    this.g = 0;
    this.b = 0;
    this.a = 0;
  }

  public static void changeScene(int newScene) {
    switch (newScene) {
      case 0:
        currentScene = new LevelEditorScene();
        currentScene.init();
        currentScene.start();
        break;
      case 1:
        currentScene = new LevelScene();
        currentScene.init();
        currentScene.start();
        break;
      default:
        assert false : "Unknown scene '" + newScene + "'";
        break;
    }
  }

  public static Window get() {
    if (Window.window == null) {
      Window.window = new Window();
    }
    return Window.window;
  }

  public static Scene getScene() {
    return get().currentScene;
  }

  public void run() {
    System.out.println("Hello, LWJGL " + Version.getVersion() + "!");

    init();
    loop();

    // Free the memory
    glfwFreeCallbacks(glfwWindow);
    glfwDestroyWindow(glfwWindow);

    // Terminate GLFW and the free the error callback
    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }


  public void init() {
    // Setup an error callback
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialize GLFW
    if(!glfwInit() ) {
      throw new IllegalStateException("Unable to initialize GLFW.");
    }

    // Configure GLFW
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    // Create the window
    glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

    if (glfwWindow == NULL) {
      throw new IllegalStateException("Failed to create the GLFW window.");
    }

    glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
    glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
    glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
    glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

    // Make the OpenGL context current
    glfwMakeContextCurrent(glfwWindow);
    // Enable v-sync
    glfwSwapInterval(1);

    // Make the window visible
    glfwShowWindow(glfwWindow);

    GL.createCapabilities();
    Window.changeScene(0);
  }

  private void loop() {
    float beginTime = (float) glfwGetTime();
    float endTime;
    float dt =  -1.0f;

    while (!glfwWindowShouldClose(glfwWindow)) {
      glfwPollEvents();

      glClearColor(r, g, b, a);
      glClear(GL_COLOR_BUFFER_BIT);

      if (dt >= 0) {
        currentScene.update(dt);
      }

      glfwSwapBuffers(glfwWindow);

      endTime = (float) glfwGetTime();
      dt = endTime - beginTime;
      beginTime = endTime;
    }
  }
}
