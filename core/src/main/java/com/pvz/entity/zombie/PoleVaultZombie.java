package com.pvz.entity.zombie;

import com.pvz.data.ZombieData;

/**
 * Pole Vaulting Zombie:
 *  - Di nhanh cho den khi gap CAY DAU TIEN.
 *  - Nhay QUA DAU cay (arc parabol) roi dap dat phia sau, chuyen di bo binh thuong.
 *  - Neu gap cay THU HAI -> khong nhay nua, an binh thuong.
 */
public class PoleVaultZombie extends Zombie {

    private boolean hasVaulted = false;
    private boolean vaulting = false;
    private float vaultProgress = 0f;
    private static final float VAULT_TIME = 0.5f;
    private static final float VAULT_HEIGHT = 120f;    // do cao nhay (pixel)
    private float vaultFromX, vaultToX;
    private float baseY;                                // y goc truoc khi nhay

    public PoleVaultZombie(ZombieData data, int row, float startX, float cy, float w, float h) {
        super(data, row, startX, cy, w, h);
        this.baseY = cy;
    }

    @Override
    public void update(float delta) {
        if (slowTimer > 0f) {
            slowTimer -= delta;
            if (slowTimer <= 0f) slowFactor = 1f;
        }

        // dying: chi chay animation roi bien mat (giong Zombie cha)
        if (state == State.DYING) {
            dyingTimer += delta;
            anim.update(delta);
            if (dyingTimer >= DYING_DURATION) {
                finishDying();
            }
            return;
        }

        if (vaulting) {
            vaultProgress += delta / VAULT_TIME;
            if (vaultProgress >= 1f) {
                vaultProgress = 1f;
                vaulting = false;
                hasVaulted = true;
                x = vaultToX;
                y = baseY;
                anim.setState("walking"); // nhay xong -> di bo
            } else {
                // noi suy x tuyen tinh
                x = vaultFromX + (vaultToX - vaultFromX) * vaultProgress;
                // y theo arc parabol: cao nhat o giua (progress=0.5)
                float arc = 4f * VAULT_HEIGHT * vaultProgress * (1f - vaultProgress);
                y = baseY + arc;
            }
            anim.update(delta);
            return;
        }

        if (state == State.WALKING) {
            float spd = (hasVaulted && data.vault != null && data.vault.speedAfterVault > 0)
                ? data.vault.speedAfterVault : data.speed;
            x -= spd * speedMul * slowFactor * delta;
            anim.setState("walking");
        } else if (state == State.EATING) {
            eatTimer += delta;
            anim.setState("eating");
        }
        anim.update(delta);
    }

    /** GameWorld goi khi zombie nay cham cay dau tien va chua nhay. */
    public void startVault(float landingX) {
        if (hasVaulted || vaulting) return;
        vaulting = true;
        vaultProgress = 0f;
        vaultFromX = x;
        vaultToX = landingX;
        baseY = y;
        anim.setState("special"); // animation nhay
    }

    public boolean canVault() { return !hasVaulted && !vaulting; }
    public boolean isVaulting() { return vaulting; }
    public boolean hasVaulted() { return hasVaulted; }
}
