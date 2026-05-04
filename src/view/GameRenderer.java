package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.entities.Enemy;
import model.entities.Player;
import model.managers.EffectManager;
import model.managers.EnemyProjectileManager;
import model.managers.ProjectileManager;
import model.managers.UpgradeManager;
import model.world.Camera;
import model.world.GameWorld;
import util.images.TextureAtlas;

public class GameRenderer {

    private static final int EFFECT_FRAME_SIZE = 32;

    private final int viewportWidth;
    private final int viewportHeight;
    private final double resolutionScale;
    private final int gridSize;
    private final Camera camera;
    private final TextureAtlas textures;
    private final HUD hud;
    private final OverlayHandler overlay;

    public GameRenderer(int viewportWidth, int viewportHeight, double resolutionScale,
                        Camera camera, UpgradeManager upgradeManager) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.resolutionScale = resolutionScale;
        this.gridSize = (int) (60 * resolutionScale);
        this.camera = camera;
        this.textures = new TextureAtlas();
        this.hud = new HUD(viewportWidth, viewportHeight, resolutionScale);
        this.overlay = new OverlayHandler(viewportWidth, viewportHeight, resolutionScale, upgradeManager, textures);
    }

    public void render(GraphicsContext gc, GameWorld world) {
        double ox = camera.getOffsetX();
        double oy = camera.getOffsetY();

        renderBackground(gc, ox, oy);
        renderProjectiles(gc, world.getProjectileManager(), ox, oy);
        renderEnemyProjectiles(gc, world.getEnemyProjectileManager(), ox, oy);
        renderPlayer(gc, world.getPlayer(), ox, oy);
        renderEnemies(gc, world, ox, oy);
        renderEffects(gc, world.getEffectManager(), ox, oy);
        hud.draw(gc, world);
        overlay.draw(gc, world.getState());
    }

    private void renderBackground(GraphicsContext gc, double ox, double oy) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, viewportWidth, viewportHeight);

        gc.setStroke(Color.GREY);
        gc.setLineWidth(1);
        double startX = -(ox % gridSize);
        double startY = -(oy % gridSize);
        for (double x = startX; x < viewportWidth; x += gridSize) {
            gc.strokeLine(x, 0, x, viewportHeight);
        }
        for (double y = startY; y < viewportHeight; y += gridSize) {
            gc.strokeLine(0, y, viewportWidth, y);
        }
    }

    private void renderPlayer(GraphicsContext gc, Player p, double ox, double oy) {
        if (p.isBlinking()) return;
        Image tex = textures.getPlayerTexture(p.getMoveDir());
        double size = p.getSize() * resolutionScale;
        gc.drawImage(tex, p.getX() - size / 2 - ox, p.getY() - size / 2 - oy, size, size);
    }

    private void renderEnemies(GraphicsContext gc, GameWorld world, double ox, double oy) {
        for (Enemy e : world.getEnemyHandler().getEnemies()) {
            if (!camera.isVisible(e.getX(), e.getY(), e.getSize())) continue;
            Image tex = textures.getEnemyTexture(e.getTextureID());
            double size = e.getSize() * resolutionScale;
            gc.drawImage(tex, e.getX() - size / 2 - ox, e.getY() - size / 2 - oy, size, size);
        }
    }

    private void renderProjectiles(GraphicsContext gc, ProjectileManager pm, double ox, double oy) {
        for (int i = 0; i < pm.getCount(); i++) {
            double px = pm.getX(i) - ox;
            double py = pm.getY(i) - oy;
            if (isOffscreen(px, py)) continue;

            double r = pm.getRadius(i) * resolutionScale;
            Image tex = textures.getProjectileTexture(pm.getTextureID(i));

            if (pm.isGrenade(i)) {
                drawSprite(gc, tex, px, py, r, 0);
                drawGrenadeFuseIndicator(gc, pm, i, px, py);
            } else {
                double angle = Math.toDegrees(Math.atan2(pm.getVelY(i), pm.getVelX(i)));
                drawSprite(gc, tex, px, py, r, angle);
            }
        }
    }

    private void drawGrenadeFuseIndicator(GraphicsContext gc, ProjectileManager pm,
                                           int i, double px, double py) {
        double fuseRemaining = pm.getFuseTimer(i);
        if (fuseRemaining >= 1.0) return;

        double alpha = 0.15 + 0.25 * (1.0 - fuseRemaining);
        gc.setStroke(Color.rgb(255, 60, 30, alpha));
        gc.setLineWidth(2);
        double explosionR = 150 * resolutionScale;
        gc.strokeOval(px - explosionR, py - explosionR, explosionR * 2, explosionR * 2);
    }

    private void renderEnemyProjectiles(GraphicsContext gc, EnemyProjectileManager epm,
                                         double ox, double oy) {
        for (int i = 0; i < epm.getCount(); i++) {
            double px = epm.getX(i) - ox;
            double py = epm.getY(i) - oy;
            if (isOffscreen(px, py)) continue;

            double r = epm.getRadius(i);
            Image tex = textures.getProjectileTexture(epm.getTextureID(i));
            double angle = Math.toDegrees(Math.atan2(epm.getVelY(i), epm.getVelX(i)));
            drawSprite(gc, tex, px, py, r, angle);
        }
    }

    private void renderEffects(GraphicsContext gc, EffectManager em, double ox, double oy) {
        for (int i = 0; i < em.getCount(); i++) {
            double x = em.getX(i) - ox;
            double y = em.getY(i) - oy;
            int frame = em.getFrame(i);
            int fxID = em.getEffectID(i);
            Image sheet = textures.getEffectTexture(fxID);

            int cols = (int) (sheet.getWidth() / EFFECT_FRAME_SIZE);
            int sx = (frame % cols) * EFFECT_FRAME_SIZE;
            int sy = (frame / cols) * EFFECT_FRAME_SIZE;
            int size = (int) (em.getEffectSize(fxID) * resolutionScale);

            gc.drawImage(sheet,
                    sx, sy, EFFECT_FRAME_SIZE, EFFECT_FRAME_SIZE,
                    x - size / 2.0, y - size / 2.0, size, size);
        }
    }

    private void drawSprite(GraphicsContext gc, Image tex, double px, double py,
                            double r, double angleDeg) {
        gc.save();
        gc.translate(px, py);
        if (angleDeg != 0) gc.rotate(angleDeg);
        gc.drawImage(tex, -r, -r, r * 2, r * 2);
        gc.restore();
    }

    private boolean isOffscreen(double px, double py) {
        return px < -200 || px > viewportWidth + 200
                || py < -200 || py > viewportHeight + 200;
    }

    public OverlayHandler getOverlay() {
        return overlay;
    }
}
