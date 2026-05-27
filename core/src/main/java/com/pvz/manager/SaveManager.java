package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pvz.core.Difficulty;
import com.pvz.core.GameConfig;

/**
 * SaveManager (SINGLETON).
 *
 * Ho tro 3 DO KHO (Easy/Normal/Hard), moi do kho luu RIENG:
 *  - lastUnlockedLevel cua rieng do kho do (easyLevel/normalLevel/hardLevel)
 *  - co "da hoan thanh full" cua do kho do (easyDone/normalDone/hardDone)
 *
 * Khoa tien trinh:
 *  - Normal chi mo khi easyDone = true (hoan thanh full Easy).
 *  - Hard chi mo khi normalDone = true.
 *
 * Khi doi do kho, KHONG mat tien do: moi do kho giu lastUnlockedLevel rieng,
 * nen Hard->Easy thi Easy ve dung level dang choi do cua Easy.
 *
 * Music setting van luu RIENG (SETTINGS_FILE).
 */
public final class SaveManager {

    private static final String SAVE_FILE = "pvz_save.json";
    private static final String SETTINGS_FILE = "pvz_settings.json";

    private static SaveManager instance;

    private SaveData saveData;
    private SettingsData settingsData;

    private SaveManager() { load(); }

    public static SaveManager get() {
        if (instance == null) instance = new SaveManager();
        return instance;
    }

    // ----- cau truc luu xuong dia -----
    public static class SaveData {
        public String difficulty = "EASY";   // do kho dang chon

        public int easyLevel = GameConfig.FIRST_LEVEL;
        public int normalLevel = GameConfig.FIRST_LEVEL;
        public int hardLevel = GameConfig.FIRST_LEVEL;

        public boolean easyDone = false;      // da hoan thanh full Easy chua
        public boolean normalDone = false;    // da hoan thanh full Normal chua
        public boolean hardDone = false;      // da hoan thanh full Hard chua
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

    public void persistSave() { new Json().toJson(saveData, Gdx.files.local(SAVE_FILE)); }
    public void persistSettings() { new Json().toJson(settingsData, Gdx.files.local(SETTINGS_FILE)); }

    // ===================== DO KHO =====================
    public Difficulty getDifficulty() { return Difficulty.fromName(saveData.difficulty); }

    public void setDifficulty(Difficulty d) {
        saveData.difficulty = d.name();
        persistSave();
    }

    /** Do kho `d` co duoc mo khong (theo dieu kien hoan thanh muc truoc). */
    public boolean isDifficultyUnlocked(Difficulty d) {
        switch (d) {
            case EASY:   return true;                 // luon mo
            case NORMAL: return saveData.easyDone;     // can hoan thanh Easy
            case HARD:   return saveData.normalDone;   // can hoan thanh Normal
            default:     return false;
        }
    }

    public boolean isDone(Difficulty d) {
        switch (d) {
            case EASY:   return saveData.easyDone;
            case NORMAL: return saveData.normalDone;
            case HARD:   return saveData.hardDone;
            default:     return false;
        }
    }

    // ===================== LEVEL (theo do kho dang chon) =====================
    public int getLastUnlockedLevel() { return levelOf(getDifficulty()); }

    private int levelOf(Difficulty d) {
        switch (d) {
            case EASY:   return saveData.easyLevel;
            case NORMAL: return saveData.normalLevel;
            case HARD:   return saveData.hardLevel;
            default:     return GameConfig.FIRST_LEVEL;
        }
    }

    private void setLevelOf(Difficulty d, int lv) {
        switch (d) {
            case EASY:   saveData.easyLevel = lv; break;
            case NORMAL: saveData.normalLevel = lv; break;
            case HARD:   saveData.hardLevel = lv; break;
        }
    }

    private void setDoneOf(Difficulty d, boolean done) {
        switch (d) {
            case EASY:   saveData.easyDone = done; break;
            case NORMAL: saveData.normalDone = done; break;
            case HARD:   saveData.hardDone = done; break;
        }
    }

    /**
     * Bam "Start New": dat lai tien do ve level 1 cho DO KHO DANG CHON (cac do
     * kho khac giu nguyen). Khong dung co "done" (van giu thanh tich da hoan thanh).
     */
    public void resetToFirstLevel() {
        setLevelOf(getDifficulty(), GameConfig.FIRST_LEVEL);
        persistSave();
    }

    /** Thang level `level` o do kho dang chon. Mo khoa level tiep; het thi danh dau done + reset ve 1. */
    public void onLevelWon(int level) {
        Difficulty d = getDifficulty();
        if (level >= GameConfig.LAST_LEVEL) {
            setDoneOf(d, true);                          // hoan thanh full -> danh dau done
            setLevelOf(d, GameConfig.FIRST_LEVEL);       // reset ve 1 (choi lai duoc)
        } else if (level >= levelOf(d)) {
            setLevelOf(d, level + 1);
        }
        persistSave();
    }

    /** Thua thi giu nguyen (no-op). */
    public void onLevelLost(int level) { }

    // ----- music settings -----
    public SettingsData settings() { return settingsData; }
}
