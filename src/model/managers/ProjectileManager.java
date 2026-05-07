package model.managers;

import model.upgrades.Upgrades;

import java.util.Random;

public class ProjectileManager {
    private static final int MAX_PROJECTILES = 1000;
    private int projectileCount = 0;
    private final double worldWidth;
    private final double worldHeight;
    private UpgradeManager upgradeManager;

    private final double[] posX = new double[MAX_PROJECTILES];
    private final double[] posY = new double[MAX_PROJECTILES];
    private final double[] radius = new double[MAX_PROJECTILES];
    private final double[] velX = new double[MAX_PROJECTILES];
    private final double[] velY = new double[MAX_PROJECTILES];
    private final int[] projectileID = new int[MAX_PROJECTILES];
    private final int[] effectID = new int[MAX_PROJECTILES];
    private final int[] damage = new int[MAX_PROJECTILES];
    private final int[] bounce = new int[MAX_PROJECTILES];
    private final boolean[] isEnemy = new boolean[MAX_PROJECTILES];

    private final double[] fuseTimer = new double[MAX_PROJECTILES];
    private final double[] deceleration = new double[MAX_PROJECTILES];
    private final double[] explosionRadius = new double[MAX_PROJECTILES];

    private final double SPREAD_ANGLE = Math.toRadians(30); // spread of multiple projectiles shot at once
    Random rand = new Random();
    private int lastHitEffectID = 0;

