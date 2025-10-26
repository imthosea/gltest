package me.thosea.gltest.bootstrap;

import me.thosea.gltest.game.GlTest;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.Platform;

import static org.lwjgl.glfw.GLFW.*;

public final class Bootstrap {
	public static void main(String[] args) {
		System.out.println("Loading");

		GLFWErrorCallback.createPrint(System.err).set();

		if(!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

		if(Platform.get() == Platform.MACOSX) {
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		}

		long window = glfwCreateWindow(800, 600, "OpenGL", /*monitor*/ 0, /*share*/ 0);
		if(window == 0) {
			System.err.println("Failed to boot");
			glfwTerminate();
			System.exit(1);
		}

		GlTest.start(window);
	}
}