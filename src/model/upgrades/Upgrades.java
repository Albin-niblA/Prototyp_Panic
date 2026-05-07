package model.upgrades;

public enum Upgrades {
    // Common
    Nimble("Faster projectile firerate and movespeed", 1, Rarity.Common),
    Healthy("Increase max health and health regeneration", 3, Rarity.Common),
    Sharp("Deal more projectile damage", 4, Rarity.Common),

    // Uncommon
    Vampire("Heal for a part of your damage", 5, Rarity.Uncommon),
    Blink("Unlock blink movement. \nIncrease distance and reduce cooldown", 2, Rarity.Uncommon),

    // Epic
    Multishot("Shoot an additional projectile per level", 0, Rarity.Epic),
    Bounce("Projectiles bounce an additional time on walls", 6, Rarity.Epic);

    String description;
    int id;
    Rarity rarity;

    Upgrades(String description, int id, Rarity rarity) {
        this.description = description;
        this.id = id;
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Rarity getRarity() {
        return rarity;
    }
}
