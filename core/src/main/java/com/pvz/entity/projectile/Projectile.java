package com.pvz.entity.projectile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.util.DebugDraw;
import com.pvz.data.ProjectileData;
import com.pvz.entity.Entity;

/**
 * Projectile (vd: pea, snow pea): la ENTITY theo yeu cau.
 * Bay PIXEL-based sang phai tren 1 hang. Trung zombie dau tien cung row co
 * pixelX gan nhat (lop dieu phoi xu ly va cham).
 */
public class Projectile extends Entity {

    private final ProjectileData data;
    private final int row;

    public Projectile(ProjectileData data, int row, float startX, float centerY,
                      float size) {
        super(startX, centerY, size, size, 1f);
        this.data = data;
        this.row = row;
    }

    @Override
    public void update(float delta) {
        x += data.speed * delta;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // TODO: ve sprite pea
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height,
            data.slows ? Color.CYAN : Color.LIME);
    }

    public int getRow() { return row; }
    public ProjectileData getData() { return data; }
}
