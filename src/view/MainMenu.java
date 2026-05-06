package view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;

public class MainMenu {

    private final Stage stage;
    private final Scene scene;

    private static final Color COLOR_DEFAULT = Color.web("#4488FF");
    private static final Color COLOR_HOVER_A = Color.web("#CC44FF");
    private static final Color COLOR_HOVER_B = Color.WHITE;
    private static final Color COLOR_OUTLINE = Color.BLACK;
    private static final Color COLOR_TITLE = Color.GREEN;

    public MainMenu(Stage stage, GameListener listener, int width, int height, double resolutionScale) {
        this.stage = stage;

        Font pixelFont = loadPixelFont(20 * resolutionScale);
        Font titleFont = loadPixelFont(34 * resolutionScale);

        Text title = new Text("PROTOTYPE PANIC");
        title.setFont(titleFont);
        title.setFill(COLOR_TITLE);

        VBox layout = new VBox(40 * resolutionScale);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(title);

        for (ButtonType type : ButtonType.values()) {
            Text item = createMenuItem(type, pixelFont, listener);
            layout.getChildren().add(item);
        }

        scene = new Scene(layout, width, height);
    }

    private Text createMenuItem(ButtonType type, Font font, GameListener listener) {
        Text item = new Text(type.getLabel());
        item.setFont(font);
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
            item.setText("> " + type.getLabel() + " <");
            item.setFill(COLOR_HOVER_A);
            toggle[0] = false;
            hoverAnim.playFromStart();
        });

        item.setOnMouseExited(e -> {
            hoverAnim.stop();
            item.setText(type.getLabel());
            item.setFill(COLOR_DEFAULT);
        });

        item.setOnMouseClicked(e -> listener.onButtonClicked(type));

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
        stage.setTitle("Prototype Panic");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
