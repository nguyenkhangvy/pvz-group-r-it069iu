package com.pvz.system;

import com.badlogic.gdx.math.MathUtils;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.entity.zombie.Zombie;
import com.pvz.factory.ZombieFactory;
import com.pvz.screen.GameScreen;

/**
 * WaveSystem: doc timeline trong level JSON va spawn zombie theo thoi gian.
 *
 * - Dem thoi gian bang gameDelta (da scale/pause) -> dung yeu cau: 2x thi
 *   wave cung den nhanh gap doi, pause thi dung.
 * - huge wave chi la cac moc spawn nhieu zombie hon (co the nhieu trong 1 lv).
 * - "isFinished" = tat ca wave da spawn xong. GameScreen ket hop dieu kien
 *   "khong con zombie song" de quyet dinh WIN.
 *
 * Neu levelData null (chua co JSON), WaveSystem coi nhu khong co wave va
 * finished ngay (de test khung khong bi treo).
 */
public final class WaveSystem {

    private final LevelData level;
    private final ZombieFactory factory;
    private final int[] activeRows;   // cac hang duoc phep spawn zombie
    private float elapsed = 0f;
    private int nextWaveIndex = 0;

    public WaveSystem(LevelData level, ZombieFactory factory) {
        this.level = level;
        this.factory = factory;
        this.activeRows = resolveActiveRows(level);
    }

    /** Lay danh sach hang active tu level; neu khong co thi dung ca 5 hang. */
    private static int[] resolveActiveRows(LevelData level) {
        if (level != null && level.activeRows != null && level.activeRows.length > 0) {
            return level.activeRows;
        }
        int[] all = new int[GameConfig.GRID_ROWS];
        for (int r = 0; r < GameConfig.GRID_ROWS; r++) all[r] = r;
        return all;
    }

    public void update(float gameDelta, GameScreen screen) {
        if (level == null || level.waves == null) return;
        elapsed += gameDelta;
        while (nextWaveIndex < level.waves.length
                && elapsed >= level.waves[nextWaveIndex].time) {
            spawnWave(level.waves[nextWaveIndex], screen);
            nextWaveIndex++;
        }
    }

    private void spawnWave(LevelData.Wave wave, GameScreen screen) {
        // huge wave -> phat am thanh bao dong (dot lon)
        if (wave.huge) {
            com.pvz.manager.AudioManager.get().playGameSound(
                com.pvz.manager.AudioManager.HUGE_WAVE, 0.9f);
        }
        for (int i = 0; i < wave.count; i++) {
            int row;
            if (wave.rows != null && wave.rows.length > 0) {
                row = wave.rows[i % wave.rows.length];
                // an toan: neu hang chi dinh khong active -> doi sang 1 hang active
                if (!isActive(row)) row = activeRows[MathUtils.random(0, activeRows.length - 1)];
            } else {
                // chon ngau nhien trong cac hang ACTIVE
                row = activeRows[MathUtils.random(0, activeRows.length - 1)];
            }
            Zombie z = factory.create(wave.zombieId, row);
            screen.spawnZombie(z);
        }
    }

    private boolean isActive(int row) {
        for (int r : activeRows) if (r == row) return true;
        return false;
    }

    /**
     * Mot cot moc tren progress bar: thoi diem (giay) va co phai huge wave.
     * GameScreen dung de ve icon mocc tren thanh tien do.
     */
    public static final class WaveMarker {
        public final float time;
        public final boolean huge;
        public WaveMarker(float time, boolean huge) { this.time = time; this.huge = huge; }
    }

    /** Tra ve danh sach moc wave (theo thoi diem) de GameScreen ve len progress bar. */
    public java.util.List<WaveMarker> getMarkers() {
        java.util.List<WaveMarker> list = new java.util.ArrayList<>();
        if (level == null || level.waves == null) return list;
        for (LevelData.Wave w : level.waves) {
            list.add(new WaveMarker(w.time, w.huge));
        }
        return list;
    }

    /** Wave nao da spawn chua (theo index) - de GameScreen biet moc nao da qua. */
    public boolean isWavePassed(int index) {
        return index < nextWaveIndex;
    }

    /** Da spawn het cac wave chua. */
    public boolean isFinished() {
        if (level == null || level.waves == null) return true;
        return nextWaveIndex >= level.waves.length;
    }
}
