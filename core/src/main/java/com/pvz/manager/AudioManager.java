package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * AudioManager (SINGLETON): quan ly 3 loai am thanh theo yeu cau:
 *  - theme music (nhac nen, lap)
 *  - click sound (tieng bam nut)
 *  - game sound (tieng trong game: ban, an...)
 *
 * On/Off doc tu SaveManager.settings() (luu file rieng).
 * Khi chua co file am thanh that, cac ham se bo qua an toan (khong crash).
 */
public final class AudioManager {

    private static AudioManager instance;

    private Music themeMusic;

    private AudioManager() {}

    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // ----- THEME -----
    /** Goi khi muon phat nhac nen. path vd: "audio/theme.ogg". */
    public void playTheme(String path) {
        if (!SaveManager.get().settings().themeMusicOn) return;
        FileHandle f = Gdx.files.internal(path);
        if (!f.exists()) { Gdx.app.log("AudioManager", "Chua co nhac: " + path); return; }
        if (themeMusic == null) {
            themeMusic = Gdx.audio.newMusic(f);
            themeMusic.setLooping(true);
        }
        themeMusic.play();
    }

    public void stopTheme() {
        if (themeMusic != null) themeMusic.stop();
    }

    public void setThemeOn(boolean on) {
        SaveManager.get().settings().themeMusicOn = on;
        SaveManager.get().persistSettings();
        if (!on) stopTheme();
    }

    // ----- CLICK -----
    public void playClick(String path) {
        if (!SaveManager.get().settings().clickSoundOn) return;
        playOneShot(path);
    }

    public void setClickOn(boolean on) {
        SaveManager.get().settings().clickSoundOn = on;
        SaveManager.get().persistSettings();
    }

    // ----- GAME SOUND -----
    public void playGameSound(String path) {
        if (!SaveManager.get().settings().gameSoundOn) return;
        playOneShot(path);
    }

    public void setGameSoundOn(boolean on) {
        SaveManager.get().settings().gameSoundOn = on;
        SaveManager.get().persistSettings();
    }

    private void playOneShot(String path) {
        FileHandle f = Gdx.files.internal(path);
        if (!f.exists()) { Gdx.app.log("AudioManager", "Chua co sound: " + path); return; }
        Sound s = Gdx.audio.newSound(f);
        s.play();
    }

    public void dispose() {
        if (themeMusic != null) themeMusic.dispose();
    }
}
