package model.weapon;

import model.ProjectileManager;

public abstract class Weapon {
    private String name;
    private int damage;
    private double fireInterval;
    private double projectileSpeed;
    private double projectileRadius;
    private int textureId;

    protected Weapon(String name, int damage, double fireInterval,
                     double projectileSpeed, double projectileRadius, int textureId) {
        this.name = name;
        this.damage = damage;
        this.fireInterval = fireInterval;
        this.projectileSpeed = projectileSpeed;
        this.projectileRadius = projectileRadius;
        this.textureId = textureId;
    }

    public abstract void shoot(ProjectileManager pm, double originX, double originY, double targetX, double targetY);

    public static Weapon fromType(WeaponType type) {
        return switch (type) {
            case BULLET  -> new Bullet();
            case ARROW   -> new Arrow();
            case ROCKET  -> new Rocket();
            case GRENADE -> new Grenade();
        };
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public double getFireInterval() { return fireInterval; }
    public double getProjectileSpeed() { return projectileSpeed; }
    public double getProjectileRadius() { return projectileRadius; }
    public int getTextureId() { return textureId; }

    public void setDamage(int damage) { this.damage = damage; }
    public void setFireInterval(double fireInterval) { this.fireInterval = fireInterval; }
    public void setProjectileSpeed(double projectileSpeed) { this.projectileSpeed = projectileSpeed; }
    public void setProjectileRadius(double projectileRadius) { this.projectileRadius = projectileRadius; }
}
