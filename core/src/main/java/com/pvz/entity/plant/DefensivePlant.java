package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.data.PlantData;
import com.pvz.manager.AssetProvider;

/**
 * DefensivePlant (Wall-nut): cay thu thuan, chi chan zombie.
 * Hien anh theo muc hp:
 *  - wallnut_idle:      hp > 2600
 *  - wallnut_special_0: hp > 1300 va <= 2600
 *  - wallnut_special_1: hp > 0 va <= 1300
 */
public class DefensivePlant extends Plant {

    public DefensivePlant(PlantData data, int row, int col, float centerX, float centerY,
                          float width, float height) {
        super(data, row, col, centerX, centerY, width, height);
    }

    @Override
    public void update(float delta) {
        // khong dung animation component tu dong
    }

    @Override
    public void draw(SpriteBatch batch) {
        String region;
        if (hp > 2600) {
            region = "wallnut_idle";
        } else if (hp > 1300) {
            region = "wallnut_special_0";
        } else {
            region = "wallnut_special_1";
        }
        TextureRegion frame = AssetProvider.get().region(region);
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width-3f / 2f, y - height -5f/ 2f, 2*width, 2*height);
        }
    }
}
