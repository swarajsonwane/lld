# How to Add New Projects to LLD Workspace

This guide explains how to add new Low Level Design (LLD) projects to this workspace.

## Files NOT Required in Git (Ignored)

The `.gitignore` file excludes the following from version control:

### IDE & Build Artifacts
- **`.idea/`** - IntelliJ IDEA configuration
- **`*.iml`** - IntelliJ module files (can be regenerated)
- **`out/`** - Compilation output directory
- **`bin/`** - Compiled bytecode (any project)
- **`target/`** - Maven build artifacts (if using Maven)
- **`build/`** - Gradle build artifacts (if using Gradle)

### Compiled Files
- **`*.class`** - Compiled Java bytecode
- **`*.jar`** - JAR archives

### OS Files
- **`.DS_Store`** - macOS metadata
- **`Thumbs.db`** - Windows thumbnails

### Other
- **`*.log`** - Log files
- **`*.tmp`** - Temporary files

---

## Step-by-Step: Adding a New Project

### Step 1: Create Project Directory Structure

```bash
cd /Users/swarajsonwane/Documents/LLD

# For a project without src/ subdirectory (like CoffeeMachine)
mkdir -p NewProject

# For a project with src/ subdirectory (like Spotify)
mkdir -p NewProject/src
```

### Step 2: Organize Source Files

```bash
# Copy or create your Java files
# For projects without src/:
cp your_files/*.java NewProject/

# For projects with src/:
cp your_files/*.java NewProject/src/
```

### Step 3: Create `.iml` File

Create `NewProject/NewProject.iml`:

**If your source files are in root directory:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
```

**If your source files are in `src/` directory:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
```

### Step 4: Create Build Scripts

**Create `NewProject/build.sh`:**

For projects without src/:
```bash
#!/bin/bash

echo "=== Building NewProject ==="
echo "Java version:"
java -version

mkdir -p bin
echo ""
echo "Compiling Java files..."
javac -d bin -source 21 -target 21 $(find . -name "*.java" -not -path "./bin/*")

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
else
    echo "✗ Compilation failed!"
    exit 1
fi
```

For projects with src/:
```bash
#!/bin/bash

echo "=== Building NewProject ==="
echo "Java version:"
java -version

mkdir -p bin
echo ""
echo "Compiling Java files..."
javac -d bin -source 21 -target 21 $(find src -name "*.java")

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
else
    echo "✗ Compilation failed!"
    exit 1
fi
```

**Create `NewProject/run.sh`:**

Replace `YourMainClass` with your actual main class name:
```bash
#!/bin/bash

if [ ! -d "bin" ]; then
    echo "Classes not found. Building first..."
    bash build.sh
fi

echo "=== Running NewProject Demo ==="
cd bin
java -cp . YourMainClass
cd ..
```

### Step 5: Make Scripts Executable

```bash
chmod +x NewProject/build.sh NewProject/run.sh
```

### Step 6: Update IntelliJ Configuration

Update `.idea/modules.xml` to include the new project:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/CoffeeMachine/CoffeeMachine.iml" filepath="$PROJECT_DIR$/CoffeeMachine/CoffeeMachine.iml" />
      <module fileurl="file://$PROJECT_DIR$/Spotify/Spotify.iml" filepath="$PROJECT_DIR$/Spotify/Spotify.iml" />
      <module fileurl="file://$PROJECT_DIR$/NewProject/NewProject.iml" filepath="$PROJECT_DIR$/NewProject/NewProject.iml" />
    </modules>
  </component>
</project>
```

### Step 7: Test Build and Run

```bash
cd NewProject
bash build.sh    # Should compile successfully
bash run.sh      # Should run your main class
```

### Step 8: Commit to Git

```bash
cd /Users/swarajsonwane/Documents/LLD

# Add all source files
git add NewProject/

# Add updated configuration
git add .idea/modules.xml

# Commit
git commit -m "Add NewProject LLD demonstration"
```

---

## Project Structure Templates

### Template 1: Simple Project (no src/ folder)

```
MyProject/
├── MyProject.iml
├── build.sh
├── run.sh
├── Main.java
├── pattern1/
│   ├── Class1.java
│   └── Class2.java
└── pattern2/
    ├── Class3.java
    └── Class4.java
```

### Template 2: Organized Project (with src/ folder)

```
MyProject/
├── MyProject.iml
├── build.sh
├── run.sh
└── src/
    ├── Main.java
    ├── pattern1/
    │   ├── Class1.java
    │   └── Class2.java
    └── pattern2/
        ├── Class3.java
        └── Class4.java
```

---

## Example: Adding ElevatorSystem Project

```bash
# 1. Create structure
mkdir -p ElevatorSystem/src

# 2. Add your Java files to src/

# 3. Create ElevatorSystem.iml
cat > ElevatorSystem/ElevatorSystem.iml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" inherit-compiler-output="true">
    <exclude-output />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src" isTestSource="false" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
EOF

# 4. Create build.sh and run.sh (see templates above)

# 5. Make executable
chmod +x ElevatorSystem/build.sh ElevatorSystem/run.sh

# 6. Update .idea/modules.xml

# 7. Test
cd ElevatorSystem
bash build.sh
bash run.sh

# 8. Git commit
cd ..
git add ElevatorSystem/ .idea/modules.xml
git commit -m "Add ElevatorSystem project"
```

---

## Quick Checklist for New Projects

- [ ] Project directory created
- [ ] Source files placed in correct location
- [ ] `.iml` file created with correct sourceFolder path
- [ ] `build.sh` script created and customized
- [ ] `run.sh` script created with correct main class name
- [ ] Scripts made executable (`chmod +x`)
- [ ] `.idea/modules.xml` updated
- [ ] Project builds successfully (`bash build.sh`)
- [ ] Project runs successfully (`bash run.sh`)
- [ ] Files committed to git

---

## Common Issues

### "javac: command not found"
- Ensure Java 21 is installed
- Set `JAVA_HOME` to Java 21 installation

### Main class not found when running
- Check main class name in `run.sh` matches actual class
- Ensure `bin/` directory contains compiled `.class` files

### IntelliJ doesn't recognize project
- Refresh IntelliJ (File → Invalidate Caches)
- Ensure `.iml` file is in project root
- Check `.idea/modules.xml` includes your project

### Git showing unwanted files
- Files in `.gitignore` are only ignored for new additions
- To ignore already tracked files: `git rm --cached <file>`
- Then commit the removal

