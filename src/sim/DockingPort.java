package sim;

public class DockingPort {
    public final Vector2D position;
    public final int radius;
    // facingAngle is the direction the docking port expects the rocket to face (radians)
    public final double facingAngle = Math.PI; // facing left

    public DockingPort(Vector2D pos, int radius) {
        this.position = pos.copy();
        this.radius = radius;
    }
}
