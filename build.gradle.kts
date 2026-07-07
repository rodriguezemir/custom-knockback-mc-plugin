import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.snom.SpotBugsTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    id("com.gradleup.shadow") version "9.5.1"
    java
}

group = "site.zvolcan.customkb"

fun getTime(): String {
    val sdf = SimpleDateFormat("yyMMdd-HHmm")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

version = (if (!hasProperty("ver")) {
    "${getTime()}-SNAPSHOT"
} else {
    val ver = property("ver") as String
    val base = if (ver.startsWith("v")) ver.drop(1) else ver.replace('/', '-')
    if (ver.startsWith("v") && !ver.lowercase().contains("-rc-")) base else "$base-SNAPSHOT"
}).uppercase()

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeModule("io.papermc.paper", "paper-api")
            includeModule("net.md-5", "bungeecord-chat")
            includeGroup("io.papermc.adventure")
        }
    }

    maven {
        name = "minecraft"
        url = uri("https://libraries.minecraft.net")
        content {
            includeModule("com.mojang", "brigadier")
        }
    }

    mavenCentral()

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.CrimsonWarpedcraft")
        }
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.72-stable")
}

tasks.processResources {
    filesMatching("**/paper-plugin.yml") {
        expand(mapOf("NAME" to rootProject.name, "VERSION" to version, "PACKAGE" to project.group))
    }
}

val shadowJar = tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    mergeServiceFiles()
    relocate("dev.jorel.commandapi", "${project.group}.commandapi")
    relocate("com.fasterxml", "${project.group}.fasterxml")
    relocate("org.yaml.snakeyaml", "${project.group}.snakeyaml")
    relocate("org.hibernate.validator", "${project.group}.hibernatevalidator")
    relocate("jakarta.validation", "${project.group}.jakartavalidation")
    relocate("org.jboss.logging", "${project.group}.jbosslogging")
    // These libs load classes via reflection or SPI and must not be minimized
    minimize {
        exclude(dependency("dev.jorel:commandapi-paper-shade:.*"))
        exclude(dependency("com.fasterxml.jackson.core:.*:.*"))
        exclude(dependency("com.fasterxml.jackson.dataformat:.*:.*"))
        exclude(dependency("com.fasterxml:classmate:.*"))
        exclude(dependency("org.hibernate.validator:.*:.*"))
        exclude(dependency("jakarta.validation:.*:.*"))
        exclude(dependency("org.yaml:snakeyaml:.*"))
        exclude(dependency("org.jboss.logging:.*:.*"))
        // cw-commons bundles the SQLite JDBC driver (loaded via SPI) inside its own jar;
        // it never appears as a separate resolvable dependency, so it must be excluded by name.
        exclude(dependency("com.github.CrimsonWarpedcraft:cw-commons:.*"))
    }
}

tasks.jar {
    enabled = false
}

tasks.assemble {
    dependsOn(shadowJar)
}

tasks.register("printProjectName") {
    doLast {
        println(rootProject.name)
    }
}

tasks.register("release") {
    dependsOn("build")

    doLast {
        if (!version.toString().endsWith("-SNAPSHOT")) {
            val releaseJar = layout.buildDirectory.file("libs/${rootProject.name}.jar").get().asFile
            shadowJar.get().archiveFile.get().asFile.renameTo(releaseJar)
        }
    }
}
