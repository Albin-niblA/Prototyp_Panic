package view;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.weapon.WeaponType;

import java.io.InputStream;
import java.util.Random;
import java.util.function.Consumer;

public class WeaponSelectDialog {

    private static final Color COLOR_DEFAULT = Color.web("#66FF44");
    private static final Color COLOR_HOVER_A = Color.web("#44FFCC");
    private static final Color COLOR_HOVER_B = Color.WHITE;
    private static final Color COLOR_OUTLINE = Color.BLACK;
    private static final Color COLOR_TITLE = Color.GREEN;
    private static final Color COLOR_ACTIVE = Color.web("#66FF44");
    private static final Color COLOR_INACTIVE = Color.web("#888888");
    private static final Color COLOR_LABEL = Color.web("#AAFFAA");
    private static final int STAR_COUNT = 500;

    private static final String[] DIFFICULTIES = {"Easy", "Normal", "Hard"};

    private final Stage stage;
    private final Scene scene;
    private final AnimationTimer starAnimation;

    private WeaponType selectedWeapon = WeaponType.BULLET;
    private int selectedDifficulty = 1; // 0=Easy, 1=Normal, 2=Hard

    private final Font pixelFont;
    private final Font titleFont;
    private final Font smallFont;

    public WeaponSelectDialog(Stage stage, Consumer<WeaponType> onStart, Runnable onBack,
                              int width, int height, double resolutionScale) {
        this.stage = stage;

        pixelFont = loadPixelFont(20 * resolutionScale);
        titleFont = loadPixelFont(34 * resolutionScale);
        smallFont = loadPixelFont(14 * resolutionScale);

        // Starfield
        Canvas starCanvas = new Canvas(width, height);
        GraphicsContext gc = starCanvas.getGraphicsContext2D();

        Random rand = new Random();
        double[] sx = new double[STAR_COUNT];
        double[] sy = new double[STAR_COUNT];
        double[] sSize = new double[STAR_COUNT];
        double[] sSpeed = new double[STAR_COUNT];
        double[] sPhase = new double[STAR_COUNT];
        double[] sBaseAlpha = new double[STAR_COUNT];
        double[] sTwinklePhase = new double[STAR_COUNT];
        double[] sTwinkleSpeed = new double[STAR_COUNT];

        for (int i = 0; i < STAR_COUNT; i++) {
            sx[i] = rand.nextDouble() * width;
            sy[i] = rand.nextDouble() * height;
            sSize[i] = 1 + rand.nextDouble() * 2.5;
            sSpeed[i] = 0.2 + rand.nextDouble() * 0.8;
            sPhase[i] = rand.nextDouble() * Math.PI * 2;
            sBaseAlpha[i] = 0.3 + rand.nextDouble() * 0.7;
            sTwinklePhase[i] = rand.nextDouble() * Math.PI * 2;
            sTwinkleSpeed[i] = 0.3 + rand.nextDouble() * 0.7;
        }

        double[] ssActive   = {0};
        double[] ssX        = {0};
        double[] ssY        = {0};
        double[] ssVx       = {0};
        double[] ssVy       = {0};
        double[] ssLife     = {0};
        double[] ssMaxLife  = {0};
        double[] ssCooldown = {rand.nextDouble() * 7 + 8};

        starAnimation = new AnimationTimer() {
            private long lastNano = 0;

            @Override
            public void handle(long now) {
                double dt = (lastNano == 0) ? 0 : (now - lastNano) / 1_000_000_000.0;
                lastNano = now;
                double timeSec = now / 1_000_000_000.0;

                gc.setFill(Color.BLACK);
                gc.fillRect(0, 0, width, height);

                for (int i = 0; i < STAR_COUNT; i++) {
                    sx[i] += sSpeed[i] * dt * 30;
                    sy[i] += sSpeed[i] * dt * 20;

                    if (sx[i] > width) sx[i] -= width;
                    if (sy[i] > height) sy[i] -= height;

                    double twinkle = 0.5 + 0.5 * Math.sin(timeSec * (1.5 + sSpeed[i]) + sPhase[i]);
                    double alpha = sBaseAlpha[i] * (0.3 + 0.7 * twinkle);

                    double bluePulse = Math.sin(timeSec * sTwinkleSpeed[i] + sTwinklePhase[i]);
                    if (bluePulse > 0.85) {
                        double blueIntensity = (bluePulse - 0.85) / 0.15;
                        gc.setFill(Color.color(0.7 + 0.3 * (1 - blueIntensity), 0.85 + 0.15 * (1 - blueIntensity), 1.0, Math.min(1.0, alpha + 0.3 * blueIntensity)));
                    } else {
                        gc.setFill(Color.gray(1.0, alpha));
                    }
                    gc.fillOval(sx[i], sy[i], sSize[i], sSize[i]);
                }

                // --- Shooting star ---
                if (ssActive[0] == 0) {
                    ssCooldown[0] -= dt;
                    if (ssCooldown[0] <= 0) {
                        ssActive[0] = 1;
                        ssMaxLife[0] = 0.5 + rand.nextDouble() * 0.5;
                        ssLife[0] = ssMaxLife[0];
                        if (rand.nextBoolean()) {
                            ssX[0] = rand.nextDouble() * width * 0.7;
                            ssY[0] = rand.nextDouble() * height * 0.3;
                        } else {
                            ssX[0] = rand.nextDouble() * width * 0.3;
                            ssY[0] = rand.nextDouble() * height * 0.5;
                        }
                        double speed = 300 + rand.nextDouble() * 200;
                        double angle = Math.toRadians(25 + rand.nextDouble() * 30);
                        ssVx[0] = speed * Math.cos(angle);
                        ssVy[0] = speed * Math.sin(angle);
                    }
                }
                if (ssActive[0] == 1) {
                    ssX[0] += ssVx[0] * dt;
                    ssY[0] += ssVy[0] * dt;
                    ssLife[0] -= dt;
                    if (ssLife[0] <= 0) {
                        ssActive[0] = 0;
                        ssCooldown[0] = 8 + rand.nextDouble() * 7;
                    } else {
                        int tailSegments = 20;
                        double progress = 1.0 - (ssLife[0] / ssMaxLife[0]);
                        double tailLength = 80 + 60 * (1.0 - progress);
                        double vLen = Math.sqrt(ssVx[0] * ssVx[0] + ssVy[0] * ssVy[0]);
                        double nx = ssVx[0] / vLen;
                        double ny = ssVy[0] / vLen;
                        double lifeFade = ssLife[0] < ssMaxLife[0] * 0.3
                                ? ssLife[0] / (ssMaxLife[0] * 0.3) : 1.0;

                        gc.save();
                        for (int t = 0; t < tailSegments; t++) {
                            double frac = t / (double) tailSegments;
                            double tx = ssX[0] - nx * tailLength * frac;
                            double ty = ssY[0] - ny * tailLength * frac;
                            double a = (1.0 - frac) * lifeFade;
                            double size = 3.0 * (1.0 - frac * 0.7);
                            double r = 1.0 - 0.3 * frac;
                            double g = 1.0 - 0.15 * frac;
                            double b = Math.min(1.0, 0.9 + 0.1 * frac);
                            gc.setGlobalAlpha(Math.max(0, Math.min(1.0, a)));
                            gc.setFill(Color.color(r, g, b));
                            gc.fillOval(tx - size / 2, ty - size / 2, size, size);
                        }
                        gc.restore();
                    }
                }
            }
        };

        Text title = new Text("GAME SETUP");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text weaponLabel = new Text("Weapon");
        weaponLabel.setFont(smallFont);
        weaponLabel.setFill(COLOR_LABEL);
        weaponLabel.setStroke(COLOR_OUTLINE);
        weaponLabel.setStrokeWidth(1);

        WeaponType[] weapons = WeaponType.values();
        Text[] weaponTexts = new Text[weapons.length];

        Runnable updateWeaponColors = () -> {
            for (int i = 0; i < weapons.length; i++) {
                weaponTexts[i].setFill(weapons[i] == selectedWeapon ? COLOR_ACTIVE : COLOR_INACTIVE);
            }
        };

        HBox weaponRow = new HBox(20 * resolutionScale);
        weaponRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < weapons.length; i++) {
            if (i > 0) {
                Text sep = new Text("/");
                sep.setFont(pixelFont);
                sep.setFill(COLOR_LABEL);
                sep.setStroke(COLOR_OUTLINE);
                sep.setStrokeWidth(1.5);
                weaponRow.getChildren().add(sep);
            }

            Text wText = new Text(weapons[i].getDisplayName());
            wText.setFont(pixelFont);
            wText.setStroke(COLOR_OUTLINE);
            wText.setStrokeWidth(1.5);
            wText.setCursor(Cursor.HAND);
            weaponTexts[i] = wText;

            final WeaponType wt = weapons[i];
            wText.setOnMouseClicked(e -> {
                selectedWeapon = wt;
                updateWeaponColors.run();
            });
            wText.setOnMouseEntered(e -> wText.setFill(COLOR_HOVER_A));
            wText.setOnMouseExited(e -> updateWeaponColors.run());

            weaponRow.getChildren().add(wText);
        }
        updateWeaponColors.run();

