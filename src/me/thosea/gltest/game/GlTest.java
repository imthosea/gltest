package me.thosea.gltest.game;

import me.thosea.gltest.utils.Shader;
import me.thosea.gltest.utils.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.Callback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;

public final class GlTest {
	public final long window;
	public final List<Callback> callbacks;

	public final Camera camera;
	public final InputHandler input;

	private final int vbo;
	private final int cubeVao;
	private final int lightCubeVao;

	private final Texture diffuseMap;
	private final Texture specularMap;

	private final Shader lightingShader;
	private final Shader lightCubeShader;

	private final Vector3f lightPos = new Vector3f(1.2f, 1.0f, 2.0f);

	private float lastFrame = 0.0f;

	public float xScale = 1f;
	public boolean highMode;

	private GlTest(long window) {
		System.out.println("GLFW initialized");
		this.window = window;

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		this.callbacks = new ArrayList<>();
		this.camera = new Camera();
		this.input = new InputHandler(this);
		this.input.init();

		callbacks.add(glfwSetFramebufferSizeCallback(window, (i_, width, height) -> {
			glViewport(0, 0, width, height);
		}));

		GL.createCapabilities();

		this.cubeVao = glGenVertexArrays();
		this.vbo = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, CubeModel.vertices, GL_STATIC_DRAW);

		glBindVertexArray(cubeVao);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0L);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
		glEnableVertexAttribArray(2);

		this.lightCubeVao = glGenVertexArrays();
		glBindVertexArray(this.lightCubeVao);
		glBindBuffer(GL_ARRAY_BUFFER, this.vbo);
		// note that we update the lamp's position attribute's stride to reflect the updated buffer data
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
		glEnableVertexAttribArray(0);

		this.diffuseMap = Texture.pick("container2.png", "diffuse map");
		this.specularMap = Texture.pick("container2_specular.png", "specular map");

		this.lightingShader = Shader.fromResource("lighting_maps");
		this.lightCubeShader = Shader.fromResource("light_cube");

		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		this.loop();
	}

	private void loop() {
		glEnable(GL_DEPTH_TEST);
		lightingShader.bind();
		lightingShader.setInt("material.diffuse", 0);
		lightingShader.setInt("material.specular", 1);

		while(!glfwWindowShouldClose(window)) {
			float currentFrame = (float) glfwGetTime();
			float deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
			this.input.tick(deltaTime);

			glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			lightingShader.bind();
			lightingShader.setVec3("light.position", lightPos);
			lightingShader.setVec3("viewPos", camera.pos);

			lightingShader.setVec3("light.ambient", 0.2f, 0.2f, 0.2f);
			lightingShader.setVec3("light.diffuse", 0.5f, 0.5f, 0.5f);
			lightingShader.setVec3("light.specular", 1.0f, 1.0f, 1.0f);

			lightingShader.setFloat("material.shininess", 64.0f);

			lightingShader.setFloat("currentFrame", currentFrame);
			lightingShader.setBool("drunkMode", highMode);

			var projection = new Matrix4f();
			projection.perspective((float) Math.toRadians(45f), 800f / 600f, 0.1f, 100f);
			var view = camera.getViewMatrix();

			lightingShader.setMat4("projection", projection);
			lightingShader.setMat4("view", view);

			// world transformation
			var model = new Matrix4f();
			model.scale(1f, 1f, xScale);
			lightingShader.setMat4("model", model);

			diffuseMap.bind(GL_TEXTURE0);
			specularMap.bind(GL_TEXTURE1);

			// render the cube
			glBindVertexArray(cubeVao);
			glDrawArrays(GL_TRIANGLES, 0, 36);

			// also draw the lamp object
			lightCubeShader.bind();
			lightCubeShader.setMat4("projection", projection);
			lightCubeShader.setMat4("view", view);
			model = new Matrix4f();
			model.translate(lightPos);
			model.scale(0.2f, 0.2f, 0.2f);
			lightCubeShader.setMat4("model", model);

			glBindVertexArray(lightCubeVao);
			glDrawArrays(GL_TRIANGLES, 0, 36);

			glfwSwapBuffers(window);
			glfwPollEvents();
		}

		this.exit();
	}

	private void exit() {
		System.out.println("Exiting!");
		glfwTerminate();
		System.exit(0);
	}

	public void reset() {
		this.lightPos.set(1.2f, 1.0f, 2.0f);
		this.camera.reset();
		this.xScale = 1f;
	}

	public void moveLight(float x, float y, float z) {
		this.lightPos.add(x, y, z);
	}

	public static void start(long window) {
		new GlTest(window);
	}
}