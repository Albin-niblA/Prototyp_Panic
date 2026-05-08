package model.managers;

import model.entities.Player;
import model.upgrades.Rarity;
import model.upgrades.Upgrades;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumMap;
import java.util.Random;

public class UpgradeManager {
    private Player p;
    private Random rand = new Random();
    private List<Upgrades> commonUpgrades = new ArrayList<>();
    private List<Upgrades> uncommonUpgrades = new ArrayList<>();
    private List<Upgrades> rareUpgrades = new ArrayList<>();
    private List<Upgrades> epicUpgrades = new ArrayList<>();

    private double firerateMultiplier = 1;
    private double damageMultiplier = 1;
    private double lifesteal = 0;
    private double poisonDamage = 0;
    private double slowMultiplier = 1;
    private double angeltouchChance = 0;
    private double electricDamageMultiplier = 0;
    private double fortunateMultiplier = 1;
    private int shotAmount = 1;
    private int bounceAmount = 0;
    private final double EFFECT_OVER_TIME_TICK_INTERVAL = 0.25;

    // Blink
    private double BLINK_DISTANCE = 200.0;
    private double BLINK_COOLDOWN_DURATION = 3.0;
    private final double BLINK_VISIBLE_DELAY = 0.21;
    private double blinkCooldown = 0.0;
    private double blinkVisibleTimer = 0.0;
    private boolean blinking = false;
    private boolean canBlink = true;

    public UpgradeManager(Player p) {
        this.p = p;
        for (Upgrades u : Upgrades.values()) {
            upgradeMap.put(u, 0);
        }
        initUpgradeLists();
    }
    private EnumMap<Upgrades, Integer> upgradeMap = new EnumMap<>(Upgrades.class);

