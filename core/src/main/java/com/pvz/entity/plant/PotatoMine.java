package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.pvz.data.PlantData;
import com.pvz.entity.zombie.Zombie;
import com.pvz.util.DebugDraw;

/**
 * Potato Mine:
 *  - Co thoi gian "arm" (armTime, vd 15s). Trong luc arm CHUA the no.
 *  - Trong luc arm, neu zombie vao cung o -> zombie coi no nhu cay binh thuong
 *    va CAN no (Plant base co hp). Khi hp het -> bi an mat, KHONG no.
 *    (GameScreen xu ly viec zombie an cay qua co che blocking thong thuong.)
 *  - Sau khi arm xong: zombie nao vao cung o -> NO ngay, giet zombie cung o
 *    (va vung explodeRadius neu >0), roi tu bien mat.
 *
 * Luu y: trong luc arm, PotatoMine khong an duoc (isEatable=false) de zombie
 *  -> nhung de zombie dung lai an, ta cho no block. Theo mo ta cua ban:
 *  "trong thoi gian arm zombie co the can no bien mat khong no" => block + bi an.
 */
public class PotatoMine extends Plant {

    private float armTimer = 0f;
    private boolean armed = false;
    private boolean exploded = false;

    public PotatoMine(PlantData data, int row, int col, float cx, float cy, float w, float h) {
        super(data, row, col, cx, cy, w, h);
    }

    @Override
    public void updateWithContext(float delta, PlantContext ctx) {
        if (exploded) return;

        if (!armed) {
            armTimer += delta;
            if (armTimer >= data.arm.armTime) armed = true;
            return; // chua arm xong thi khong no
        }

        // da arm: co zombie cung o -> no
        Array<Zombie> here = ctx.zombiesInCell(row, col);
        if (here.size > 0) {
            int radius = Math.max(0, data.explode.radius);
            ctx.damageArea(row, col, radius, data.explode.damage);
            exploded = true;
            kill();
            ctx.removePlant(this);
        }
    }

    /**
     * Chua arm: zombie DUNG LAI an (co the an mat min truoc khi no) -> eatable = true.
     * Da arm:   zombie KHONG an, ma tien vao o de bi no giet -> eatable = false.
     */
    @Override
    public boolean isEatable() {
        return !armed;
    }

    public boolean isArmed() { return armed; }

    @Override
    public void drawDebug(SpriteBatch batch) {
        // xam khi chua arm, do khi da arm (san sang no)
        Color c = armed ? new Color(0.9f, 0.3f, 0.1f, 1f) : new Color(0.5f, 0.4f, 0.3f, 1f);
        DebugDraw.get().rectCentered(batch, x, y, width * 0.7f, height * 0.5f, c);
    }
}
