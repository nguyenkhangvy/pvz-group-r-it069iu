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
    public static final String HIT_ZOMBIE = "splat";        // pea trung zombie
    public static final String CHOMP      = "chomp2";       // chomper nhai
    public static final String VAULT      = "splat2";       // pole vault nhay qua cay
    public static final String ZOMBIE_DIE = "groan";        // zombie het mau (chet)
    public static final String GULP       = "gulp";         // zombie nuot khi cay ve 0 hp
    public static final String SHOVEL     = "shovel";       // xen go cay
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

    // AssetManager do LoadingScreen nap san va ban giao (co the null neu khong dung loading).
    private com.badlogic.gdx.assets.AssetManager assets;
    private String assetDir = AUDIO_DIR;

    private AudioManager() {}

    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    /**
     * Nhan AssetManager da nap san tu LoadingScreen. Tu day getSound()/theme se
     * lay tu day truoc (da nap san -> khong giat), chi fallback nap lazy neu thieu.
     */
    public void attachAssetManager(com.badlogic.gdx.assets.AssetManager am, String dir) {
        this.assets = am;
        this.assetDir = dir;
    }

    /** Tim duong dan day du cua file am thanh trong AssetManager (thu .wav/.ogg/.mp3). */
    private String assetPath(String name) {
        if (name == null) return null;
        for (String ext : EXTS) {
            String path = assetDir + name + ext;
            if (assets != null && assets.isLoaded(path)) return path;
        }
        return null;
    }

    /**
     * Nap san (cache) cac sound quan trong de lan phat dau tien khong bi tre.
     * Goi 1 lan khi khoi dong game.
     */
    public void preload(String... names) {
        for (String name : names) getSound(name);
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
            // 1) Uu tien: Music da nap san trong AssetManager (LoadingScreen)
            String path = assetPath(themeKey);
            if (path != null && assets != null) {
                themeMusic = assets.get(path, com.badlogic.gdx.audio.Music.class);
            } else {
                // 2) Fallback: nap lazy tu dia
                FileHandle f = resolve(themeKey);
                if (f == null) { Gdx.app.log("AudioManager", "Chua co nhac nen: " + themeKey); return; }
                themeMusic = Gdx.audio.newMusic(f);
            }
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

        // 1) Uu tien: lay tu AssetManager da nap san o LoadingScreen (khong giat)
        String path = assetPath(name);
        if (path != null) {
            Sound s = assets.get(path, Sound.class);
            soundCache.put(name, s);
            return s;
        }

        // 2) Fallback: nap lazy tu dia (cho truong hop chua qua LoadingScreen)
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

    /**
     * Dao trang thai ON/OFF cua 1 trong 3 kenh am thanh.
     * index: 0=theme, 1=click, 2=gameSound.
     * Gom logic trung lap tu PauseMenu va MusicScreen vao 1 cho duy nhat.
     */
    public void toggleSetting(int index) {
        SaveManager.SettingsData s = SaveManager.get().settings();
        switch (index) {
            case 0: setThemeOn(!s.themeMusicOn); break;
            case 1: setClickOn(!s.clickSoundOn); break;
            case 2: setGameSoundOn(!s.gameSoundOn); break;
        }
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
        // Neu da ban giao AssetManager: chinh no se dispose toan bo Sound/Music
        // ma no giu. Ta KHONG tu dispose nhung cai do (tranh double-dispose -> crash).
        if (assets != null) {
            assets.dispose();
            assets = null;
        } else {
            // Truong hop khong qua LoadingScreen: ta tu nap lazy nen tu dispose.
            if (themeMusic != null) themeMusic.dispose();
            for (Sound s : soundCache.values()) s.dispose();
        }
        themeMusic = null;
        soundCache.clear();
    }
}
