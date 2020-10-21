# Quiver
A resource pack creator and manager for Minecraft.

## Downloading
You can find pre-built versions of Quiver [here](https://github.com/deflatedpickle/quiver/releases).
The most recent build will be labeled as such. Any of the older builds *will* ***not*** be supported, especially v0.\*.\*, so please stay up-to-date.

## Running
You will first need to download a version *(hopefully the latest)* of Quiver *(it should be a zip)*, you will then need to unzip it and then run either `Quiver.bat` or `Quiver` *(depending on if you're on Windows or Linux/MacOS)*

This program requires a Java 8 JRE. It is not compatible with anything higher, as I'm old and lazy.

If everything works, great! If not, please submit an issue [here](https://github.com/DeflatedPickle/Quiver/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc).

## Building
In order to build this program, you will need a Java 8 JDK

- [Windows](https://github.com/ojdkbuild/ojdkbuild/releases/tag/java-1.8.0-openjdk-debug-1.8.0.265-1.b01)
- [Linux](http://openjdk.java.net/install/)

You will then want to clone this repository into a directory, `cd` into it and run `gradlew distZip`

## Extending
This program uses the [haruhi](https://github.com/DeflatedPickle/haruhi) framework, allowing it to have plugins and configs.

Though there isn't an official way to develop external plugins, there are two routes I can think of;

1. Clone this repository and develop your plugin as a submodule
    - **Pro:** Easier to debug
    - **Con:** Harder to distribute
2. Develop your plugin in its own repository, then build it and place the build in your install of Quiver's `/plugins` directory when you want to test it
    - **Pro:** Doesn't require the Quiver source
    - **Con:** Harder to test
