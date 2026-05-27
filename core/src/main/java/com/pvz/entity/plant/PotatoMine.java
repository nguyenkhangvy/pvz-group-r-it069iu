package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.pvz.data.PlantData;
import com.pvz.entity.zombie.Zombie;
import com.pvz.manager.AssetProvider;

/**
 * Potato Mine:
 *  - idle: dang arm (chua san sang)
 *  - special_0: da arm, san sang no
 *  - special_1: dang no (hien 1 frame roi bien mat)
 */
public class PotatoMine extends Plant {

    private float armTimer = 0f;
    private boolean armed = false;
    private boolean exploded = false;
    private float explodeShowTimer = 0f;
    private static final float EXPLODE_SHOW_TIME = 0.3f;

    public PotatoMine(PlantData data, int row, int col, float cx, float cy, float w, float h) {
        super(data, row, col, cx, cy, w, h);
    }

    @Override
    public void update(float delta) {
        // khong dung animation component tu dong
    }

    @Override
    public void updateWithContext(float delta, PlantContext ctx) {
        // dang hien anh no -> dem roi bien mat
        if (exploded) {
            explodeShowTimer += delta;
            if (explodeShowTimer >= EXPLODE_SHOW_TIME) {
                kill();
                ctx.removePlant(this);
            }
            return;
        }

        if (!armed) {
            armTimer += delta;
            if (armTimer >= data.arm.armTime) {
                armed = true;
            }
            return;
        }

        // da arm: co zombie cung o -> no
        Array<Zombie> here = ctx.zombiesInCell(row, col);
        if (here.size > 0) {
            int radius = Math.max(0, data.explode.radius);
            ctx.damageArea(row, col, radius, data.explode.damage);
            com.pvz.manager.AudioManager.get().playGameSound(com.pvz.manager.AudioManager.EXPLODE, 0.9f);
            exploded = true;
            explodeShowTimer = 0f;
            // khong kill ngay, cho hien anh no (special_1) mot chut
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        String region;
        if (exploded) {
            region = "potatomine_special_1";     // dang no
        } else if (armed) {
            region = "potatomine_special_0";     // da arm, san sang
        } else {
            region = "potatomine_idle";          // dang arm
        }
        TextureRegion frame = AssetProvider.get().region(region);
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        }
    }

    @Override
    public boolean isEatable() {
        return !armed;
    }

    public boolean isArmed() { return armed; }
}
