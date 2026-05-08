package model.upgrades;

public enum Upgrades {
    // Common
    Nimble("Faster projectile firerate and movespeed", 0, Rarity.Common),
    Healthy("Increase max health and health regeneration", 1, Rarity.Common),
    Sharp("Deal more projectile damage", 2, Rarity.Common),

    // Uncommon
    Vampire("Heal for a part of your damage", 3, Rarity.Uncommon),
    Blink("Unlock blink movement. \nIncrease distance and reduce cooldown", 4, Rarity.Uncommon),
    Fortunate("Increase the amount of gold and xp enemies drop", 5, Rarity.Uncommon),

    // Rare
    Frostbullet("Projectiles slow enemies", 6, Rarity.Rare),
    Poisonbullet("Enemies hit take poison damage for 3 seconds", 7, Rarity.Rare),
    Angeltouch("Increase the chance of not taking damage from an enemy hit", 8, Rarity.Rare),

    // Epic
    Multishot("Shoot an additional projectile per level", 9, Rarity.Epic),
    Bounce("Projectiles bounce an additional time on walls", 10, Rarity.Epic),
    Electric("Projectiles hit 3 targets indirectly \nfor a part of the projectile damage", 11, Rarity.Epic);

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
