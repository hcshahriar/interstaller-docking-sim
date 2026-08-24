package sim;

public class Rocket {
    private Vector2D position;
    private Vector2D velocity;
    private double angle; // radians
    private boolean docked = false;

    public Rocket(Vector2D pos, double angle) {
        this.position = pos.copy();
        this.velocity = new Vector2D(0, 0);
        this.angle = angle;
    }

    public void applyThrust(double accel) {
        // accel is linear acceleration magnitude per second
        Vector2D a = new Vector2D(Math.cos(angle), Math.sin(angle)).multiply(accel);
        // apply instantaneous velocity change per physics step in SimulationPanel
        this.velocity = this.velocity.add(a);
    }

    public void rotate(double degrees) {
        this.angle += Math.toRadians(degrees);
    }

    public void update(double dt) {
        if (docked) {
            // when docked, stop movement
            velocity = new Vector2D(0, 0);
            return;
        }
        // simple Euler integration
        this.position = this.position.add(this.velocity.multiply(dt));
        // small damping to prevent runaway from iterative thrust
        this.velocity = this.velocity.multiply(0.999);
    }

    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public double getAngle() { return angle; }

    public void setPosition(Vector2D p) { this.position = p.copy(); }
    public void setVelocity(Vector2D v) { this.velocity = v.copy(); }
    public void setAngle(double a) { this.angle = a; }
    public boolean isDocked() { return docked; }
    public void setDocked(boolean d) { this.docked = d; }
}
