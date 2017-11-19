This is a port of the ["Your second WebExtension"](https://developer.mozilla.org/en-US/Add-ons/WebExtensions/Your_second_WebExtension) for Firefox to Kotlin.

## Requirements

- JDK 8
- node.js
- 

## Build instructions

```
./gradlew runDceKotlinJs --continuous
```

In a separate terminal

```
web-ext run
```

## Working in an IDE

Open IntelliJ IDEA and import the project as a Gradle project.

Create a run configuration for the Gradle task `runDceKotlinJs` with the arguments `--continuous`. Execute the run configuration for continuous building of the Kotlin code.

Run `web-ext run` in a terminal to launch a Firefox instance with the extension installed.