package com.pvz.factory;

import com.pvz.data.ProjectileData;
import com.pvz.entity.projectile.Projectile;
import com.pvz.manager.DataManager;

/**
 * ProjectileFactory (FACTORY METHOD pattern).
 */
public final class ProjectileFactory {

    private final DataManager data = DataManager.get();

    public Projectile create(String projId, int row, float startX, float centerY) {
        ProjectileData pd = data.projectile(projId);
        if (pd == null) return null;
        return new Projectile(pd, row, startX, centerY, 24f);
    }
}
