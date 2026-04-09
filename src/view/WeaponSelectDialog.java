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

    public WeaponSelectDialog(Stage owner) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);

        Label title = new Label("Choose your weapon:");

        ToggleGroup group = new ToggleGroup();
        WeaponType[] types = WeaponType.values();
        RadioButton[] buttons = new RadioButton[types.length];

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setPadding(new Insets(20));
        layout.getChildren().add(title);

        for (int i = 0; i < types.length; i++) {
            buttons[i] = new RadioButton(types[i].getDisplayName());
            buttons[i].setToggleGroup(group);
            final WeaponType wt = types[i];
            buttons[i].setOnAction(e -> selectedWeapon = wt);
            layout.getChildren().add(buttons[i]);
        }
        buttons[0].setSelected(true);

        Button confirm = new Button("Start");
        confirm.setOnAction(e -> dialog.close());
        layout.getChildren().add(confirm);

        dialog.setScene(new Scene(layout, 200, 200));
        dialog.setTitle("Weapon Select");
    }

    public WeaponType showAndWait() {
        dialog.showAndWait();
        return selectedWeapon;
    }
}
