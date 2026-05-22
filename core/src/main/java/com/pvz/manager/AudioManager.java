package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * AudioManager (SINGLETON): quan ly 3 loai am thanh:
 *  - theme music (nhac nen, lap) -> Music (stream tu dia, cho file dai)
 *  - click sound (tieng bam nut)  -> Sound (nap san vao RAM, cho file ngan)
 *  - game sound (ban, an, no...)  -> Sound
 *
 * On/Off doc tu SaveManager.settings() (luu file rieng).
 *
 * QUAN TRONG ve hieu nang:
 *  - Sound duoc CACHE: moi file chi nap 1 lan, phat lai nhieu lan. KHONG nap
 *    lai tu dia moi lan ban (tranh lag + ro ri bo nho).
 *  - Tat ca file dat trong assets/audio/. Ta chi truyen TEN FILE (vd "shoot"),
 *    AudioManager tu ghep duong dan + tu thu cac duoi pho bien (.ogg, .mp3, .wav).
 *  - Thieu file -> bo qua an toan, khong crash.
 *
 * Cach dung tu cho khac:
 *   AudioManager.get().playTheme(AudioManager.THEME);
 *   AudioManager.get().playClick();
 *   AudioManager.get().playGameSound(AudioManager.SHOOT);
 */
public final class AudioManager {

    private static final String AUDIO_DIR = "audio/";
    private static final String[] EXTS = { ".ogg", ".mp3", ".wav" };

    // ----- Ten file am thanh (KHONG co duoi). Ban dat file dung ten nay -----
    public static final String THEME      = "theme";        // nhac nen
    public static final String CLICK      = "click";        // bam nut
    public static final String SHOOT      = "shoot";        // cay ban dan
    public static final String PLANT      = "plant";        // dat cay xuong
    public static final String SUN_PICK   = "sun";          // nhat sun
    public static final String ZOMBIE_EAT = "zombie_eat";   // zombie an cay
    public static final String EXPLODE    = "explode";      // cherry/potato no
    public static final String MOWER      = "mower";        // may xen co chay
    public static final String WIN        = "win";          // thang man
    public static final String LOSE       = "lose";         // thua man

    private static AudioManager instance;

    private Music themeMusic;
    private String themeKey;                                // ten theme dang phat
    private final ObjectMap<String, Sound> soundCache = new ObjectMap<>();
    private final ObjectMap<String, Boolean> missing = new ObjectMap<>(); // file da biet la khong co

    private AudioManager() {}

    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    /** Tim FileHandle theo ten (thu .ogg/.mp3/.wav). Tra null neu khong co. */
    private FileHandle resolve(String name) {
        if (name == null) return null;
        for (String ext : EXTS) {
            FileHandle f = Gdx.files.internal(AUDIO_DIR + name + ext);
            if (f.exists()) return f;
        }
        return null;
    }

    // ===================== THEME (Music) =====================
    /** Phat nhac nen (lap). name vd AudioManager.THEME. */
    public void playTheme(String name) {
        themeKey = name;
        if (!SaveManager.get().settings().themeMusicOn) return;
        startThemeIfNeeded();
    }

    private void startThemeIfNeeded() {
        if (themeKey == null) return;
        if (themeMusic == null) {
            FileHandle f = resolve(themeKey);
            if (f == null) { Gdx.app.log("AudioManager", "Chua co nhac nen: " + themeKey); return; }
            themeMusic = Gdx.audio.newMusic(f);
            themeMusic.setLooping(true);
            themeMusic.setVolume(0.6f);
        }
        if (!themeMusic.isPlaying()) themeMusic.play();
    }

    public void stopTheme() {
        if (themeMusic != null) themeMusic.stop();
    }

    public void setThemeOn(boolean on) {
        SaveManager.get().settings().themeMusicOn = on;
        SaveManager.get().persistSettings();
        if (on) startThemeIfNeeded();
        else stopTheme();
    }

    // ===================== CLICK & GAME SOUND (Sound) =====================
    /** Tieng bam nut. */
    public void playClick() {
        if (!SaveManager.get().settings().clickSoundOn) return;
        play(CLICK, 1f);
    }

    /** Tieng trong game (ban, an, no...). name vd AudioManager.SHOOT. */
    public void playGameSound(String name) {
        if (!SaveManager.get().settings().gameSoundOn) return;
        play(name, 1f);
    }

    public void playGameSound(String name, float volume) {
        if (!SaveManager.get().settings().gameSoundOn) return;
        play(name, volume);
    }

    /** Nap (1 lan) va phat 1 sound. */
    private void play(String name, float volume) {
        Sound s = getSound(name);
        if (s != null) s.play(volume);
    }

    private Sound getSound(String name) {
        if (soundCache.containsKey(name)) return soundCache.get(name);
        if (missing.containsKey(name)) return null;       // da biet khong co
        FileHandle f = resolve(name);
        if (f == null) {
            Gdx.app.log("AudioManager", "Chua co sound: " + name);
            missing.put(name, true);
            return null;
        }
        Sound s = Gdx.audio.newSound(f);
        soundCache.put(name, s);
        return s;
    }

    public void setClickOn(boolean on) {
        SaveManager.get().settings().clickSoundOn = on;
        SaveManager.get().persistSettings();
    }

    public void setGameSoundOn(boolean on) {
        SaveManager.get().settings().gameSoundOn = on;
        SaveManager.get().persistSettings();
    }

    // ===================== DISPOSE =====================
    public void dispose() {
        if (themeMusic != null) { themeMusic.dispose(); themeMusic = null; }
        for (Sound s : soundCache.values()) s.dispose();
        soundCache.clear();
    }
}