        Text diffLabel = new Text("Difficulty");
        diffLabel.setFont(smallFont);
        diffLabel.setFill(COLOR_LABEL);
        diffLabel.setStroke(COLOR_OUTLINE);
        diffLabel.setStrokeWidth(1);

        Text[] diffTexts = new Text[DIFFICULTIES.length];

        Runnable updateDiffColors = () -> {
            for (int i = 0; i < DIFFICULTIES.length; i++) {
                diffTexts[i].setFill(i == selectedDifficulty ? COLOR_ACTIVE : COLOR_INACTIVE);
            }
        };

        HBox diffRow = new HBox(20 * resolutionScale);
        diffRow.setAlignment(Pos.CENTER);

        for (int i = 0; i < DIFFICULTIES.length; i++) {
            if (i > 0) {
                Text sep = new Text("/");
                sep.setFont(pixelFont);
                sep.setFill(COLOR_LABEL);
                sep.setStroke(COLOR_OUTLINE);
                sep.setStrokeWidth(1.5);
                diffRow.getChildren().add(sep);
            }

            Text dText = new Text(DIFFICULTIES[i]);
            dText.setFont(pixelFont);
            dText.setStroke(COLOR_OUTLINE);
            dText.setStrokeWidth(1.5);
            dText.setCursor(Cursor.HAND);
            diffTexts[i] = dText;

            final int idx = i;
            dText.setOnMouseClicked(e -> {
                selectedDifficulty = idx;
                updateDiffColors.run();
            });
            dText.setOnMouseEntered(e -> dText.setFill(COLOR_HOVER_A));
            dText.setOnMouseExited(e -> updateDiffColors.run());

            diffRow.getChildren().add(dText);
        }
        updateDiffColors.run();

