package model.enemies;

import model.Enemy;

public class Slime extends Enemy {
    public Slime() {
        setTextureID(0);
    }

    public Slime(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.health = 50;
        this.maxHealth = 50;
        this.movementSpeed = 30;
        this.setTextureID(0);
    }
}
