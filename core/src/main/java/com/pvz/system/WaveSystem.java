package com.pvz.system;

import com.badlogic.gdx.math.MathUtils;
import com.pvz.data.LevelData;
import com.pvz.entity.zombie.Zombie;
import com.pvz.factory.ZombieFactory;
import com.pvz.screen.GameScreen;

/**
 * WaveSystem: doc timeline trong level JSON va spawn zombie theo thoi gian.
 * Zombie chi spawn tren activeRows (hang da mo).
 */
public final class WaveSystem {

    private final LevelData level;
    private final ZombieFactory factory;
    private final LawnSystem lawn;
    private float elapsed = 0f;
    private int nextWaveIndex = 0;

    public WaveSystem(LevelData level, ZombieFactory factory, LawnSystem lawn) {
        this.level = level;
        this.factory = factory;
        this.lawn = lawn;
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
        // lay danh sach hang active
        int[] active = lawn.getActiveRows();
        if (active.length == 0) return;

        for (int i = 0; i < wave.count; i++) {
            int row;
            if (wave.rows != null && wave.rows.length > 0) {
                // JSON chi dinh row cu the — nhung chi dung neu row do active
                row = wave.rows[i % wave.rows.length];
                if (!lawn.isRowActive(row)) {
                    row = active[MathUtils.random(0, active.length - 1)];
                }
            } else {
                // random trong cac hang active
                row = active[MathUtils.random(0, active.length - 1)];
            }
            Zombie z = factory.create(wave.zombieId, row);
            screen.spawnZombie(z);
        }
    }

    public boolean isFinished() {
        if (level == null || level.waves == null) return true;
        return nextWaveIndex >= level.waves.length;
    }
}
