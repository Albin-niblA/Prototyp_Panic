package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.weapon.WeaponType;
import util.settings.ControlScheme;
import util.settings.ControlSettings;
import view.ButtonType;
import view.GameListener;
import view.MainMenu;
import util.settings.SettingsListener;
import view.SettingsMenu;
import view.WeaponSelectDialog;

public class Main extends Application implements GameListener, SettingsListener {

    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;

    private Stage stage;
    private int width;
    private int height;
    private double resolutionScale;
    private GameController activeGame;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> shutdown());
        initResolution();
        showMainMenu();
    }

    private void initResolution() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        width = (int) screenBounds.getWidth();
        height = (int) screenBounds.getHeight();
        resolutionScale = Math.min((double) width / BASE_WIDTH, (double) height / BASE_HEIGHT);
        stage.setFullScreen(true);
    }

    private void showMainMenu() {
        new MainMenu(stage, this, width, height, resolutionScale).show();
    }

    private void shutdown() {
        if (activeGame != null) {
            activeGame.stop();
        }
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void onButtonClicked(ButtonType type) {
        switch (type) {
            case START -> {
                WeaponSelectDialog dialog = new WeaponSelectDialog(stage, resolutionScale);
                WeaponType weapon = dialog.showAndWait();
                GameController gc = new GameController(stage, width, height, resolutionScale, weapon);
                gc.setOnReturnToMenu(() -> {
                    activeGame = null;
                    showMainMenu();
                });
                activeGame = gc;
                gc.start();
            }
            case SETTINGS -> new SettingsMenu(stage, this, this::showMainMenu, width, height, resolutionScale).show();
            case EXIT -> shutdown();
        }
    }

    @Override
    public void onWasdSelected() {
        ControlSettings.setScheme(ControlScheme.WASD);
    }

    @Override
    public void onArrowKeysSelected() {
        ControlSettings.setScheme(ControlScheme.ARROW_KEYS);
    }

    @Override
    public boolean isWasdActive() {
        return ControlSettings.getScheme() == ControlScheme.WASD;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
