package model.entities;

import model.managers.ProjectileManager;

public abstract class Enemy extends Entity {
    protected int textureID;
    protected int coinDropAmount;
    protected int xpDropAmount;
    protected boolean isBoss = false;

    public Enemy(double x, double y, int size, int maxHealth, int movementSpeed,
                 int contactDamage, int textureID, int coinDropAmount, int xpDropAmount) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.movementSpeed = movementSpeed;
        this.contactDamage = contactDamage;
        this.textureID = textureID;
        this.coinDropAmount = coinDropAmount;
        this.xpDropAmount = xpDropAmount;
    }

    public void update(double deltaTime, double playerX, double playerY) {
        double dx = playerX - x;
        double dy = playerY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            x += (dx / dist) * movementSpeed * deltaTime;
            y += (dy / dist) * movementSpeed * deltaTime;
        }
    }

    public void update(double deltaTime, double playerX, double playerY,
                        ProjectileManager projectileManager) {
        update(deltaTime, playerX, playerY);
    }

    public void takeProjectileDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            dead = true;
        }
    }

    public int getTextureID() {
        return textureID;
    }
    public int getCoinDropAmount() { return coinDropAmount; }
    public int getXpDropAmount() { return xpDropAmount; }
}
