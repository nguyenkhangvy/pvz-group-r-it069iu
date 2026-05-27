package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.data.PlantData;
import com.pvz.manager.AssetProvider;

/**
 * CherryBomb: dat xong dem fuse (1.2s) roi no vung 3x3.
 *  - idle: vua dat xuong
 *  - special_0: gan no (hien tu giua fuse den luc no)
 *  - special_1: dang no (hien 1 frame roi bien mat)
 */
public class CherryBomb extends Plant {

    private float fuse = 0f;
    private boolean exploded = false;
    private float explodeShowTimer = 0f;
    private static final float EXPLODE_SHOW_TIME = 0.3f;

    public CherryBomb(PlantData data, int row, int col, float cx, float cy, float w, float h) {
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

        fuse += delta;
        if (fuse >= data.explode.delay) {
            ctx.damageArea(row, col, Math.max(1, data.explode.radius), data.explode.damage);
            exploded = true;
            explodeShowTimer = 0f;
            com.pvz.manager.AudioManager.get().playGameSound(com.pvz.manager.AudioManager.EXPLODE, 0.9f);
            // khong kill ngay, cho hien anh no (special_1) mot chut
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        String region;
        if (exploded) {
            region = "cherrybomb_special_1";     // dang no
        } else if (fuse >= data.explode.delay * 0.5f) {
            region = "cherrybomb_special_0";     // gan no (nua sau cua fuse)
        } else {
            region = "cherrybomb_idle";          // vua dat xuong
        }
        TextureRegion frame = AssetProvider.get().region(region);
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        }
    }

    @Override
    public boolean isEatable() {
        return false;
    }
}
