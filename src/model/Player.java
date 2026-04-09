package model;

public class Player extends Entity {
    private boolean moveUp;
    private boolean moveDown;
    private boolean moveLeft;
    private boolean moveRight;
    private int moveDir = 0; // 0=front, 1=back, 2=left, 3=right

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.maxHealth = 100;
        this.health = 100;
        this.contactDamage = 0;
    }

    public void setMoving(boolean up, boolean down, boolean left, boolean right) {
        this.moveUp = up;
        this.moveDown = down;
        this.moveLeft = left;
        this.moveRight = right;
    }

    public void update(double deltaTime, double mapWidth, double mapHeight) {
        double dx = 0;
        double dy = 0;

        if (moveUp) {
            dy -= 1;
            moveDir = 1;
        }
        if (moveDown) {
            dy += 1;
            moveDir = 0;
        }
        if (moveLeft) {
            dx -= 1;
            moveDir = 2;
        }
        if (moveRight) {
            dx += 1;
            moveDir = 3;
        }

        if (dx != 0 && dy != 0) {
            double factor = 1.0 / Math.sqrt(2);
            dx *= factor;
            dy *= factor;
        }

        x += dx * movementSpeed * deltaTime;
        y += dy * movementSpeed * deltaTime;

        x = Math.max(size / 2, Math.min(mapWidth - size / 2, x));
        y = Math.max(size / 2, Math.min(mapHeight - size / 2, y));

        if (damageCooldown > 0) damageCooldown -= deltaTime;
    }

    public int getMoveDir() {
        return moveDir;
    }

    public void reset(double startX, double startY) {
        x = startX;
        y = startY;
        health = maxHealth;
        damageCooldown = 0;
        dead = false;
        moveUp = moveDown = moveLeft = moveRight = false;
    }
}
