package sim;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) { this.x = x; this.y = y; }

    public Vector2D add(Vector2D o) { return new Vector2D(x + o.x, y + o.y); }
    public Vector2D subtract(Vector2D o) { return new Vector2D(x - o.x, y - o.y); }
    public Vector2D multiply(double s) { return new Vector2D(x * s, y * s); }
    public double dot(Vector2D o) { return x * o.x + y * o.y; }
    public double magnitude() { return Math.sqrt(x * x + y * y); }
    public Vector2D copy() { return new Vector2D(x, y); }
    public String toString() { return "(" + x + "," + y + ")"; }
}
