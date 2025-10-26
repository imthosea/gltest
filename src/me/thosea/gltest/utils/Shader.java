package me.thosea.gltest.utils;

import lombok.SneakyThrows;
import org.joml.Matrix2f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL32.*;


public final class Shader {
	private final int id;

	private Shader(String vertexData, String fragmentData) {
		int vertex = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertex, vertexData);
		glCompileShader(vertex);
		this.checkCompile(vertex);

		int fragment = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragment, fragmentData);
		glCompileShader(fragment);
		this.checkCompile(fragment);

		this.id = glCreateProgram();
		glAttachShader(id, vertex);
		glAttachShader(id, fragment);
		glLinkProgram(id);
		this.checkLink(id);

		glDeleteShader(vertex);
		glDeleteShader(fragment);
	}

	private void checkCompile(int shader) {
		int status = glGetShaderi(shader, GL_COMPILE_STATUS);
		if(status == 0) {
			String error = glGetShaderInfoLog(shader);
			throw new IllegalStateException("Shader compilation error\n" + error);
		}
	}

	private void checkLink(int id) {
		int status = glGetProgrami(id, GL_LINK_STATUS);
		if(status == 0) {
			String error = glGetProgramInfoLog(id);
			throw new IllegalStateException("Shader linking error\n" + error);
		}
	}

	public void bind() {
		glUseProgram(id);
	}
	public void setBool(String name, boolean value) {
		glUniform1i(glGetUniformLocation(id, name), value ? 1 : 0);
	}
	public void setInt(String name, int value) {
		glUniform1i(glGetUniformLocation(id, name), value);
	}
	public void setFloat(String name, float value) {
		glUniform1f(glGetUniformLocation(id, name), value);
	}
	public void setVec2(String name, Vector2f value) {
		float[] vector = new float[2];
		vector[0] = value.x;
		vector[1] = value.y;
		glUniform2fv(glGetUniformLocation(id, name), vector);
	}
	public void setVec2(String name, float x, float y) {
		glUniform2f(glGetUniformLocation(id, name), x, y);
	}
	public void setVec3(String name, Vector3f value) {
		float[] vector = new float[3];
		vector[0] = value.x;
		vector[1] = value.y;
		vector[2] = value.z;
		glUniform3fv(glGetUniformLocation(id, name), vector);
	}
	public void setVec3(String name, float x, float y, float z) {
		glUniform3f(glGetUniformLocation(id, name), x, y, z);
	}
	public void setVec4(String name, Vector4f value) {
		float[] vector = new float[4];
		vector[0] = value.x;
		vector[1] = value.y;
		vector[2] = value.z;
		vector[3] = value.w;
		glUniform4fv(glGetUniformLocation(id, name), vector);
	}
	public void setVec4(String name, float x, float y, float z, float w) {
		glUniform4f(glGetUniformLocation(id, name), x, y, z, w);
	}
	public void setMat2(String name, Matrix2f mat) {
		float[] value = new float[4];
		mat.get(value);
		glUniformMatrix2fv(glGetUniformLocation(id, name), false, value);
	}
	public void setMat3(String name, Matrix3f mat) {
		float[] value = new float[9];
		mat.get(value);
		glUniformMatrix3fv(glGetUniformLocation(id, name), false, value);
	}
	public void setMat4(String name, Matrix4f mat) {
		float[] value = new float[16];
		mat.get(value);
		glUniformMatrix4fv(glGetUniformLocation(id, name), false, value);
	}

	@SneakyThrows
	public static Shader fromResource(String name) {
		return new Shader(
				Utils.readString(name + ".vs"),
				Utils.readString(name + ".fs")
		);
	}
}