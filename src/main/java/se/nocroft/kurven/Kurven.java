package se.nocroft.kurven;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

import java.awt.*;

public class Kurven extends AdvancedRobot {

    private static long RADAR_LOCK_TIMEOUT = 10;

    private double lastEnemyEnergy = 100.0f;
    private long radarLockedTime = 0;

    public void run() {
        setColors(Color.WHITE, Color.RED, Color.BLACK, Color.RED, Color.GREEN);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        turnRadarRight(Double.POSITIVE_INFINITY);

        while (getEnergy() > 0) {

            scan();
            execute();
        }

        System.out.println("I died :/");
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        radarLockedTime = e.getTime();
        var targetRadarHeading = getHeadingRadians() + e.getBearingRadians();
        var targetRobotHeading = targetRadarHeading + Math.PI / 2;
        var gunError = Utils.normalRelativeAngle(targetRadarHeading - getGunHeadingRadians());

        setTurnRadarRightRadians(Utils.normalRelativeAngle(targetRadarHeading - getRadarHeadingRadians()));

        // Perpendicular to target for better movement
        setTurnRightRadians(Utils.normalRelativeAngle(targetRobotHeading - getHeadingRadians()));
        setTurnGunRightRadians(gunError);

        if (gunError < Math.toRadians(3)) {
            fire(3);
        }

        // We can assume shot
        if (e.getEnergy() < lastEnemyEnergy) {
            setAhead(Math.random() > 0.5 ? 100 : -100);
        }

        lastEnemyEnergy = e.getEnergy();
    }

    public void onPaint(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(50, 50, 100, 150);
    }
}
