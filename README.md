<p align="center">
<h3 align="center">Nicotine</h3>

------

<p align="center">
Instances container with automatic annotation-based dependency injection. It is useful for people with basic understanding of java, gradle, workflow and is designed for lazy people
</p>

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-minecraft/Nicotine">
<a href="#Download"><img alt="Sonatype Nexus (Snapshots)" src="https://img.shields.io/nexus/s/dev.ckateptb.minecraft/Nicotine?label=repo&server=https://repo.animecraft.fun/"></a>
<img alt="Publish" src="https://img.shields.io/github/workflow/status/CKATEPTb-minecraft/Nicotine/Publish/production">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-7.5-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://discord.gg/P7FaqjcATp" target="_blank"><img alt="Discord" src="https://img.shields.io/discord/925686623222505482?label=discord"></a>
</p>

------

# Versioning

We use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) to manage our releases.

# Features

- [X] Easy to use
- [X] TableclothContainer implementation
- [X] TableclothEvent implementation
- [X] Automatic listeners registration
- [X] Automatic scheduler registration
- [X] Post constructor
- [X] Documented

# Download

Download from our repository or depend via Gradle:

```kotlin
repositories {
    maven("https://repo.animecraft.fun/repository/maven-snapshots/")
}

dependencies {
    implementation("dev.ckateptb.minecraft:Nicotine:<version>")
}
```

# How To

* Import the dependency [as shown above](#Download)
* Add Nicotine as a dependency to your `plugin.yml`
```yaml
name: ...
version: ...
main: ...
depend: [ Nicotine ]
authors: ...
description: ...
```
* Scan your packages in your plugin's constructor
```java
import dev.ckateptb.common.tableclothcontainer.IoC;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginExample extends JavaPlugin {
    public PluginExample() {
        // Scan component from plugin root classpath
        // You can also specify another package and filter packages
        // Look IoC#scan for mode details
        IoC.scan(PluginExample.class);
    }

    @Override
    public void onLoad() {
        // ...
    }

    @Override
    public void onEnable() {
        // ...
    }
    @Override
    public void onDisable() {
        // ...
    }
}
```
* Create a class and annotate it as `@Component` to automatically instantiate that class 
```java
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;

@Component
public class ComponentExample {
    private final String finalField;
    private String field;
    
    // You can pass other components to the constructor, they will be included automatically
    public ComponentExample() {
        this.finalField = "simple final field";
    }

    // The function will be called immediately after the instance is created
    @PostConstruct
    public void postConstructorExample() {
        this.field = "simple field";
    }

    // Synchronous scheduler example
    // initialDelay and fixedRate specified in ticks
    @Schedule(initialDelay = 0, fixedRate = 20)
    public void scheduleExample() {
        System.out.println("Wow, it's work!");
    }

    // Asynchronous scheduler example
    // initialDelay and fixedRate specified in ticks
    @Schedule(initialDelay = 10, fixedRate = 50, async = true)
    public void asyncScheduleExample() {
        System.out.println("Wow, it's work!");
    }
}
```
* To automatically register event listeners, annotate the class implementing `Listener` as `@Component`
```java
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Component
public class ExampleListener implements Listener {
    @EventHandler
    public void on(PlayerJoinEvent event) {
        // ...
    }
}
```