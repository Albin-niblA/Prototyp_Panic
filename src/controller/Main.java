package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import model.weapon.WeaponType;
import view.ButtonType;
import view.GameListener;
import view.MainMenu;
import view.WeaponSelectDialog;

public class Main extends Application implements GameListener {

    private Stage stage;
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 900;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        showMainMenu();
    }

    private void showMainMenu() {
        new MainMenu(stage, this, WIDTH, HEIGHT).show();
    }

    @Override
    public void onButtonClicked(ButtonType type) {
        switch (type) {
            case START -> {
                WeaponSelectDialog dialog = new WeaponSelectDialog(stage);
                WeaponType weapon = dialog.showAndWait();
                GameController gc = new GameController(stage, WIDTH, HEIGHT, weapon);
                gc.setOnReturnToMenu(this::showMainMenu);
                gc.start();
            }
            case EXIT -> Platform.exit();
            default -> { }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
