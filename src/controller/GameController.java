package controller;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.world.Camera;
import model.world.GameState;
import model.world.GameWorld;
import model.weapon.WeaponType;
import model.managers.SoundManager;
import view.GameRenderer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameController {

    private final Stage stage;
    private final GameWorld world;
    private final InputHandler input;
    private final GameRenderer renderer;
    private final Camera camera;
    private final AnimationTimer gameLoop;
    private final GraphicsContext gc;
    private final VBox pauseControls;

    private long lastTime = -1;
    private Runnable onReturnToMenu;

    public GameController(Stage stage, int width, int height, double resolutionScale, WeaponType weaponType) {
        this.stage = stage;

        SoundManager.init();

        this.camera = new Camera(width, height);
        this.world = new GameWorld(weaponType);
        this.input = new InputHandler();
        this.renderer = new GameRenderer(width, height, resolutionScale, camera, world.getUpgradeManager());

        Canvas canvas = new Canvas(width, height);
        this.gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        StackPane root = new StackPane(canvas);

        Slider volumeSlider = new Slider(0, 1, SoundManager.getVolume());
        volumeSlider.setPrefWidth(180 * resolutionScale);
        volumeSlider.setShowTickMarks(false);
        volumeSlider.setShowTickLabels(false);

        Label volumeLabel = new Label(Math.round(SoundManager.getVolume() * 100) + "%");
        volumeLabel.setStyle("-fx-text-fill: white; -fx-font-size: " + (int)(16 * resolutionScale) + "px;");

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setVolume(newVal.doubleValue());
            volumeLabel.setText(Math.round(newVal.doubleValue() * 100) + "%");
        });

        HBox sliderRow = new HBox(10 * resolutionScale, volumeSlider, volumeLabel);
        sliderRow.setAlignment(Pos.CENTER);

        Label soundTitle = new Label("Sound Volume");
        soundTitle.setStyle("-fx-text-fill: white; -fx-font-size: " + (int)(18 * resolutionScale) + "px;");

        pauseControls = new VBox(8 * resolutionScale, soundTitle, sliderRow);
        pauseControls.setAlignment(Pos.BOTTOM_CENTER);
        pauseControls.setPadding(new Insets(0, 0, height * 0.12, 0));
        pauseControls.setVisible(false);

        root.getChildren().add(pauseControls);
        Scene scene = new Scene(root, width, height);
        input.attachTo(scene);

        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setTitle("Prototype Panic");
        stage.setResizable(false);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                handleStateInput();
                world.update(delta, input, camera, now);
                camera.follow(world.getPlayer());
                renderer.render(gc, world);
                input.clearFrameState();
            }
        };
    }

    private void handleStateInput() {
        if (input.wasPressed(KeyCode.ESCAPE)) {
            if (world.getState() == GameState.RUNNING) {
                world.pause();
                pauseControls.setVisible(true);
            } else if (world.getState() == GameState.PAUSED) {
                world.resume();
                pauseControls.setVisible(false);
            }
        }

        if (input.wasPressed(KeyCode.M) &&
                (world.getState() == GameState.PAUSED || world.getState() == GameState.GAME_OVER)) {
            world.reset();
            pauseControls.setVisible(false);
            gameLoop.stop();
            if (onReturnToMenu != null) onReturnToMenu.run();
            return;
        }

        if (input.wasPressed(KeyCode.R) && world.getState() == GameState.GAME_OVER) {
            world.reset();
        }

        if (input.wasPressed(KeyCode.F4)) {
            world.upgrade();
        }

        if (input.wasMouseClicked()) {
            world.toggleShooting();
        }

        if (world.getState() == GameState.UPGRADE) {
            renderer.getOverlay().setMouseCoords(input.getMouseX(), input.getMouseY());
            if (input.wasMouseClicked()) {
                world.applyCardUpgrade(renderer.getOverlay().getClickedCard());
            }
            world.resume();
        }
    }

    public void setOnReturnToMenu(Runnable callback) {
        this.onReturnToMenu = callback;
    }

    public void start() {
        stage.show();
        gameLoop.start();
    }

    public void stop() {
        gameLoop.stop();
    }
}