        Text startItem = createMenuItem("Start", () -> onStart.accept(selectedWeapon));
        Text backItem = createMenuItem("Back", onBack);

        VBox menuBox = new VBox(40 * resolutionScale,
                title,
                weaponLabel, weaponRow,
                diffLabel, diffRow,
                startItem, backItem
        );
        menuBox.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(starCanvas, menuBox);
        scene = new Scene(root, width, height);

        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != scene) {
                starAnimation.stop();
            }
        });
    }

    private Text createMenuItem(String label, Runnable action) {
        Text item = new Text(label);
        item.setFont(pixelFont);
        item.setFill(COLOR_DEFAULT);
        item.setStroke(COLOR_OUTLINE);
        item.setStrokeWidth(1.5);
        item.setCursor(Cursor.HAND);

        boolean[] toggle = {false};
        Timeline hoverAnim = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            toggle[0] = !toggle[0];
            item.setFill(toggle[0] ? COLOR_HOVER_B : COLOR_HOVER_A);
        }));
        hoverAnim.setCycleCount(Animation.INDEFINITE);

        item.setOnMouseEntered(e -> {
            item.setText("> " + label + " <");
            item.setFill(COLOR_HOVER_A);
            toggle[0] = false;
            hoverAnim.playFromStart();
        });

        item.setOnMouseExited(e -> {
            hoverAnim.stop();
            item.setText(label);
            item.setFill(COLOR_DEFAULT);
        });

        item.setOnMouseClicked(e -> action.run());

        return item;
    }

    private Font loadPixelFont(double size) {
        InputStream fontStream = getClass().getResourceAsStream("/util/fonts/PressStart2P.ttf");
        if (fontStream != null) {
            Font font = Font.loadFont(fontStream, size);
            if (font != null) return font;
        }
        return Font.font("Monospaced", size);
    }

    public void show() {
        starAnimation.start();
        stage.setTitle("Prototype Panic - Game Setup");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
