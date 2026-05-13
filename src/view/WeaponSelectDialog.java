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

        for (int i = 0; i < STAR_COUNT; i++) {
            sx[i] = rand.nextDouble() * width;
            sy[i] = rand.nextDouble() * height;
            sSize[i] = 1 + rand.nextDouble() * 2.5;
            sSpeed[i] = 0.2 + rand.nextDouble() * 0.8;
            sPhase[i] = rand.nextDouble() * Math.PI * 2;
            sBaseAlpha[i] = 0.3 + rand.nextDouble() * 0.7;
        }

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

                    gc.setFill(Color.gray(1.0, alpha));
                    gc.fillOval(sx[i], sy[i], sSize[i], sSize[i]);
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
