plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.gepron1x"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven {
        url = uri("https://repo.decalium.ru/releases")
    }
    maven{ url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":npc-api"))
    compileOnly("org.jetbrains:annotations:23.0.0")
    compileOnly("net.pl3x.purpur:purpur:1.16.5-R0.1-SNAPSHOT")
    implementation("ru.xezard:XGlowAPI:1.1.0") {
        exclude("com.destroystokyo.paper", "paper-api")
    }
    implementation("cloud.commandframework:cloud-paper:1.8.0") {
        exclude("org.checkerframework", "checker-qual")
    }
    implementation("org.spongepowered:configurate-yaml:4.1.2") {
        exclude("org.yaml", "snakeyaml")
    }
    implementation("com.manya:persistent-data-types:1.0.25")
    implementation("org.jooq:joor-java-8:0.9.14")
}



tasks {
    shadowJar {
        relocate("cloud.commandframework", "org.gepron1x.npc.libraries.cloud.commandframework")
        relocate("org.spongepowered.configurate", "org.gepron1x.npc.libraries.configurate")
        relocate("io.leangen.geantyref", "org.gepron1x.npc.libraries.io.leangen.geantyref")
        relocate("com.manya", "org.gepron1x.npc.libraries.pdc")
        relocate("org.joor", "org.gepron1x.npc.libraries.joor")
        relocate("ru.xezard.glow", "org.gepron1x.npc.libraries.glow")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.16.5")
        jvmArgs("-Xms128M", "-Xmx512M")
    }
}

bukkit {
    name = "DecaliumNpc"
    main = "org.gepron1x.npc.plugin.DecaliumNpc"
    description = "The genially simple npc plugin"
    apiVersion = "1.16"
    authors = listOf("gepron1x")
    softDepend = listOf("PlaceholderAPI", "ProtocolLib")
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
}
