package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.weapon.WeaponType;

public class WeaponSelectDialog {

    private final Stage dialog;
    private WeaponType selectedWeapon = WeaponType.BULLET;

    public WeaponSelectDialog(Stage owner, double resolutionScale) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Weapon Select");

        ToggleGroup group = new ToggleGroup();
        WeaponType[] types = WeaponType.values();

        VBox layout = new VBox(10 * resolutionScale);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20 * resolutionScale));
        layout.getChildren().add(new Label("Choose your weapon:"));

        for (int i = 0; i < types.length; i++) {
            RadioButton rb = new RadioButton(types[i].getDisplayName());
            rb.setToggleGroup(group);
            WeaponType wt = types[i];
            rb.setOnAction(e -> selectedWeapon = wt);
            if (i == 0) rb.setSelected(true);
            layout.getChildren().add(rb);
        }

        Button confirm = new Button("Start");
        confirm.setOnAction(e -> dialog.close());
        layout.getChildren().add(confirm);

        dialog.setScene(new Scene(layout, 200 * resolutionScale, 200 * resolutionScale));
    }

    public WeaponType showAndWait() {
        dialog.showAndWait();
        return selectedWeapon;
    }
}
