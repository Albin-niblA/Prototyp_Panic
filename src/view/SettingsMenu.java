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
import model.managers.SoundManager;
import util.settings.SettingsListener;

import java.io.InputStream;
import java.util.Random;

public class SettingsMenu {

    private static final Color COLOR_DEFAULT = Color.web("#66FF44");
    private static final Color COLOR_HOVER_A = Color.web("#44FFCC");
    private static final Color COLOR_HOVER_B = Color.WHITE;
    private static final Color COLOR_OUTLINE = Color.BLACK;
    private static final Color COLOR_TITLE = Color.GREEN;
    private static final Color COLOR_ACTIVE = Color.web("#66FF44");
    private static final Color COLOR_INACTIVE = Color.web("#888888");
    private static final Color COLOR_LABEL = Color.web("#AAFFAA");
    private static final int STAR_COUNT = 500;

    private final Stage stage;
    private final Scene scene;
    private final AnimationTimer starAnimation;
    private final SettingsListener listener;
    private final double resolutionScale;
    private final Runnable onBack;
    private final int width;
    private final int height;

    private VBox menuLayer;
    private StackPane root;
    private Font pixelFont;
    private Font titleFont;
    private Font smallFont;

    public SettingsMenu(Stage stage, SettingsListener listener, Runnable onBack, int width, int height, double resolutionScale) {
        this.stage = stage;
        this.listener = listener;
        this.onBack = onBack;
        this.width = width;
        this.height = height;
        this.resolutionScale = resolutionScale;

        pixelFont = loadPixelFont(20 * resolutionScale);
        titleFont = loadPixelFont(34 * resolutionScale);
        smallFont = loadPixelFont(14 * resolutionScale);

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

        menuLayer = new VBox();
        menuLayer.setAlignment(Pos.CENTER);

        root = new StackPane(starCanvas, menuLayer);
        scene = new Scene(root, width, height);

        showSettingsLanding();

        stage.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != scene) {
                starAnimation.stop();
            }
        });
    }

    private void showSettingsLanding() {
        menuLayer.getChildren().clear();

        Text title = new Text("SETTINGS");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text controlsItem = createMenuItem("Controls", this::showControlsPage);
        Text audioItem = createMenuItem("Audio", this::showAudioPage);
        Text backItem = createMenuItem("Back", onBack);

        VBox box = new VBox(40 * resolutionScale, title, controlsItem, audioItem, backItem);
        box.setAlignment(Pos.CENTER);
        menuLayer.getChildren().add(box);
    }

    private void showControlsPage() {
        menuLayer.getChildren().clear();

        Text title = new Text("CONTROLS");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text subtitle = new Text("Select control scheme");
        subtitle.setFont(smallFont);
        subtitle.setFill(COLOR_LABEL);
        subtitle.setStroke(COLOR_OUTLINE);
        subtitle.setStrokeWidth(1);

        Text wasdLabel = new Text("WASD");
        wasdLabel.setFont(pixelFont);
        wasdLabel.setStroke(COLOR_OUTLINE);
        wasdLabel.setStrokeWidth(1.5);

        Text arrowLabel = new Text("Arrow Keys");
        arrowLabel.setFont(pixelFont);
        arrowLabel.setStroke(COLOR_OUTLINE);
        arrowLabel.setStrokeWidth(1.5);

        Runnable updateColors = () -> {
            boolean wasd = listener.isWasdActive();
            wasdLabel.setFill(wasd ? COLOR_ACTIVE : COLOR_INACTIVE);
            arrowLabel.setFill(wasd ? COLOR_INACTIVE : COLOR_ACTIVE);
        };
        updateColors.run();

        wasdLabel.setCursor(Cursor.HAND);
        arrowLabel.setCursor(Cursor.HAND);

        wasdLabel.setOnMouseClicked(e -> {
            listener.onWasdSelected();
            updateColors.run();
        });
        arrowLabel.setOnMouseClicked(e -> {
            listener.onArrowKeysSelected();
            updateColors.run();
        });

        wasdLabel.setOnMouseEntered(e -> wasdLabel.setFill(COLOR_HOVER_A));
        wasdLabel.setOnMouseExited(e -> updateColors.run());
        arrowLabel.setOnMouseEntered(e -> arrowLabel.setFill(COLOR_HOVER_A));
        arrowLabel.setOnMouseExited(e -> updateColors.run());

        Text separator = new Text("/");
        separator.setFont(pixelFont);
        separator.setFill(COLOR_LABEL);
        separator.setStroke(COLOR_OUTLINE);
        separator.setStrokeWidth(1.5);

        HBox toggleRow = new HBox(30 * resolutionScale, wasdLabel, separator, arrowLabel);
        toggleRow.setAlignment(Pos.CENTER);

        Text backItem = createMenuItem("Back", this::showSettingsLanding);

        VBox box = new VBox(40 * resolutionScale, title, subtitle, toggleRow, backItem);
        box.setAlignment(Pos.CENTER);
        menuLayer.getChildren().add(box);
    }

    private void showAudioPage() {
        menuLayer.getChildren().clear();

        Text title = new Text("AUDIO");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        Text volumeLabel = new Text("Volume");
        volumeLabel.setFont(smallFont);
        volumeLabel.setFill(COLOR_LABEL);
        volumeLabel.setStroke(COLOR_OUTLINE);
        volumeLabel.setStrokeWidth(1);

        int currentVol = (int) Math.round(SoundManager.getVolume() * 100);
        Text volumeValue = new Text(currentVol + "%");
        volumeValue.setFont(pixelFont);
        volumeValue.setFill(COLOR_DEFAULT);
        volumeValue.setStroke(COLOR_OUTLINE);
        volumeValue.setStrokeWidth(1.5);

        Text minus = new Text("-");
        minus.setFont(pixelFont);
        minus.setFill(COLOR_DEFAULT);
        minus.setStroke(COLOR_OUTLINE);
        minus.setStrokeWidth(1.5);
        minus.setCursor(Cursor.HAND);

        Text plus = new Text("+");
        plus.setFont(pixelFont);
        plus.setFill(COLOR_DEFAULT);
        plus.setStroke(COLOR_OUTLINE);
        plus.setStrokeWidth(1.5);
        plus.setCursor(Cursor.HAND);

        minus.setOnMouseEntered(e -> minus.setFill(COLOR_HOVER_A));
        minus.setOnMouseExited(e -> minus.setFill(COLOR_DEFAULT));
        plus.setOnMouseEntered(e -> plus.setFill(COLOR_HOVER_A));
        plus.setOnMouseExited(e -> plus.setFill(COLOR_DEFAULT));

        minus.setOnMouseClicked(e -> {
            double vol = Math.max(0, SoundManager.getVolume() - 0.1);
            listener.onVolumeChanged(vol);
            volumeValue.setText(Math.round(vol * 100) + "%");
        });

        plus.setOnMouseClicked(e -> {
            double vol = Math.min(1, SoundManager.getVolume() + 0.1);
            listener.onVolumeChanged(vol);
            volumeValue.setText(Math.round(vol * 100) + "%");
        });

        HBox volumeRow = new HBox(25 * resolutionScale, minus, volumeValue, plus);
        volumeRow.setAlignment(Pos.CENTER);

        Text backItem = createMenuItem("Back", this::showSettingsLanding);

        VBox box = new VBox(40 * resolutionScale, title, volumeLabel, volumeRow, backItem);
        box.setAlignment(Pos.CENTER);
        menuLayer.getChildren().add(box);
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
        stage.setTitle("Prototype Panic - Settings");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
