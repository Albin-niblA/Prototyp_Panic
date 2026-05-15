package model.entities;

import model.upgrades.Upgrades;
import model.managers.UpgradeManager;
import java.util.EnumMap;

public class Player extends Entity {
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private int moveDir = 0; // 0=front, 1=back, 2=left, 3=right, 4=frontLeft, 5=frontRight, 6=backLeft, 7=backRight
    private int level = 0;
    private int xp = 0;
    private double flatMoveSpeed = movementSpeed;
    private double flatHealth;
    private int healthRegen = 0;
    private int bonusDamage = 0;
    private final double REGEN_TIMER_DURATION = 0.5;
    private double healthRegenTimer = REGEN_TIMER_DURATION;
    private double freezeTimer = 0;

    private UpgradeManager upgradeManager = new UpgradeManager(this);



    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.maxHealth = 100;
        this.health = 100;
        flatHealth = maxHealth;
        this.contactDamage = 0;
    }

    public void setMoving(boolean up, boolean down, boolean left, boolean right) {
        this.moveUp = up;
        this.moveDown = down;
        this.moveLeft = left;
        this.moveRight = right;
    }

    public void update(double deltaTime, double mapWidth, double mapHeight) {
        if (freezeTimer > 0) freezeTimer -= deltaTime;

        if (freezeTimer <= 0) {
            double dx = 0;
            double dy = 0;

            if      ( moveDown && !moveLeft && !moveRight) moveDir = 0;
            else if ( moveUp   && !moveLeft && !moveRight) moveDir = 1;
            else if ( moveLeft && !moveUp   && !moveDown)  moveDir = 2;
            else if ( moveRight&& !moveUp   && !moveDown)  moveDir = 3;
            else if ( moveDown &&  moveLeft)               moveDir = 4;
            else if ( moveDown)                            moveDir = 5;
            else if ( moveUp   &&  moveLeft)               moveDir = 6;
            else if ( moveUp)                              moveDir = 7;

            if (moveUp)    dy -= 1;
            if (moveDown)  dy += 1;
            if (moveLeft)  dx -= 1;
            if (moveRight) dx += 1;

            if (dx != 0 && dy != 0) {
                double factor = 1.0 / Math.sqrt(2);
                dx *= factor;
                dy *= factor;
            }

            x += dx * movementSpeed * deltaTime;
            y += dy * movementSpeed * deltaTime;

            x = Math.max(size / 2, Math.min(mapWidth - size / 2, x));
            y = Math.max(size / 2, Math.min(mapHeight - size / 2, y));
        }

        if (damageCooldown > 0)    damageCooldown -= deltaTime;
        if (healthRegenTimer <= 0) {
            if (health + healthRegen <= maxHealth) {
                health += healthRegen;
            }
            healthRegenTimer = REGEN_TIMER_DURATION;
        }
        healthRegenTimer -= deltaTime;
        upgradeManager.update(deltaTime);
    }

    public int getMoveDir()                 { return moveDir; }

    public boolean addXp(int xp) {
        this.xp += xp;
        int required = getXpRequired();
        if (this.xp >= required) {
            this.xp -= required;
            level++;
            return true;
        }
        return false;
    }

    public void reset(double startX, double startY) {
        x = startX;
        y = startY;
        health = maxHealth;
        damageCooldown = 0;
        freezeTimer = 0;
        dead = false;
        moveUp = moveDown = moveLeft = moveRight = false;
    }

    public int getXp() {
        return xp;
    }

    public int getXpRequired() {
        return (int) (100 * Math.pow(1.1, level));
    }

    public int getLevel() {
        return level;
    }

    public double getFirerateMultiplier() {
        return upgradeManager.getFirerateMultiplier();
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public double getFlatMoveSpeed() {
        return flatMoveSpeed;
    }

    public double getFlatHealth() {
        return flatHealth;
    }

    public void setMaxHealth(int health) {
        maxHealth = health;
        this.health = health;
    }

    public void addHealth(int health) {
        if (this.health + health <= maxHealth) {
            this.health += health;
        }
    }

    public void setHealthRegen(int healthRegen) {
        this.healthRegen = healthRegen;
    }

    public void applyFreeze(double duration) {
        freezeTimer = duration;
    }

    public boolean isFrozen() {
        return freezeTimer > 0;
    }

    public double getFreezeTimer() {
        return freezeTimer;
    }

    public void addBonusDamage(int damage) { bonusDamage += damage; }
}