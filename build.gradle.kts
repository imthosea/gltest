plugins {
	java
	application

	alias(libs.plugins.shadow)
	alias(libs.plugins.lombok)

	id("buildSrc-lwjgl")

	idea
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(libs.joml)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)
application {
	mainClass = "me.thosea.gltest.bootstrap.Bootstrap"

	if(System.getProperty("os.name").contains("mac", ignoreCase = true)) {
		applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
	}
}

sourceSets.main {
	java.setSrcDirs(listOf("src"))
	resources.setSrcDirs(listOf("resources"))
}

tasks.jar { archiveClassifier = "no-deps" }
tasks.shadowJar {
	minimize {
		exclude(dependency("org.lwjgl:lwjgl-egl"))
	}
	archiveClassifier = ""

	val archiveFile = tasks.jar.get().archiveFile
	doLast { archiveFile.get().asFile.delete() }
}

tasks.distZip { enabled = false }
tasks.distTar { enabled = false }
tasks.shadowDistTar { enabled = false }
tasks.shadowDistZip { enabled = false }

idea.module.excludeDirs.addAll(listOf(
	".gradle", ".idea",
	".kotlin",
).map(::file))