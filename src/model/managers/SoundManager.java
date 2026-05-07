package model.managers;

import javafx.scene.media.AudioClip;

public class SoundManager {
    private static AudioClip shootSound;
    private static AudioClip hitSound;
    private static AudioClip deathSound;
    private static boolean initialized = false;
    private static double volume = 1.0;

    public static void init() {
        if (initialized) return;
        shootSound = loadClip("/util/sounds/shoot.wav");
        // Ready for when sound files are added:
        hitSound = loadClip("/util/sounds/taking_damage.wav");
        // deathSound = loadClip("/util/sounds/death.wav");
        initialized = true;
    }

    private static AudioClip loadClip(String path) {
        var url = SoundManager.class.getResource(path);
        return url != null ? new AudioClip(url.toExternalForm()) : null;
    }

    public static void setVolume(double v) {
        volume = Math.max(0.0, Math.min(1.0, v));
        if (shootSound != null) shootSound.setVolume(volume);
        if (hitSound   != null) hitSound.setVolume(volume);
    }

    public static double getVolume() {
        return volume;
    }

    public static void playShoot() { if (shootSound != null) shootSound.play(volume);}
    public static void playHit() { if (hitSound != null) hitSound.play(volume); }
    public static void playDeath() { if (deathSound != null) deathSound.play(); }
}
