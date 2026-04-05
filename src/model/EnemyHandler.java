package model;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import model.enemies.Slime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class EnemyHandler {
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private final int X_VALUE_MAX;
    private final int Y_VALUE_MAX;
    private Random rand = new Random();
    private final int ENEMY_TEXTURE_COUNT = 1;
    private final Image[] enemyTextures = new Image[ENEMY_TEXTURE_COUNT];
    private void initTextures() {
        enemyTextures[0] = new Image(getClass().getResourceAsStream("/util/images/enemies/slime.png"));
    }

    public EnemyHandler(int x, int y) {
        this.X_VALUE_MAX = x;
        this.Y_VALUE_MAX = y;
    }


    public void update(double deltaTime) {
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy e = it.next();
            e.update();

            if (e.isDead()) {
                it.remove();
            }
        }
    }

    public void drawAll(GraphicsContext gc) {
        Iterator<Enemy> it = enemies.iterator();

        while (it.hasNext()) {
            Enemy e = it.next();
            Image tex = enemyTextures[e.getTextureID()];
            double size = e.getSize();

            gc.drawImage(tex, e.getX() - size/2, e.getY() - size/2, size, size);
        }
    }

    public void spawnRandom(int type) {
        Enemy e;
        double x = rand.nextDouble(0, X_VALUE_MAX);
        double y = rand.nextDouble(0, Y_VALUE_MAX);
        switch (type) {
            case 0:
                e = new Slime(x, y);
                break;
            default:
                e = new Slime(x, y);
        }
        enemies.add(e);
    }
}
