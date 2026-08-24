package sim;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import java.text.DecimalFormat;

public class SimulationPanel extends JPanel {
    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    private final Rocket rocket;
    private final DockingPort dock;
    private final Timer timer;
    private boolean thrustOn = false;
    private int rotateDir = 0; // -1 left, 1 right

    private double timeScale = 1.0;

    private final DecimalFormat fmt = new DecimalFormat("#0.00");

    public SimulationPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));

        rocket = new Rocket(new Vector2D(100, HEIGHT / 2.0), 0.0);
        dock = new DockingPort(new Vector2D(WIDTH - 150, HEIGHT / 2.0), 24);

        // Key bindings
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "thrustOn");
        getActionMap().put("thrustOn", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { thrustOn = true; }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "thrustOff");
        getActionMap().put("thrustOff", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { thrustOn = false; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "rotateLeft");
        getActionMap().put("rotateLeft", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rotateDir = -1; }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "rotateStop");
        getActionMap().put("rotateStop", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { if (rotateDir == -1) rotateDir = 0; }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rotateRight");
        getActionMap().put("rotateRight", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { rotateDir = 1; }
        });
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rotateStop2");
        getActionMap().put("rotateStop2", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { if (rotateDir == 1) rotateDir = 0; }
        });

        // Reset with R
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "reset");
        getActionMap().put("reset", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                rocket.setPosition(new Vector2D(100, HEIGHT / 2.0));
                rocket.setVelocity(new Vector2D(0, 0));
                rocket.setAngle(0);
            }
        });

        timer = new Timer(20, e -> {
            updatePhysics(0.02 * timeScale);
            repaint();
        });
        timer.start();
    }

    private void updatePhysics(double dt) {
        if (rotateDir != 0) {
            rocket.rotate(rotateDir * 90 * dt); // 90 deg/sec rotation speed
        }
        if (thrustOn) {
            rocket.applyThrust(80 * dt); // acceleration units
        }
        rocket.update(dt);

        // Boundaries wrap-around (simple)
        Vector2D p = rocket.getPosition();
        if (p.x < 0) p.x = WIDTH;
        if (p.x > WIDTH) p.x = 0;
        if (p.y < 0) p.y = HEIGHT;
        if (p.y > HEIGHT) p.y = 0;

        // Docking detection
        if (!rocket.isDocked() && isDocked()) {
            rocket.setDocked(true);
        }
    }

    private boolean isDocked() {
        double dist = rocket.getPosition().subtract(dock.position).magnitude();
        double speed = rocket.getVelocity().magnitude();
        double angleDiff = Math.abs(normalizeAngle(rocket.getAngle() - dock.facingAngle));
        angleDiff = Math.min(angleDiff, Math.PI * 2 - angleDiff);
        return dist <= dock.radius + 8 && speed < 16 && angleDiff < Math.toRadians(20);
    }

    private double normalizeAngle(double a) {
        while (a <= -Math.PI) a += 2 * Math.PI;
        while (a > Math.PI) a -= 2 * Math.PI;
        return a;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw docking port
        g.setColor(new Color(60, 200, 60));
        int dr = dock.radius * 2;
        g.fillOval((int) (dock.position.x - dock.radius), (int) (dock.position.y - dock.radius), dr, dr);
        // docking facing line
        g.setColor(Color.white);
        Vector2D face = new Vector2D(Math.cos(dock.facingAngle), Math.sin(dock.facingAngle)).multiply(dock.radius + 10);
        g.drawLine((int) dock.position.x, (int) dock.position.y, (int) (dock.position.x + face.x), (int) (dock.position.y + face.y));

        // Draw rocket
        drawRocket(g);

        // HUD
        g.setColor(Color.white);
        Vector2D pos = rocket.getPosition();
        g.drawString("Pos: " + fmt.format(pos.x) + ", " + fmt.format(pos.y), 10, 18);
        g.drawString("Vel: " + fmt.format(rocket.getVelocity().magnitude()), 10, 36);
        g.drawString("Angle: " + fmt.format(Math.toDegrees(rocket.getAngle())) + "°", 10, 54);
        g.drawString("Thrust: " + (thrustOn ? "ON" : "OFF") + "   Rotate: " + (rotateDir == -1 ? "Left" : rotateDir == 1 ? "Right" : "None"), 10, 72);

        String status = rocket.isDocked() ? "DOCKED! Press R to reset." : "Approach and match speed/angle to dock.";
        g.drawString(status, 10, 96);
    }

    private void drawRocket(Graphics2D g) {
        Vector2D p = rocket.getPosition();
        double angle = rocket.getAngle();

        // Triangle shape points
        int size = 14;
        Vector2D nose = new Vector2D(Math.cos(angle), Math.sin(angle)).multiply(size).add(p);
        Vector2D left = new Vector2D(Math.cos(angle + 2.3), Math.sin(angle + 2.3)).multiply(size * 0.8).add(p);
        Vector2D right = new Vector2D(Math.cos(angle - 2.3), Math.sin(angle - 2.3)).multiply(size * 0.8).add(p);

        int[] xs = {(int) nose.x, (int) left.x, (int) right.x};
        int[] ys = {(int) nose.y, (int) left.y, (int) right.y};

        g.setColor(Color.cyan);
        g.fillPolygon(xs, ys, 3);

        // Thrust flame
        if (thrustOn && !rocket.isDocked()) {
            Vector2D back = new Vector2D(Math.cos(angle + Math.PI), Math.sin(angle + Math.PI)).multiply(size * 1.2).add(p);
            int fx = (int) back.x;
            int fy = (int) back.y;
            g.setColor(Color.orange);
            g.fillOval(fx - 6, fy - 6, 12, 12);
        }
    }
}
