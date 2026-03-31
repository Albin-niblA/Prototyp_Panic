package model;

public enum Directions {
    NORTH(0, -0.1),
    SOUTH(0, 0.1),
    WEST(-0.1, 0),
    EAST(0.1, 0),
    NORTHEAST(0.1, -0.1),
    NORTHWEST(-0.1, -0.1),
    SOUTHEAST(0.1, 0.1),
    SOUTHWEST(-0.1, 0.1);

    double x;
    double y;
    Directions(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
