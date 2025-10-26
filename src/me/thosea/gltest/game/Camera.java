package me.thosea.gltest.game;

import me.thosea.gltest.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Camera {
	private static final float SPEED = 2.5f;
	private static final float SENSITIVITY = 0.1f;

	public final Vector3f pos = new Vector3f();
	private final Vector3f front = new Vector3f(0f, 0f, -1f);
	private final Vector3f up = new Vector3f(0f, 1f, 0f);
	private final Vector3f right = new Vector3f();
	private final Vector3f worldup = Utils.copy(up);

	private float yaw = -90;
	private float pitch = 0f;
	private float zoom = 45f;

	public Camera() {
		this.updateCameraVectors();
	}

	public Matrix4f getViewMatrix() {
		var result = new Matrix4f();
		Vector3f center = new Vector3f(
				pos.x + front.x,
				pos.y + front.y,
				pos.z + front.z
		);
		result.lookAt(this.pos, center, this.up);
		return result;
	}

	public void reset() {
		pos.set(0f);
	}

	public void onKey(CameraMovement direction, float deltaTime) {
		float velocity = SPEED * deltaTime;
		switch(direction) {
			case FORWARD -> pos.add(Utils.copy(this.front).mul(velocity));
			case BACKWARD -> pos.sub(Utils.copy(this.front).mul(velocity));
			case LEFT -> pos.sub(Utils.copy(this.right).mul(velocity));
			case RIGHT -> pos.add(Utils.copy(this.right).mul(velocity));
			case UP -> pos.add(0, velocity, 0);
			case DOWN -> pos.sub(0, velocity, 0);
		}
	}

	public void onMoveMoved(float xoffset, float yoffset, boolean constrainPitch) {
		xoffset *= SENSITIVITY;
		yoffset *= SENSITIVITY;

		this.yaw += xoffset;
		this.pitch += yoffset;

		// make sure that when pitch is out of bounds, screen doesn't get flipped
		if(constrainPitch) {
			if(pitch > 89.0f)
				pitch = 89.0f;
			if(pitch < -89.0f)
				pitch = -89.0f;
		}

		updateCameraVectors();
	}

	public void onScroll(float amount) {
		this.zoom = Math.clamp(zoom - amount, 1f, 45F);
	}

	private void updateCameraVectors() {
		// calculate the new Front vector
		front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.y = (float) Math.sin(Math.toRadians(pitch));
		front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
		front.normalize();

		front.cross(worldup, right);
		right.normalize();

		right.cross(front, up);
		up.normalize();
	}

	public enum CameraMovement {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
}