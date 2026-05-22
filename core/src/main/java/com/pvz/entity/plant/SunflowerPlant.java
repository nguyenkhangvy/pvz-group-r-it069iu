package com.pvz.entity.plant;

import com.pvz.data.PlantData;

/**
 * SunflowerPlant: cay SAN XUAT SUN (Sunflower).
 *
 * Single Responsibility: chi lo chu ky sinh sun (sunTimer + canProduceSun).
 * Khong he co attackTimer.
 */
public class SunflowerPlant extends Plant {

    private float sunTimer = 0f;

    public SunflowerPlant(PlantData data, int row, int col, float centerX, float centerY,
                          float width, float height) {
        super(data, row, col, centerX, centerY, width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        sunTimer += delta;
    }

    /** Da toi luc nha 1 sun chua? Neu roi, reset dong ho va tra true. */
    public boolean canProduceSun() {
        if (data.sun == null || data.sun.interval <= 0) return false;
        if (sunTimer >= data.sun.interval) {
            sunTimer = 0f;
            return true;
        }
        return false;
    }

    public int getSunAmount() { return data.sun != null ? data.sun.amount : 0; }
}
