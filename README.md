# LLD Projects - Simple Java Configuration Guide

This workspace contains two separate Low Level Design (LLD) projects that are simple Java applications configured to run with **Java 21**.

## Prerequisites

- **Java 21** installed on your system
- Terminal/Command line access

## Projects

1. **Coffee Machine** (`CoffeeMachine/`) - Coffee Vending Machine with Design Patterns
2. **Spotify** (`Spotify/`) - Music Streaming System with Design Patterns

## Running the Projects

### Quick Start

#### Coffee Machine Project
```bash
cd CoffeeMachine
bash build.sh    # Compile
bash run.sh      # Run
```

#### Spotify Project
```bash
cd Spotify
bash build.sh    # Compile
bash run.sh      # Run
```

### Manual Compilation (without scripts)

#### Coffee Machine
```bash
cd CoffeeMachine
mkdir -p bin
javac -d bin -source 21 -target 21 $(find . -name "*.java")
cd bin
java CoffeeVendingMachineDemo
```

#### Spotify
```bash
cd Spotify
mkdir -p bin
javac -d bin -source 21 -target 21 $(find src -name "*.java")
cd bin
java MusicStreamingDemo
```

## Project Structure

### CoffeeMachine
- `CoffeeVendingMachineDemo.java` - Main entry point
- `decorator/` - Decorator pattern implementation
- `enums/` - Enum types
- `facade/` - Facade pattern
- `factory/` - Factory pattern
- `singleton/` - Singleton pattern
- `state/` - State pattern

### Spotify
- `src/MusicStreamingDemo.java` - Main entry point
- `src/commands/` - Command pattern
- `src/enums/` - Enum types
- `src/models/` - Data models
- `src/observer/` - Observer pattern
- `src/recommendations/` - Strategy pattern
- `src/services/` - Service classes
- `src/states/` - State pattern
- `src/strategies/` - Strategy pattern

## Java 21 Configuration

Both projects use **Java 21** with the following flags:
- `-source 21` - Source code compatibility
- `-target 21` - Target bytecode version

## Troubleshooting

### Java 21 not found
```bash
# Check Java version
java -version

# If Java 21 not available, install it or set JAVA_HOME
export JAVA_HOME=/path/to/java21
export PATH=$JAVA_HOME/bin:$PATH
```

### IntelliJ IDEA Setup
1. File → Project Structure → Project
2. Set SDK to Java 21
3. Set Language level to 21
4. Mark each project root as "Sources Root"
   - `CoffeeMachine/` as Sources Root
   - `Spotify/src/` as Sources Root

### Compilation Errors
- Ensure Java 21 is installed: `java -version`
- Check that all Java files are in correct directories
- For Spotify project, ensure source files are in `src/` directory

## Building JAR Files

To create executable JAR files:

```bash
# For Coffee Machine
cd CoffeeMachine
mkdir -p bin
javac -d bin -source 21 -target 21 $(find . -name "*.java")
cd bin
jar cfe ../coffee-machine.jar CoffeeVendingMachineDemo *
cd ..

# For Spotify
cd Spotify
mkdir -p bin
javac -d bin -source 21 -target 21 $(find src -name "*.java")
cd bin
jar cfe ../music-streaming.jar MusicStreamingDemo *
cd ..

# Run JAR files
java -jar CoffeeMachine/coffee-machine.jar
java -jar Spotify/music-streaming.jar
```

## Notes

- Each project is completely independent
- Both compile and run with Java 21
- No external dependencies required
- Source files are organized by design pattern packages
