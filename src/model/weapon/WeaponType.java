package model.weapon;

public enum WeaponType {
    BULLET("Bullet"),
    ARROW("Arrow"),
    ROCKET("Rocket");

    private final String displayName;

    WeaponType(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
