# LibSLParser

![h31](https://circleci.com/gh/h31/LibSLParser.svg?style=shield)
[![](https://jitpack.io/v/h31/LibSLParser.svg)](https://jitpack.io/#h31/LibSLParser)

LibSLParser library allows to parse, construct, modify and pretty print library models written in LibSL language. More information about LibSL can be found in the article
```
Itsykson V. M. LibSL: Language for Specification of Software Libraries, Programmnaya Ingeneria, 2018, vol. 9, no. 5, pp. 209—220.
```
or in papers [1](https://link.springer.com/article/10.3103/S0146411618070027), [2](https://link.springer.com/chapter/10.1007/978-3-030-57663-9_23).

### Maven
```
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
...
<dependency>
    <groupId>com.github.h31.LibSLParser</groupId>
    <artifactId>libslparser-core</artifactId>
    <version>1.1</version>
</dependency>
```

### Gradle
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
...
dependencies {
  implementation 'com.github.h31.LibSLParser:libslparser-core:1.1'
}
```

### Usage Examples

* [Kotlin example](https://github.com/h31/LibSLParser/blob/master/libslparser-tests/src/test/kotlin/ru/spbstu/insys/libsl/parser/test/ParserTest.kt#L22)
* [Java example](https://github.com/h31/LibSLParser/blob/master/libslparser-tests/src/test/java/ru/spbstu/insys/libsl/parser/test/ParserJavaUsageTest.java#L23)

### Project Structure

The main submodule is `libslparser-core`. `libslparser-edgemodel` contains some legacy code for model analysis, you probably wouldn't need it. `libslparser-tests` is for tests.
