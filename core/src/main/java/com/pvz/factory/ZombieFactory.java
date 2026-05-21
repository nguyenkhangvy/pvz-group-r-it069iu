package com.pvz.factory;

import com.pvz.core.GameConfig;
import com.pvz.data.ZombieData;
import com.pvz.entity.zombie.PoleVaultZombie;
import com.pvz.entity.zombie.Zombie;
import com.pvz.manager.DataManager;
import com.pvz.system.GridSystem;

/**
 * ZombieFactory (FACTORY METHOD pattern).
 * basic/flag/conehead/buckethead dung Zombie base (chi khac hp/speed tu data).
 * polevault dung lop con PoleVaultZombie (co hanh vi nhay).
 */
public final class ZombieFactory {

    private final DataManager data = DataManager.get();
    private final GridSystem grid;

    public ZombieFactory(GridSystem grid) { this.grid = grid; }

    public Zombie create(String zombieId, int row) {
        ZombieData zd = data.zombie(zombieId);
        if (zd == null) return null;

        float startX = grid.rightSpawnX();
        float cy = grid.rowToPixelY(row);
        float w = GameConfig.CELL_WIDTH * 0.7f;
        float h = GameConfig.CELL_HEIGHT * 0.9f;

        if ("polevault".equals(zombieId) || "polevault".equals(zd.ability)) {
            return new PoleVaultZombie(zd, row, startX, cy, w, h);
        }
        return new Zombie(zd, row, startX, cy, w, h);
    }
}
