package model.items;

import java.util.List;

public class Item {
    private String name;
    private List<String> description;
    private final int textureID;
    private final int price;

    private final int movementSpeed;
    private final int health;
    private final int damage;
    private final int attackSpeed;
    private final int onHit;

    public Item(String name, int textureID, int price, int movementSpeed, int health, int damage, int attackSpeed, int onHit) {
        this.name = name;
        this.textureID = textureID;
        this.price = price;
        this.movementSpeed = movementSpeed;
        this.health = health;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.onHit = onHit;
        formatDescription();
    }

    private void formatDescription() {
        if (movementSpeed > 0) {
            description.add("Movement speed: " + movementSpeed);
        }
        if (health > 0) {
            description.add("Health: " + health);
        }
        if (damage > 0) {
            description.add("Damage: " + damage);
        }
        if (attackSpeed > 0) {
            description.add("Attack speed: " + attackSpeed + "%");
        }
        if (onHit > 0) {
            description.add("Onhit damage: " + onHit);
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getOnHit() {
        return onHit;
    }

    public int getPrice() {
        return price;
    }
}
