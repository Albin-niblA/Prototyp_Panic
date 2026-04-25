package model.upgrades;

public enum Rarity {
    Common(0.50f, "8C8C8C", "F2F2F2", "B8CED1"),
    Uncommon(0.25f, "F5971B", "FFD094", "F5B71B"),
    Rare(0.15f, "48A0F7", "A7D2FC", "68D6F2"),
    Epic(0.10f, "C56DE3", "DFB4ED", "FA4D9F");

    float chance;
    String gradientStart;
    String gradientEnd;
    String lightColor;

    Rarity(float chance, String gradientStart, String gradientEnd, String lightColor) {
        this.chance = chance;
        this.gradientStart = gradientStart;
        this.gradientEnd = gradientEnd;
        this.lightColor = lightColor;
    }

    public float getChance() {
        return chance;
    }

    public String getGradientStart() {
        return gradientStart;
    }

    public String getGradientEnd() {
        return gradientEnd;
    }

    public String getLightColor() {
        return lightColor;
    }
}
