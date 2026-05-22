package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.data.PlantData;
import com.pvz.util.DebugDraw;

/**
 * CherryBomb: dat xong dem explodeDelay (1.2s) roi NO vung vuong 3x3
 * (explodeRadius = 1 -> tam +/-1 o), gay explodeDamage cho moi zombie trong vung,
 * sau do tu bien mat.
 *
 * Trong thoi gian cho no, hp dat rat cao (gan nhu bat tu) de khong bi an mat.
 */
public class CherryBomb extends Plant {

    private float fuse = 0f;
    private boolean exploded = false;

    public CherryBomb(PlantData data, int row, int col, float cx, float cy, float w, float h) {
        super(data, row, col, cx, cy, w, h);
    }

    @Override
    public void updateWithContext(float delta, PlantContext ctx) {
        if (exploded) return;
        fuse += delta;
        if (fuse >= data.explode.delay) {
            // No: gay sat thuong vung
            ctx.damageArea(row, col, Math.max(1, data.explode.radius), data.explode.damage);
            exploded = true;
            kill();              // cherry bomb bien mat sau khi no
            ctx.removePlant(this);
        }
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        // do dam khi sap no
        float t = data.explode.delay > 0 ? Math.min(1f, fuse / data.explode.delay) : 1f;
        Color c = new Color(1f, 0.2f * (1f - t), 0f, 1f);
        DebugDraw.get().rectCentered(batch, x, y, width, height, c);
    }

    /** Cherry Bomb sap no -> zombie di xuyen qua, khong dung lai an. */
    @Override
    public boolean isEatable() {
        return false;
    }
}
