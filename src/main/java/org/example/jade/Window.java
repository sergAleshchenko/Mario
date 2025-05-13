package org.example.jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author Sergei Aleshchenko
 */
public class Window {

    private int width, height;
    private String title;
    private static Window window;
    private long glfwWindow;


    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Mario";
    }


    public static Window ge() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Window get() {
        return null;
    }

    public void run() {
        System.out.println("Hello, LWJGL " + Version.getVersion() + "!");

        init();
        loop();
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

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);
}

    private void loop() {

    }
}
