package util.sounds;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip shootSound;
    private static AudioClip hitSound;
    private static AudioClip deathSound;

    public static void init() {
        shootSound = new AudioClip(SoundManager.class.getResource("/util/sounds/shoot.wav").toExternalForm());
    }

    public static void playShoot() { shootSound.play(); }
}