    public List<Upgrades> rollThree() {
        float rollChance;
        List<Upgrades> rolledUpgrades = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            rollChance = rand.nextFloat(0, 1);
            float tempChance = Rarity.Common.getChance();
            if (rollChance < tempChance) {
                giveRandomFromList(rolledUpgrades, commonUpgrades);
                continue;
            }
            tempChance += Rarity.Uncommon.getChance();
            if (rollChance < tempChance) {
                giveRandomFromList(rolledUpgrades, uncommonUpgrades);
                continue;
            }
            tempChance += Rarity.Rare.getChance();
            if (rollChance < tempChance) {
                giveRandomFromList(rolledUpgrades, rareUpgrades);
                continue;
            }
            giveRandomFromList(rolledUpgrades, epicUpgrades);
        }
        return rolledUpgrades;
    }

    private void giveRandomFromList(List<Upgrades> rollList, List<Upgrades> rarityList) {
        Upgrades upgrade;
        while (true) {
            int randomIndex = rand.nextInt(rarityList.size());
            upgrade = rarityList.get(randomIndex);
            int counter = 0;
            for (Upgrades u : rollList) {
                if (u != upgrade) {
                    counter++;
                }
            }
            if (counter == rollList.size()) break;
        }
        rollList.add(upgrade);
    }

    private void initUpgradeLists() {
        List<Upgrades> allUpgrades = new ArrayList<>(List.of(Upgrades.values()));
        for (Upgrades u : allUpgrades) {
            if (u.getRarity().equals(Rarity.Common)) {
                commonUpgrades.add(u);
            } else if (u.getRarity().equals(Rarity.Uncommon)) {
                uncommonUpgrades.add(u);
            } else if (u.getRarity().equals(Rarity.Rare)) {
                rareUpgrades.add(u);
            } else if (u.getRarity().equals(Rarity.Epic)) {
                epicUpgrades.add(u);
            }
        }
    }

    public void levelUpgrade(Upgrades u) {
        int level = upgradeMap.getOrDefault(u, 0) + 1;
        upgradeMap.put(u, level);

        switch (u) {
            case Nimble:
                p.setMovementSpeed(p.getFlatMoveSpeed() * (1.0 + (1 - Math.pow(0.85, level))));
                firerateMultiplier *= 0.85;
                break;

            case Healthy:
                p.setMaxHealth((int) (p.getFlatHealth() * (1.0 + (1 - Math.pow(0.85, level)))));
                p.setHealthRegen((int) (p.getMaxHealth() * 0.01 * level));
                break;

            case Sharp:
                damageMultiplier = 1.0 + (1 - Math.pow(0.85, level));
                break;

            case Vampire:
                lifesteal += 0.1;
                break;

            case Blink:
                BLINK_COOLDOWN_DURATION *= 0.8;
                BLINK_DISTANCE += 25;
                break;

            case Fortunate:
                fortunateMultiplier += 0.25;
                break;

            case Frostbullet:
                if (slowMultiplier >= 0.03) {
                    slowMultiplier -= 0.03;
                }
                break;

            case Poisonbullet:
                poisonDamage += 0.01;
                break;

            case Angeltouch:
                if (angeltouchChance <= 0.99) {
                    angeltouchChance += 0.01;
                }
                break;

            case Multishot:
                shotAmount++;
                break;

            case Bounce:
                bounceAmount++;
                break;

            case Electric:
                electricDamageMultiplier += 0.15;
                break;

            default:
                break;
        }
    }

    public void update(double deltaTime) {
        if (blinkCooldown > 0)     blinkCooldown -= deltaTime;
        if (blinkVisibleTimer > 0) {
            blinkVisibleTimer -= deltaTime;
            if (blinkVisibleTimer <= 0) blinking = false;
        }
    }

    public void reset() {
        blinkCooldown = 0;
        blinkVisibleTimer = 0;
        blinking = false;
    }

    public boolean tryBlink(double mapWidth, double mapHeight) {
        int blinkLevel = getUpgradeLevel(Upgrades.Blink);
        if (blinkLevel == 0) return false;
        if (!canBlink || blinkCooldown > 0) return false;

        double dx = 0, dy = 0;
        switch (p.getMoveDir()) {
            case 0 -> { dy =  1; }
            case 1 -> { dy = -1; }
            case 2 -> { dx = -1; }
            case 3 -> { dx =  1; }
            case 4 -> { dx = -1; dy =  1; }
            case 5 -> { dx =  1; dy =  1; }
            case 6 -> { dx = -1; dy = -1; }
            case 7 -> { dx =  1; dy = -1; }
        }

        if (dx != 0 && dy != 0) {
            double factor = 1.0 / Math.sqrt(2);
            dx *= factor;
            dy *= factor;
        }
        double x = p.getX();
        double y = p.getY();
        double size = p.getSize();
        p.setX(Math.max(size / 2, Math.min(mapWidth  - size / 2, x + dx * BLINK_DISTANCE)));
        p.setY(Math.max(size / 2, Math.min(mapHeight - size / 2, y + dy * BLINK_DISTANCE)));

        blinkCooldown = BLINK_COOLDOWN_DURATION;
        blinking = true;
        blinkVisibleTimer = BLINK_VISIBLE_DELAY;
        return true;
    }

    public int getUpgradeLevel(Upgrades u) {
        return upgradeMap.getOrDefault(u, 0);
    }

    public double getFirerateMultiplier() {
        return firerateMultiplier;
    }

    public boolean isBlinking() {
        return blinking;
    }

    public double getLifesteal() {
        return lifesteal;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getPoisonDamage() {
        return poisonDamage;
    }

    public double getSlowMultiplier() {
        return slowMultiplier;
    }

    public double getAngeltouchChance() {
        return angeltouchChance;
    }

    public double getElectricDamageMultiplier() {
        return electricDamageMultiplier;
    }

    public double getFortunateMultiplier() {
        return fortunateMultiplier;
    }

    public double getEFFECT_OVER_TIME_TICK_INTERVAL() {
        return EFFECT_OVER_TIME_TICK_INTERVAL;
    }

    public int getBounceAmount() {
        return bounceAmount;
    }

    public int getShotAmount() {
        return shotAmount;
    }
}
