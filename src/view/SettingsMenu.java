package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.managers.SoundManager;
import util.settings.SettingsListener;

public class SettingsMenu {
    private static final String ACTIVE_STYLE =
            "-fx-border-width: 4; -fx-border-color: #4a9eff; -fx-background-color: transparent; -fx-cursor: hand;";
    private static final String INACTIVE_STYLE =
            "-fx-border-width: 4; -fx-border-color: transparent; -fx-background-color: transparent; -fx-cursor: hand;";
    private static final String TAB_ACTIVE =
            "-fx-background-color: #4a9eff; -fx-text-fill: white; -fx-font-size: %spx; -fx-cursor: hand; -fx-padding: 8 24 8 24;";
    private static final String TAB_INACTIVE =
            "-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-font-size: %spx; -fx-cursor: hand; -fx-padding: 8 24 8 24; -fx-border-color: #aaaaaa; -fx-border-width: 2;";

    private final Stage stage;
    private final Scene scene;
    private final VBox wasdBox;
    private final VBox arrowBox;
    private final SettingsListener listener;
    private final double resolutionScale;

    public SettingsMenu(Stage stage, SettingsListener listener, Runnable onBack, int width, int height, double resolutionScale) {
        this.stage = stage;
        this.listener = listener;
        this.resolutionScale = resolutionScale;

        Text title = new Text("Settings");
        title.setFont(new Font(40 * resolutionScale));

        // --- Tab buttons ---
        Button controlsTab = new Button("Controls");
        Button audioTab = new Button("Audio");

        // --- Controls panel ---
        wasdBox = createImageBox("WASD", "/util/images/assets/WASD_Keys.png");
        arrowBox = createImageBox("Arrow keys", "/util/images/assets/Arrow_Keys.png");

        wasdBox.setOnMouseClicked(e -> {
            listener.onWasdSelected();
            updateHighlight();
        });
        arrowBox.setOnMouseClicked(e -> {
            listener.onArrowKeysSelected();
            updateHighlight();
        });
        updateHighlight();

        Text controlsSubtitle = new Text("Select control scheme");
        controlsSubtitle.setFont(new Font(18 * resolutionScale));

        HBox controlsRow = new HBox(60 * resolutionScale, wasdBox, arrowBox);
        controlsRow.setAlignment(Pos.CENTER);

        VBox controlsPanel = new VBox(25 * resolutionScale, controlsSubtitle, controlsRow);
        controlsPanel.setAlignment(Pos.CENTER);

        // --- Audio panel ---
        Text soundLabel = new Text("Sound Volume");
        soundLabel.setFont(new Font(18 * resolutionScale));

        Slider volumeSlider = new Slider(0, 1, SoundManager.getVolume());
        volumeSlider.setPrefWidth(180 * resolutionScale);
        volumeSlider.setShowTickMarks(false);
        volumeSlider.setShowTickLabels(false);

        Text volumeValue = new Text(Math.round(SoundManager.getVolume() * 100) + "%");
        volumeValue.setFont(new Font(16 * resolutionScale));

        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            listener.onVolumeChanged(newVal.doubleValue());
            volumeValue.setText(Math.round(newVal.doubleValue() * 100) + "%");
        });

        HBox sliderRow = new HBox(10 * resolutionScale, volumeSlider, volumeValue);
        sliderRow.setAlignment(Pos.CENTER);

        VBox audioPanel = new VBox(8 * resolutionScale, soundLabel, sliderRow);
        audioPanel.setAlignment(Pos.CENTER);

        // --- Content area (swaps between panels) ---
        VBox contentArea = new VBox();
        contentArea.setAlignment(Pos.CENTER);
        contentArea.setMinHeight(300 * resolutionScale);
        contentArea.getChildren().add(controlsPanel);

        // --- Tab switching logic ---
        String tabFontSize = String.valueOf((int)(16 * resolutionScale));
        controlsTab.setStyle(String.format(TAB_ACTIVE, tabFontSize));
        audioTab.setStyle(String.format(TAB_INACTIVE, tabFontSize));

        controlsTab.setOnAction(e -> {
            contentArea.getChildren().setAll(controlsPanel);
            controlsTab.setStyle(String.format(TAB_ACTIVE, tabFontSize));
            audioTab.setStyle(String.format(TAB_INACTIVE, tabFontSize));
        });
        audioTab.setOnAction(e -> {
            contentArea.getChildren().setAll(audioPanel);
            audioTab.setStyle(String.format(TAB_ACTIVE, tabFontSize));
            controlsTab.setStyle(String.format(TAB_INACTIVE, tabFontSize));
        });

        HBox tabRow = new HBox(12 * resolutionScale, controlsTab, audioTab);
        tabRow.setAlignment(Pos.CENTER);

        // --- Back button ---
        Button backButton = new Button("Back to main menu");
        backButton.setPrefWidth(250 * resolutionScale);
        backButton.setPrefHeight(50 * resolutionScale);
        backButton.setOnAction(e -> onBack.run());

        VBox layout = new VBox(25 * resolutionScale);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, tabRow, contentArea, backButton);

        scene = new Scene(layout, width, height);
    }

    private VBox createImageBox(String label, String imagePath) {
        Image img = new Image(getClass().getResourceAsStream(imagePath), 200 * resolutionScale, 200 * resolutionScale, true, false);
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(200 * resolutionScale);
        imageView.setFitHeight(200 * resolutionScale);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);

        Text labelText = new Text(label);
        labelText.setFont(new Font(20 * resolutionScale));

        VBox box = new VBox(12 * resolutionScale, imageView, labelText);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16 * resolutionScale));
        box.setStyle(INACTIVE_STYLE);
        return box;
    }

    private void updateHighlight() {
        boolean wasdActive = listener.isWasdActive();
        wasdBox.setStyle(wasdActive ? ACTIVE_STYLE : INACTIVE_STYLE);
        arrowBox.setStyle(wasdActive ? INACTIVE_STYLE : ACTIVE_STYLE);
    }

    public void show() {
        stage.setTitle("Prototype Panic - Settings");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}