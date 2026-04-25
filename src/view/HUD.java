package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.world.GameWorld;
import model.entities.Player;

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
    }

    private void drawHealthBar(GraphicsContext gc, Player p) {
        double barWidth = (double) screenWidth / 5;
        double barHeight = (double) screenHeight / 30;
        double barX = (double) screenWidth / 70;
        double barY = screenHeight - (double) screenHeight / 20;

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
        gc.setFont(Font.font("Arial", 16));
        gc.fillText("HP: " + p.getHealth() + " / " + p.getMaxHealth(),
                     barX + 10, barY + 18);
    }

    private void drawWaveInfo(GraphicsContext gc, GameWorld world) {
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Wave: " + world.getWaveManager().getCurrentWave(),
                     screenWidth - 200, 35);
        gc.fillText("Enemies: " + world.getEnemyHandler().getCount(),
                     screenWidth - 200, 65);
    }
}
