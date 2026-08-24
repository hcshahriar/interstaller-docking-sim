# Interstaller Docking Simulator (Java)

Simple 2D rocket docking simulation using Java Swing.

Controls:
- Up arrow: Thrust forward
- Left/Right arrows: Rotate
- R: Reset position

How to compile & run (no build tools required):

1. Place the files under `src/sim/` as shown.
2. From the project root run:

```bash
mkdir -p out
javac -d out src/sim/*.java
java -cp out sim.Main
```

Notes & ideas to extend:
- Add stars/background, fuel consumption, or multiple docking ports.
- Add a simple scoring system (fuel used, time).
- Replace Swing with JavaFX for smoother graphics.
- Add obstacles and multiple ships.

Enjoy! If you want, I can:
- Package this as a Maven or Gradle project.
- Push it to a GitHub repository under your account (I'll need repo name/permission).
- Add features like fuel, collision, or a replay system.
