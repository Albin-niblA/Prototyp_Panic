package model;

// Main class for handling enemies that pop up on the screen
public abstract class Enemy extends Entity {
    private int textureID;
    public void update() {

    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }
}
