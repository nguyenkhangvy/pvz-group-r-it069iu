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
    private float elapsed = 0f;
    private int nextWaveIndex = 0;

    public WaveSystem(LevelData level, ZombieFactory factory) {
        this.level = level;
        this.factory = factory;
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
        for (int i = 0; i < wave.count; i++) {
            int row;
            if (wave.rows != null && wave.rows.length > 0) {
                row = wave.rows[i % wave.rows.length];
            } else {
                row = MathUtils.random(0, GameConfig.GRID_ROWS - 1);
            }
            Zombie z = factory.create(wave.zombieId, row);
            screen.spawnZombie(z);
        }
    }

    /** Da spawn het cac wave chua. */
    public boolean isFinished() {
        if (level == null || level.waves == null) return true;
        return nextWaveIndex >= level.waves.length;
    }
}
