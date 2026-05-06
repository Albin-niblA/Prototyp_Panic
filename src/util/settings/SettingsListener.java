package util.settings;

public interface SettingsListener {
    void onWasdSelected();
    void onArrowKeysSelected();
    boolean isWasdActive();
    void onVolumeChanged(double volume);
}
