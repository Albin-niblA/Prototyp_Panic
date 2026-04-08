package model.enemies;

import model.Enemy;

public class DemonSlime extends Enemy {
    public DemonSlime() {
        setTextureID(1);
    }

    public DemonSlime(double x, double y) {
        this.x = x;
        this.y = y;
        this.size = 40;
        this.health = 100;
        this.maxHealth = 100;
        this.movementSpeed = 100;
        this.setTextureID(1);
    }
}
