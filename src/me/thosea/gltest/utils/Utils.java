package me.thosea.gltest.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

@UtilityClass
public final class Utils {
	@SneakyThrows
	public static String readString(String path) {
		return new String(readBytes(path), StandardCharsets.UTF_8);
	}

	@SneakyThrows
	public static byte[] readBytes(String path) {
		try(InputStream stream = getResource(path)) {
			return stream.readAllBytes();
		}
	}

	@SneakyThrows
	public static InputStream getResource(String path) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream stream = loader.getResourceAsStream(path);
		if(stream == null) {
			throw new IllegalStateException("No file found: " + path);
		}
		return stream;
	}

	public static ByteBuffer nativeBuffer(byte[] data) {
		ByteBuffer buf = MemoryUtil.memAlloc(data.length);
		buf.put(data);
		return buf;
	}

	@SneakyThrows
	public static Vector3f copy(Vector3f vector) {
		return (Vector3f) vector.clone();
	}
}