package com.pvz.entity.plant;

import com.pvz.data.PlantData;

/**
 * ShooterPlant: cay TAN CONG tu xa (Peashooter, SnowPea, Repeater).
 *
 * Single Responsibility: chi lo chu ky ban (attackTimer + canAttack).
 * Khong he co sunTimer.
 *
 * So vien dan moi luot doc tu data.shoot.projectilePerShot (Repeater = 2).
 */
public class ShooterPlant extends Plant {

    private float attackTimer = 0f;

    public ShooterPlant(PlantData data, int row, int col, float centerX, float centerY,
                        float width, float height) {
        super(data, row, col, centerX, centerY, width, height);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        attackTimer += delta;
    }

    /** Da toi luot ban chua? Neu roi, reset dong ho va tra true. */
    public boolean canAttack() {
        if (data.shoot == null || data.shoot.attackInterval <= 0) return false;
        if (attackTimer >= data.shoot.attackInterval) {
            attackTimer = 0f;
            return true;
        }
        return false;
    }

    public String getProjectileType() { return data.shoot != null ? data.shoot.projectileType : null; }
    public int getProjectilePerShot() { return data.shoot != null ? Math.max(1, data.shoot.projectilePerShot) : 1; }
}