    public ProjectileManager(double worldWidth, double worldHeight, UpgradeManager upgradeManager) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.upgradeManager = upgradeManager;
    }

    public void update(double deltaTime) {
        for (int i = 0; i < projectileCount; i++) {
            if (isGrenade(i)) {
                // Decelerate the grenade
                double speed = Math.sqrt(velX[i] * velX[i] + velY[i] * velY[i]);
                if (speed > 0) {
                    double newSpeed = Math.max(0, speed - deceleration[i] * deltaTime);
                    double scale = newSpeed / speed;
                    velX[i] *= scale;
                    velY[i] *= scale;
                }

                // Count down fuse
                fuseTimer[i] -= deltaTime;

                // Move (even while decelerating)
                posX[i] += velX[i] * deltaTime;
                posY[i] += velY[i] * deltaTime;
            } else {
                // Normal projectile: constant velocity, delete if out of bounds
                posX[i] += velX[i] * deltaTime;
                posY[i] += velY[i] * deltaTime;

                // Bounce logic reflects the velocity off the wall and decrements the bounce counter
                if (posX[i] < 0 || posX[i] > worldWidth) {
                    if (bounce[i] > 0) {
                        velX[i] = -velX[i];
                        if (posX[i] < 0) {
                            posX[i] = 0;
                        } else {
                            posX[i] = worldWidth;
                        }
                        bounce[i]--;
                    } else {
                        deleteProjectile(i--);
                        continue;
                    }
                }
                if (posY[i] < 0 || posY[i] > worldHeight) {
                    if (bounce[i] > 0) {
                        velY[i] = -velY[i];
                        if (posY[i] < 0) {
                            posY[i] = 0;
                        } else {
                            posY[i] = worldHeight;
                        }
                        bounce[i]--;
                    } else deleteProjectile(i--);
                }
            }
        }
    }

    private boolean isInBounds(double x, double y) {
        return (x > 0 && x < worldWidth) && (y > 0 && y < worldHeight);
    }

    public void deleteProjectile(int index) {
        int last = projectileCount - 1;
        posX[index] = posX[last];
        posY[index] = posY[last];
        radius[index] = radius[last];
        velX[index] = velX[last];
        velY[index] = velY[last];
        projectileID[index] = projectileID[last];
        effectID[index] = effectID[last];
        damage[index] = damage[last];
        fuseTimer[index] = fuseTimer[last];
        deceleration[index] = deceleration[last];
        explosionRadius[index] = explosionRadius[last];
        bounce[index] = bounce[last];
        isEnemy[index] = isEnemy[last];
        projectileCount--;
    }

    public void addProjectile(double x, double y, double r,
                               double targetX, double targetY,
                               double speed, int projID, int effID, int dmg, int bounce, boolean enemy) {
        if (projectileCount >= MAX_PROJECTILES) return;

        posX[projectileCount] = x;
        posY[projectileCount] = y;
        radius[projectileCount] = r;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) {
            velX[projectileCount] = speed;
            velY[projectileCount] = 0;
        } else {
            velX[projectileCount] = (dx / dist) * speed;
            velY[projectileCount] = (dy / dist) * speed;
        }

        projectileID[projectileCount] = projID;
        effectID[projectileCount] = effID;
        damage[projectileCount] = dmg;
        fuseTimer[projectileCount] = 0;
        deceleration[projectileCount] = 0;
        explosionRadius[projectileCount] = 0;
        this.bounce[projectileCount] = bounce;
        isEnemy[projectileCount] = enemy;
        projectileCount++;
    }

    public void addProjectiles(double x, double y, double r,
                               double targetX, double targetY,
                               double speed, int projID, int effID, int dmg, int amountOfProjectiles, int bounce, boolean isEnemy) {
        if (projectileCount + amountOfProjectiles >= MAX_PROJECTILES) return;

        // below contains logic for creating a spread of projectiles
        // where the first projectile starts at the farthest left
        // of the spread and then it fills with X amount of projectiles to the end of the spread range
        double centerAngle = Math.atan2(targetY - y, targetX - x);
        double randomSpreadAngle = SPREAD_ANGLE + Math.toRadians(-10 + rand.nextDouble() * 20);
        for (int i = 0; i < amountOfProjectiles; i++) {
            double angle = centerAngle - randomSpreadAngle / 2 + i * (randomSpreadAngle / (amountOfProjectiles - 1));
            double targetXOffset = x + Math.cos(angle);
            double targetYOffset = y + Math.sin(angle);
            addProjectile(x, y, r, targetXOffset, targetYOffset, speed, projID, effID, dmg, bounce, isEnemy);
        }
    }

    public void addGrenade(double x, double y, double r,
                            double targetX, double targetY,
                            double speed, int projID, int effID, int dmg,
                            double fuseTime, double explRadius, int bounce, boolean isEnemy) {
        if (projectileCount >= MAX_PROJECTILES) return;

        posX[projectileCount] = x;
        posY[projectileCount] = y;
        radius[projectileCount] = r;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        // Adjust speed so the grenade lands at the target position
        double adjustedSpeed = (dist == 0) ? 0 : (2 * dist / fuseTime);

        if (dist == 0) {
            velX[projectileCount] = 0;
            velY[projectileCount] = 0;
        } else {
            velX[projectileCount] = (dx / dist) * adjustedSpeed;
            velY[projectileCount] = (dy / dist) * adjustedSpeed;
        }

        projectileID[projectileCount] = projID;
        effectID[projectileCount] = effID;
        damage[projectileCount] = dmg;
        this.fuseTimer[projectileCount] = fuseTime;
        this.deceleration[projectileCount] = adjustedSpeed / fuseTime;
        this.explosionRadius[projectileCount] = explRadius;
        this.bounce[projectileCount] = bounce;
        this.isEnemy[projectileCount] = isEnemy;
        projectileCount++;
    }

    public int checkPlayerHit(double playerX, double playerY, double playerRadius) {
        for (int i = 0; i < projectileCount; i++) {
            if (!isEnemy[i]) continue;
            double dx = posX[i] - playerX;
            double dy = posY[i] - playerY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < radius[i] + playerRadius) {
                int dmg = damage[i];
                lastHitEffectID = effectID[i];
                deleteProjectile(i);
                return dmg;
            }
        }
        return 0;
    }

    public void clear() {
        projectileCount = 0;
    }

    public int getCount() { return projectileCount; }
    public double getX(int i) { return posX[i]; }
    public double getY(int i) { return posY[i]; }
    public double getRadius(int i) { return radius[i]; }
    public double getVelX(int i) { return velX[i]; }
    public double getVelY(int i) { return velY[i]; }
    public int getTextureID(int i) { return projectileID[i]; }
    public int getDamage(int i) { return damage[i]; }
    public boolean isGrenade(int i) { return projectileID[i] == 3; }
    public double getFuseTimer(int i) { return fuseTimer[i]; }
    public double getExplosionRadius(int i) { return explosionRadius[i]; }
    public boolean getIsEnemy(int i) { return isEnemy[i]; }
    public int getLastHitEffectID() { return lastHitEffectID; }
}
