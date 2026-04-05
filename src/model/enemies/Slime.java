package model.enemies;

import model.Enemy;

public class Slime extends Enemy {
    public Slime() {
        setTextureID(0);
    }

    public Slime(double x, double y) {
        super();
        setX(x);
        setY(y);
    }
}
