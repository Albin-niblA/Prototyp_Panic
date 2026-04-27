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

/*
* SKRIV INNEHÅLL HÄR
* */
public class Main extends Application implements GameListener, SettingsListener {

    private Stage stage;
    private static int WIDTH;
    private static int HEIGHT;
    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;
    //private static final int BASE_DPI = 96;
    private static double resolutionScale;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        setResolution();
        showMainMenu();
    }

    private void setResolution() {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        WIDTH = (int) screenBounds.getWidth();
        HEIGHT = (int) screenBounds.getHeight();
        // DPI compensation gave weird results, skip it
        //int dpi = (int) Screen.getPrimary().getDpi();
        resolutionScale = Math.min((double) WIDTH / BASE_WIDTH, (double) HEIGHT / BASE_HEIGHT);
        System.out.println("Screen width " + WIDTH + " vs base " + BASE_WIDTH);
        System.out.println("Screen height " + HEIGHT + " vs base " + BASE_HEIGHT);
        System.out.println("Scale: " + resolutionScale);
        stage.setFullScreen(true);
    }

    private void showMainMenu() {
        new MainMenu(stage, this, WIDTH, HEIGHT, resolutionScale).show();
    }

    @Override
    public void onButtonClicked(ButtonType type) {
        switch (type) {
            case START -> {
                WeaponSelectDialog dialog = new WeaponSelectDialog(stage, resolutionScale);
                WeaponType weapon = dialog.showAndWait();
                GameController gc = new GameController(stage, WIDTH, HEIGHT, resolutionScale, weapon);
                gc.setOnReturnToMenu(this::showMainMenu);
                gc.start();
            }
            case SETTINGS -> new SettingsMenu(stage, this, this::showMainMenu, WIDTH, HEIGHT, resolutionScale).show();
            case EXIT -> Platform.exit();
            default -> { }
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
