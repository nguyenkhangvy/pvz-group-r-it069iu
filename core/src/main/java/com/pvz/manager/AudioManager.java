package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Map;

/**
 * AudioManager - Quản lý toàn bộ âm thanh game PvZ.
 *
 * CÁCH DÙNG:
 *
 *   // Phát sound ngắn (bắn đậu, zombie chết, UI...)
 *   AudioManager.get().playGameSound(SoundKeys.PEASHOOTER_THROW);
 *   AudioManager.get().playClick(SoundKeys.UI_BUTTONCLICK);
 *
 *   // Phát nhạc nền (loop)
 *   AudioManager.get().playTheme(SoundKeys.INTRO_PHONOGRAPH);
 *
 *   // Phát nhạc một lần (win/lose/wave)
 *   AudioManager.get().playMusic(SoundKeys.WIN_MUSIC, false);
 *   AudioManager.get().playMusic(SoundKeys.GAMEPLAY_HUGEWAVE, false);
 *
 *   // Phát ngẫu nhiên (groan zombie)
 *   AudioManager.get().playRandom(
 *       SoundKeys.ZOMBIE_GROAN, SoundKeys.ZOMBIE_GROAN2,
 *       SoundKeys.ZOMBIE_GROAN3, SoundKeys.ZOMBIE_GROAN4
 *   );
 *
 *   // Pause/resume khi nhấn nút pause
 *   AudioManager.get().pauseTheme();
 *   AudioManager.get().resumeTheme();
 *
 *   // Dispose khi thoát game
 *   AudioManager.get().dispose();
 */
public final class AudioManager {

    private static AudioManager instance;

    // Cache sound để tránh memory leak (không newSound() mỗi lần nữa)
    private final Map<String, Sound> soundCache = new HashMap<>();

    // Nhạc nền loop (theme)
    private Music themeMusic;
    private String themePath;

    // Nhạc một lần (wave, win, lose)
    private Music oneShotMusic;

    private AudioManager() {}

    public static AudioManager get() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // ---------------------------------------------------------------
    // THEME MUSIC (nhạc nền, loop)
    // ---------------------------------------------------------------

    /** Phát nhạc nền loop. Nếu đang phát cùng bài thì không làm gì. */
    public void playTheme(String path) {
        if (!SaveManager.get().settings().themeMusicOn) return;
        if (path.equals(themePath) && themeMusic != null && themeMusic.isPlaying()) return;

        stopTheme();
        FileHandle f = Gdx.files.internal(path);
        if (!f.exists()) { Gdx.app.log("AudioManager", "Chua co nhac: " + path); return; }

        themeMusic = Gdx.audio.newMusic(f);
        themeMusic.setLooping(true);
        themeMusic.setVolume(0.7f);
        themeMusic.play();
        themePath = path;
    }

    public void stopTheme() {
        if (themeMusic != null) {
            themeMusic.stop();
            themeMusic.dispose();
            themeMusic = null;
            themePath = null;
        }
    }

    public void pauseTheme() {
        if (themeMusic != null && themeMusic.isPlaying()) themeMusic.pause();
    }

    public void resumeTheme() {
        if (themeMusic != null && !themeMusic.isPlaying()
                && SaveManager.get().settings().themeMusicOn) {
            themeMusic.play();
        }
    }

    public void setThemeOn(boolean on) {
        SaveManager.get().settings().themeMusicOn = on;
        SaveManager.get().persistSettings();
        if (!on) stopTheme();
    }

    // ---------------------------------------------------------------
    // ONE-SHOT MUSIC (wave lớn, win, lose — phát 1 lần, không loop)
    // ---------------------------------------------------------------

    /**
     * Phát nhạc một lần (win/lose/wave). Dừng bài cũ trước khi phát bài mới.
     * @param loop true = loop, false = phát 1 lần
     */
    public void playMusic(String path, boolean loop) {
        if (!SaveManager.get().settings().themeMusicOn) return;
        stopOneShotMusic();

        FileHandle f = Gdx.files.internal(path);
        if (!f.exists()) { Gdx.app.log("AudioManager", "Chua co music: " + path); return; }

        oneShotMusic = Gdx.audio.newMusic(f);
        oneShotMusic.setLooping(loop);
        oneShotMusic.setVolume(0.8f);
        oneShotMusic.play();
    }

    public void stopOneShotMusic() {
        if (oneShotMusic != null) {
            oneShotMusic.stop();
            oneShotMusic.dispose();
            oneShotMusic = null;
        }
    }

    // ---------------------------------------------------------------
    // CLICK SOUND (UI buttons)
    // ---------------------------------------------------------------

    public void playClick(String path) {
        if (!SaveManager.get().settings().clickSoundOn) return;
        playOneShot(path);
    }

    public void setClickOn(boolean on) {
        SaveManager.get().settings().clickSoundOn = on;
        SaveManager.get().persistSettings();
    }

    // ---------------------------------------------------------------
    // GAME SOUND (plants, zombies, gameplay)
    // ---------------------------------------------------------------

    public void playGameSound(String path) {
        if (!SaveManager.get().settings().gameSoundOn) return;
        playOneShot(path);
    }

    /**
     * Phát ngẫu nhiên 1 trong nhiều sound — dùng cho groan zombie.
     * Ví dụ:
     *   AudioManager.get().playRandom(
     *       SoundKeys.ZOMBIE_GROAN, SoundKeys.ZOMBIE_GROAN2,
     *       SoundKeys.ZOMBIE_GROAN3, SoundKeys.ZOMBIE_GROAN4
     *   );
     */
    public void playRandom(String... paths) {
        if (!SaveManager.get().settings().gameSoundOn) return;
        if (paths.length == 0) return;
        int idx = (int)(Math.random() * paths.length);
        playOneShot(paths[idx]);
    }

    public void setGameSoundOn(boolean on) {
        SaveManager.get().settings().gameSoundOn = on;
        SaveManager.get().persistSettings();
    }

    // ---------------------------------------------------------------
    // PAUSE / RESUME toàn bộ (gọi từ ApplicationListener)
    // ---------------------------------------------------------------

    /** Gọi trong ApplicationListener.pause() khi game bị minimize. */
    public void onAppPause() {
        pauseTheme();
    }

    /** Gọi trong ApplicationListener.resume() khi game mở lại. */
    public void onAppResume() {
        resumeTheme();
    }

    // ---------------------------------------------------------------
    // INTERNAL - cache sound để tránh memory leak
    // ---------------------------------------------------------------

    /**
     * FIX so với code cũ: dùng cache thay vì newSound() mỗi lần.
     * Sound được giữ trong RAM và tái sử dụng — không leak nữa.
     */
    private void playOneShot(String path) {
        Sound sound = soundCache.get(path);
        if (sound == null) {
            FileHandle f = Gdx.files.internal(path);
            if (!f.exists()) {
                Gdx.app.log("AudioManager", "Chua co sound: " + path);
                return;
            }
            sound = Gdx.audio.newSound(f);
            soundCache.put(path, sound);
        }
        sound.play();
    }

    // ---------------------------------------------------------------
    // DISPOSE
    // ---------------------------------------------------------------

    /** Gọi trong ApplicationListener.dispose() khi thoát game. */
    public void dispose() {
        stopTheme();
        stopOneShotMusic();
        for (Sound s : soundCache.values()) s.dispose();
        soundCache.clear();
    }
}
