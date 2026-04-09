package util;

import javafx.scene.image.Image;

public class TextureAtlas {
    private final Image[] playerTextures = new Image[4];
    private final Image[] enemyTextures = new Image[2];
    private final Image[] projectileTextures = new Image[3];

    public TextureAtlas() {
        playerTextures[0] = load("/util/images/player/pFront.png");
        playerTextures[1] = load("/util/images/player/pBack.png");
        playerTextures[2] = load("/util/images/player/pLeft.png");
        playerTextures[3] = load("/util/images/player/pRight.png");

        enemyTextures[0] = load("/util/images/enemies/slime.png");
        enemyTextures[1] = load("/util/images/enemies/demonslime.png");

        projectileTextures[0] = load("/util/images/projectiles/bullet1.png");
        projectileTextures[1] = load("/util/images/projectiles/arrow.png");
        projectileTextures[2] = load("/util/images/projectiles/rocket.png");
    }

    private Image load(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }

    public Image getPlayerTexture(int dir) { return playerTextures[dir]; }
    public Image getEnemyTexture(int id) { return enemyTextures[id]; }
    public Image getProjectileTexture(int id) { return projectileTextures[id]; }
}
