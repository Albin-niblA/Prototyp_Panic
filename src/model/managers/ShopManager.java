package model.managers;

import model.entities.Player;
import model.items.Item;

import java.util.List;

public class ShopManager {
    private final Player player;
    private final List<Item> shopItems = null;
    private List<Item> playerInventory;

    public ShopManager(Player player) {
        this.player = player;
    }
    private List<Item> initItems() {
        // Constructor format:
        // String name, int textureID, int price
        // int movementspeed, int health, int damage
        // double attackspeed, int onHit, int blinkDistance
        return List.of(
                new Item("Boots", 0, 100, 50, 0, 0, 0, 0, 0),
                new Item("Shadow boots", 1, 500, 80, 0, 0, 0, 0, 100),
                new Item("Shield", 2, 1000, 0, 100, 0, 0, 0, 0),
                new Item("Enchanted Dagger", 3, 2000, 20, 0, 25, 10, 15, 50)
        );
    }
}
