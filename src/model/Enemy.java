package model;

public abstract class Enemy extends Entity {
    protected int textureID;

    public void update(double deltaTime, double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            x += (dx / dist) * movementSpeed * deltaTime;
            y += (dy / dist) * movementSpeed * deltaTime;
        }
    }

    public int getTextureID() {
        return textureID;
    }
}
