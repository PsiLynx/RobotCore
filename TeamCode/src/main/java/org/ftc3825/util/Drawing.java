package org.ftc3825.util;

import static java.lang.Math.PI;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;

import org.ftc3825.util.geometry.Pose2D;
import org.ftc3825.util.geometry.Vector2D;

import java.util.Vector;

/**
 * This is the Drawing class. It handles the drawing of stuff on FTC Dashboard, like the robot.
 *
 * @author Logan Nash
 * @author Anyi Lin - 10158 Scott's Bots
 * @version 1.0, 4/22/2024
 */
public class Drawing {
    public static final double ROBOT_RADIUS = 9;

    private static TelemetryPacket packet;

    private static void ensurePacketExists() {
        if (packet == null) packet = new TelemetryPacket(false);

        packet.fieldOverlay().setTranslation(0, 0);
        packet.fieldOverlay().setRotation(- PI / 2);
        packet.field().drawImage(
                "/dash/into-the-deep.png",
                0.0,
                0.0,
                144.0,
                144.0,
                PI,
                72.0,
                72.0,
                false
        );
    }

    /**
     * This adds instructions to the current packet to draw a robot at a specified Pose with a specified
     * color. If no packet exists, then a new one is created.
     *
     * @param pose the Pose to draw the robot at
     * @param color the color to draw the robot with
     */
    public static void drawRobot(Pose2D pose, String color) {
        ensurePacketExists();

        packet.fieldOverlay().setStroke(color);
        Drawing.drawRobotOnCanvas(packet.fieldOverlay(), pose);
    }

    /**
     * This tries to send the current packet to FTC Dashboard.
     *
     * @return returns if the operation was successful.
     */
    public static boolean sendPacket() {
        if (packet != null) {
            FtcDashboard.getInstance().sendTelemetryPacket(packet);
            packet = null;
            return true;
        }
        return false;
    }

    /**
     * This draws a robot on the Dashboard at a specified Pose. This is more useful for drawing the
     * actual robot, since the Pose contains the direction the robot is facing as well as its position.
     *
     * @param c the Canvas on the Dashboard on which this will draw
     * @param t the Pose to draw at
     */
    public static void drawRobotOnCanvas(Canvas c, Pose2D t) {
        c.setStrokeWidth(1);
        c.strokeCircle(t.getX(), t.getY(), ROBOT_RADIUS);
        Vector2D v = t.getHeading().times(new Vector2D(0.0, 1.0));
        v.setMag(v.getMag() * ROBOT_RADIUS);
        double x1 = t.getX(), y1 = t.getY();
        double x2 = t.getX() + v.getX(), y2 = t.getY() + v.getY();
        c.strokeLine(x1, y1, x2, y2);
    }

    /**
     * This draws a Path on the Dashboard from a specified Array of Points.
     *
     * @param c the Canvas on the Dashboard on which this will draw
     * @param points the Points to draw
     */
    public static void drawPath(Canvas c, double[][] points) {
        c.strokePolyline(points[0], points[1]);
    }

    public static void drawLine(Double x, Double y, Double theta, String color) {
        packet.fieldOverlay().setStroke(color);
        packet.fieldOverlay().strokeLine(x, y, cos(theta) * ROBOT_RADIUS + x, sin(theta) * ROBOT_RADIUS + y);
    }
    public static void drawPoint(Double x, Double y) {
        packet.fieldOverlay().setStroke("black");
        packet.fieldOverlay().fillCircle(x, y, 2.0);
    }

    public static void drawGVFPath(org.ftc3825.gvf.Path path, boolean active) {
        ensurePacketExists();
        for (int i = 0; i < path.getNumSegments(); i ++){
            double[][] points = new double[2][100];
            for(int t = 0; t < 100; t ++) {
                Vector2D point = path.get(i).point(t/100.0);
                points[0][t] = point.getX();
                points[1][t] = point.getY();
            }
            String color = "yellow";
            if(active) {
                color = "lightpink";
                if (path.getCurrentPath().equals(path.get(i))) {
                    color = "red";
                }
            }
            packet.fieldOverlay().setStroke(color);
            drawPath(packet.fieldOverlay(), points);
        }
    }
    public static void drawPoseHistory(
            org.ftc3825.util.geometry.Pose2D[] poseHistory,
            String color
    ){
        ensurePacketExists();
        double[][] points = new double[2][poseHistory.length];

        for (int i = 0; i < poseHistory.length; i ++){
            points[0][i] = poseHistory[i].getX();
            points[1][i] = poseHistory[i].getY();
        }

        packet.fieldOverlay().setStroke(color);
        drawPath(
            packet.fieldOverlay(),
            points
        );
    }
}
