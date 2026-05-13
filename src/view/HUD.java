package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.entities.Player;
import model.world.GameWorld;

public class HUD {

    private final int screenWidth;
    private final int screenHeight;
    private final double resolutionScale;

    public HUD(int screenWidth, int screenHeight, double resolutionScale) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.resolutionScale = resolutionScale;
    }

    public void draw(GraphicsContext gc, GameWorld world) {
        drawHealthBar(gc, world.getPlayer());
        drawWaveInfo(gc, world);
        drawXpBar(gc, world.getPlayer());
        drawCoins(gc, world);
    }

    private void drawHealthBar(GraphicsContext gc, Player p) {
        double barWidth = screenWidth / 5.0;
        double barHeight = screenHeight / 30.0;
        double barX = screenWidth / 70.0;
        double barY = screenHeight - screenHeight / 20.0;
        double healthPct = (double) p.getHealth() / p.getMaxHealth();

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(barX, barY, barWidth, barHeight);

        Color healthColor;
        if (healthPct > 0.6) healthColor = Color.LIMEGREEN;
        else if (healthPct > 0.3) healthColor = Color.YELLOW;
        else healthColor = Color.RED;

        gc.setFill(healthColor);
        gc.fillRect(barX, barY, barWidth * healthPct, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16 * resolutionScale));
        gc.fillText("HP: " + p.getHealth() + " / " + p.getMaxHealth(),
                barX + 10, barY + barHeight * 0.6);
    }

    private void drawWaveInfo(GraphicsContext gc, GameWorld world) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 22 * resolutionScale));
        double rightMargin = screenWidth - 200 * resolutionScale;
        gc.fillText("Wave: " + world.getWaveManager().getCurrentWave(),
                rightMargin, 35 * resolutionScale);
        gc.fillText("Enemies: " + world.getEnemyHandler().getCount(),
                rightMargin, 65 * resolutionScale);
    }

    private void drawCoins(GraphicsContext gc, GameWorld world) {
        int coins = world.getMyntManager().getBalance();
        String text = "Mynt: " + coins;

        // Samma X som HP/XP-barerna, precis ovanför XP-baren
        double barX      = screenWidth / 70.0;
        double barHeight = screenHeight / 30.0;
        double xpBarY    = screenHeight - screenHeight / 10.0;
        double coinY     = xpBarY - barHeight * 0.3; // lite ovanför XP-baren

        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.setFont(javafx.scene.text.Font.font("Arial",
                javafx.scene.text.FontWeight.BOLD, 18 * resolutionScale));

        // Svart skugga för läsbarhet
        gc.setFill(Color.BLACK);
        gc.fillText(text, barX + 1, coinY + 1);

        // Gul text
        gc.setFill(Color.GOLD);
        gc.fillText(text, barX, coinY);
    }

    private void drawXpBar(GraphicsContext gc, Player p){
        double barWidth = screenWidth / 5.0;
        double barHeight = screenHeight / 30.0;
        double barX = screenWidth / 70.0;
        double barY = screenHeight - screenHeight / 10.0;
        double xpPct = (double) p.getXp()/p.getXpRequired();

        gc.setFill(Color.BLACK);
        gc.fillRect(barX, barY, barWidth, barHeight);
        gc.setFill(Color.PURPLE);
        gc.fillRect(barX, barY, barWidth * xpPct, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 16 * resolutionScale));
        gc.fillText("LVL " + p.getLevel() + "  XP: " + p.getXp() + " / " + p.getXpRequired(),
                barX + 10, barY + barHeight * 0.75);

    }
}
