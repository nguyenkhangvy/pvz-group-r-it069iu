package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pvz.core.GameConfig;

/**
 * SaveManager (SINGLETON - Creational pattern).
 *
 * Theo yeu cau:
 *  - Save game CHI luu lastUnlockedLevel (so nguyen 1..8).
 *  - Continue -> vao choosing plant screen cua level cuoi da unlock.
 *  - Thua level X -> save GIU nguyen X (khong tut).
 *  - Thang level X -> save = X+1.
 *  - Hoan thanh het (thang 1-8) -> reset ve 1.
 *  - Music setting luu RIENG (file khac), khong chung voi save game.
 *
 * Cac cay unlock duoc TINH TU lastUnlockedLevel (khong luu danh sach cay).
 */
public final class SaveManager {

    private static final String SAVE_FILE = "pvz_save.json";
    private static final String SETTINGS_FILE = "pvz_settings.json";

    private static SaveManager instance;

    private SaveData saveData;
    private SettingsData settingsData;

    private SaveManager() {
        load();
    }

    public static SaveManager get() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }

    // ----- cau truc du lieu luu xuong dia -----
    public static class SaveData {
        public int lastUnlockedLevel = GameConfig.FIRST_LEVEL; // 1..8
    }

    public static class SettingsData {
        public boolean themeMusicOn = true;
        public boolean clickSoundOn = true;
        public boolean gameSoundOn = true;
    }

    // ----- nap / ghi -----
    private void load() {
        Json json = new Json();
        FileHandle save = Gdx.files.local(SAVE_FILE);
        saveData = save.exists() ? json.fromJson(SaveData.class, save) : new SaveData();

        FileHandle settings = Gdx.files.local(SETTINGS_FILE);
        settingsData = settings.exists() ? json.fromJson(SettingsData.class, settings) : new SettingsData();
    }

    public void persistSave() {
        new Json().toJson(saveData, Gdx.files.local(SAVE_FILE));
    }

    public void persistSettings() {
        new Json().toJson(settingsData, Gdx.files.local(SETTINGS_FILE));
    }

    // ----- level -----
    public int getLastUnlockedLevel() { return saveData.lastUnlockedLevel; }

    /**
     * Goi khi bam "Start New": dat lai tien do ve level 1 va luu xuong.
     * He qua: Continue sau do cung se vao level 1 (tien do cu bi xoa).
     */
    public void resetToFirstLevel() {
        saveData.lastUnlockedLevel = GameConfig.FIRST_LEVEL;
        persistSave();
    }

    /** Goi khi THANG level `level`. Mo khoa level tiep; neu het thi reset ve 1. */
    public void onLevelWon(int level) {
        if (level >= GameConfig.LAST_LEVEL) {
            saveData.lastUnlockedLevel = GameConfig.FIRST_LEVEL; // hoan thanh -> reset
        } else if (level >= saveData.lastUnlockedLevel) {
            saveData.lastUnlockedLevel = level + 1;
        }
        persistSave();
    }

    /** Thua thi khong thay doi save (giu nguyen). Ham nay de cho ro y dinh. */
    public void onLevelLost(int level) {
        // intentionally no-op
    }

    // ----- music settings -----
    public SettingsData settings() { return settingsData; }
}
