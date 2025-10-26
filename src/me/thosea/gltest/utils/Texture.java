package me.thosea.gltest.utils;

import lombok.SneakyThrows;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

import java.awt.FileDialog;
import java.awt.Frame;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.stb.STBImage.*;

public final class Texture {
	private final int textureId;

	@SneakyThrows
	private Texture(String path, boolean isClasspath) {
		this.textureId = glGenTextures();

		int[] width = new int[1];
		int[] height = new int[1];
		int[] nrComponents = new int[1];

		ByteBuffer buffer;
		if(isClasspath) {
			ByteBuffer fileData = Utils.nativeBuffer(Utils.readBytes(path));
			fileData.flip();
			buffer = stbi_load_from_memory(
					fileData, width, height, nrComponents, 0
			);
			MemoryUtil.memFree(fileData);
		} else {
			buffer = stbi_load(
					path, width, height, nrComponents, 0
			);
		}

		if(buffer == null) {
			String reason = stbi_failure_reason();
			throw new IllegalStateException("Failed to load " + path + "\n" + reason);
		}

		int format = switch(nrComponents[0]) {
			case 1 -> GL_RED;
			case 3 -> GL_RGB;
			case 4 -> GL_RGBA;
			default -> 0;
		};
		glBindTexture(GL_TEXTURE_2D, this.textureId);
		glTexImage2D(
				GL_TEXTURE_2D,
				/*level*/ 0,
				format, width[0], height[0],
				/*border*/ 0, format,
				GL_UNSIGNED_BYTE,
				buffer
		);
		glGenerateMipmap(GL_TEXTURE_2D);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		stbi_image_free(buffer);
	}

	public void bind(int slot) {
		glActiveTexture(slot);
		glBindTexture(GL_TEXTURE_2D, this.textureId);
	}

	public static Texture loadClasspath(String path) {
		return new Texture(path, /*isClasspath*/true);
	}

	static {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
	}

	public static Texture pick(String defPath, String imageName) {
		System.out.println("Asking for " + imageName);

		String title = "Pick " + imageName;
		String picked;
		if(Platform.get() != Platform.MACOSX) {
			picked = awtPick(title);
		} else {
			picked = macPick(title);
		}

		if(picked != null) {
			return new Texture(picked, /*isClasspath*/ false);
		} else {
			return new Texture(defPath, /*isClasspath*/ true);
		}
	}

	@SneakyThrows
	private static String macPick(String title) {
		Runtime runtime = Runtime.getRuntime();
		String[] cmd = {
				"osascript", "-e",
				"POSIX path of (choose file of type {\"public.image\"} " +
						"with prompt \"" + title + "\")"
		};
		Process proc = runtime.exec(cmd);
		if(proc.waitFor() != 0)
			return null;
		return new String(
				proc.getInputStream().readAllBytes(),
				StandardCharsets.UTF_8
		).trim();
	}

	private static String awtPick(String title) {
		FileDialog dialog = new FileDialog((Frame) null, title);
		dialog.setFile("*.png|*.jpeg|*.jpg");
		dialog.setFilenameFilter((dir, name) -> {
			name = name.toLowerCase();
			return name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".jpg");
		});
		dialog.setVisible(true);
		if(dialog.getFile() == null) return null;
		return dialog.getDirectory() + "/" + dialog.getFile();
	}
}