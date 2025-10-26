val lwjglVersion = rootProject
	.versionCatalogs
	.named("libs")
	.findVersion("lwjgl")
	.orElseThrow()

repositories {
	mavenCentral()
}

val implementation by configurations

dependencies {
	implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

	implementation("org.lwjgl:lwjgl")
	implementation("org.lwjgl:lwjgl-egl")
	implementation("org.lwjgl:lwjgl-glfw")
	implementation("org.lwjgl:lwjgl-opengl")
	implementation("org.lwjgl:lwjgl-stb")

	for(natives in listOf(
		"natives-linux", "natives-linux-arm64", "natives-linux-arm32",
		"natives-macos", "natives-macos-arm64",
		"natives-windows", "natives-windows-arm64", "natives-windows-x86"
	)) {
		implementation("org.lwjgl:lwjgl") { artifact { classifier = natives } }
//	implementation("org.lwjgl:lwjgl-freetype") { artifact { classifier = natives } }
		implementation("org.lwjgl:lwjgl-glfw") { artifact { classifier = natives } }
		implementation("org.lwjgl:lwjgl-opengl") { artifact { classifier = natives } }
		implementation("org.lwjgl:lwjgl-stb") { artifact { classifier = natives } }
	}
}