package me.thosea.gltest.game;

import me.thosea.gltest.game.Camera.CameraMovement;

import static org.lwjgl.glfw.GLFW.*;

public final class InputHandler {
	private final GlTest game;

	private float lastX = 400;
	private float lastY = 300;
	private boolean firstMouse = true;

	private boolean allowMouse = false;

	public InputHandler(GlTest game) {
		this.game = game;
	}

	public void init() {
		game.callbacks.add(glfwSetKeyCallback(game.window, (window, key, scancode, action, mods) -> {
			if(action != GLFW_PRESS) return;
			this.onKey(window, key);
		}));
		game.callbacks.add(glfwSetCursorPosCallback(game.window, (window, xpos, ypos) -> {
			this.onMouseMove((float) xpos, (float) ypos);
		}));
		game.callbacks.add(glfwSetScrollCallback(game.window, (window, xOff, yOff) -> {
			this.onMouseScroll((float) yOff);
		}));
	}

	public void onKey(long window, int key) {
		if(key == GLFW_KEY_ESCAPE) {
			glfwSetWindowShouldClose(window, true);
		} else if(key == GLFW_KEY_E) {
			allowMouse = !allowMouse;
			glfwSetInputMode(window, GLFW_CURSOR, allowMouse ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
		} else if(key == GLFW_KEY_R) {
			game.reset();
		} else if(key == GLFW_KEY_H) {
			game.highMode = !game.highMode;
		}
	}

	public void onMouseMove(float xPos, float yPos) {
		if(firstMouse) {
			lastX = xPos;
			lastY = yPos;
			firstMouse = false;
		}

		float xOff = xPos - lastX;
		float yOff = lastY - yPos;
		lastX = xPos;
		lastY = yPos;
		game.camera.onMoveMoved(xOff, yOff, true);
	}

	public void onMouseScroll(float yOff) {
		game.camera.onScroll(yOff);
	}

	public void tick(float deltaTime) {
		long window = game.window;
		Camera camera = game.camera;
		if(glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
			camera.onKey(CameraMovement.FORWARD, deltaTime);
		else if(glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
			camera.onKey(CameraMovement.BACKWARD, deltaTime);
		else if(glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
			camera.onKey(CameraMovement.LEFT, deltaTime);
		else if(glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
			camera.onKey(CameraMovement.RIGHT, deltaTime);

		if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
			camera.onKey(CameraMovement.UP, deltaTime);
		else if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
			camera.onKey(CameraMovement.DOWN, deltaTime);

		if(glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
			game.moveLight(0f, 0f, deltaTime * 2);
		else if(glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
			game.moveLight(0f, 0f, -deltaTime * 2);
		else if(glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
			game.moveLight(deltaTime * 2, 0f, 0f);
		else if(glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
			game.moveLight(-deltaTime * 2, 0f, 0f);

		if(glfwGetKey(window, GLFW_KEY_RIGHT_SHIFT) == GLFW_PRESS)
			game.moveLight(0f, -deltaTime * 2, 0f);
		else if(glfwGetKey(window, GLFW_KEY_ENTER) == GLFW_PRESS)
			game.moveLight(0f, deltaTime * 2, 0f);

		if(glfwGetKey(window, GLFW_KEY_N) == GLFW_PRESS)
			game.xScale += deltaTime;
		else if(glfwGetKey(window, GLFW_KEY_M) == GLFW_PRESS)
			game.xScale -= deltaTime;
	}
}