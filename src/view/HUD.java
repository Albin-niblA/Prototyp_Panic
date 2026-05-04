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
}
