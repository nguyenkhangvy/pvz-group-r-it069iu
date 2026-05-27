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

        // PoleVault: nhan dien qua id, hoac qua su ton tai cua nhom "vault" trong JSON.
        Zombie z;
        if ("polevault".equals(zombieId) || zd.vault != null) {
            z = new PoleVaultZombie(zd, row, startX, cy, w, h);
        } else {
            z = new Zombie(zd, row, startX, cy, w, h);
        }

        // ap he so DO KHO (hp/damage/speed) - mot cho duy nhat, moi zombie deu chiu
        com.pvz.core.Difficulty d = com.pvz.manager.SaveManager.get().getDifficulty();
        z.applyDifficulty(d.hpMul, d.damageMul, d.speedMul);
        return z;
    }
}
