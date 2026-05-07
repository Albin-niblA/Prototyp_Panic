package model.managers;

import model.entities.Player;
import model.upgrades.Upgrades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.EnumMap;

public class UpgradeManager {
    private Player p;

    private double firerateMultiplier = 1;
    private double damageMultiplier = 1;
    private double lifesteal = 0;

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
    }

    private List<Upgrades> upgrades = new ArrayList<>();
    private EnumMap<Upgrades, Integer> upgradeMap = new EnumMap<>(Upgrades.class);

    public List<Upgrades> rollThree() {
        // collect all upgrades in a list, shuffle it, and return 3;
        List<Upgrades> allUpgrades = new ArrayList<>(List.of(Upgrades.values()));
        Collections.shuffle(allUpgrades);
        upgrades = allUpgrades.subList(0, 3);
        return upgrades;
    }

    public void levelUpgrade(Upgrades u) {
        int level = upgradeMap.getOrDefault(u, 0) + 1;
        upgradeMap.put(u, level);

        if (u == Upgrades.Blink) {
            BLINK_COOLDOWN_DURATION = BLINK_COOLDOWN_DURATION * 0.8;
            BLINK_DISTANCE += 25;
        }
        else if (u == Upgrades.Nimble) {
            p.setMovementSpeed(p.getFlatMoveSpeed() * (1.0 + (1 - Math.pow(0.85, level))));
            firerateMultiplier = firerateMultiplier * 0.85;
        }
        else if (u == Upgrades.Healthy) {
            p.setMaxHealth((int) (p.getFlatHealth() * (1.0 + (1 - Math.pow(0.85, level)))));
            p.setHealthRegen((int) (p.getMaxHealth() * 0.01 * level));
        }
        else if (u == Upgrades.Sharp) {
            damageMultiplier = 1.0 + (1 - Math.pow(0.85, level));
        }
        else if (u == Upgrades.Vampire) {
            lifesteal += 0.1;
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

    public List<Upgrades> getUpgrades() {
        return upgrades;
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
}
