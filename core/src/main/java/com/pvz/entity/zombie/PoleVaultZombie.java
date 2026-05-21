package com.pvz.entity.zombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.data.ZombieData;
import com.pvz.util.DebugDraw;

/**
 * Pole Vaulting Zombie:
 *  - Di nhanh (speed) cho den khi gap CAY DAU TIEN.
 *  - Khi gap cay dau tien -> NHAY qua no (vault), bo qua o do, dap dat phia sau cay,
 *    roi chuyen sang di bo BINH THUONG (speedAfterVault).
 *  - Neu gap cay THU HAI -> khong nhay nua, an binh thuong nhu zombie thuong.
 *
 * Co che nhay duoc xu ly o GameScreen (vi can biet vi tri cay): khi phat hien
 * va cham cay dau tien va zombie chua nhay -> goi performVault() de day x qua o.
 */
public class PoleVaultZombie extends Zombie {

    private boolean hasVaulted = false;     // da nhay qua cay dau tien chua
    private boolean vaulting = false;        // dang trong animation nhay
    private float vaultProgress = 0f;
    private static final float VAULT_TIME = 0.5f; // thoi gian nhay (giay)
    private float vaultFromX, vaultToX;

    public PoleVaultZombie(ZombieData data, int row, float startX, float cy, float w, float h) {
        super(data, row, startX, cy, w, h);
    }

    @Override
    public void update(float delta) {
        // cap nhat slow nhu zombie thuong
        if (slowTimer > 0f) {
            slowTimer -= delta;
            if (slowTimer <= 0f) slowFactor = 1f;
        }

        if (vaulting) {
            // dang nhay: noi suy x tu vaultFromX -> vaultToX
            vaultProgress += delta / VAULT_TIME;
            if (vaultProgress >= 1f) {
                vaultProgress = 1f;
                vaulting = false;
                hasVaulted = true;
                x = vaultToX;
            } else {
                x = vaultFromX + (vaultToX - vaultFromX) * vaultProgress;
            }
            return; // trong luc nhay khong an, khong di thuong
        }

        // di chuyen binh thuong (truoc nhay dung speed; sau nhay dung speedAfterVault)
        if (state == State.WALKING) {
            float spd = hasVaulted && data.speedAfterVault > 0 ? data.speedAfterVault : data.speed;
            x -= spd * slowFactor * delta;
        } else if (state == State.EATING) {
            eatTimer += delta;
        }
    }

    /** GameScreen goi khi zombie nay cham cay dau tien va chua nhay. */
    public void startVault(float landingX) {
        if (hasVaulted || vaulting) return;
        vaulting = true;
        vaultProgress = 0f;
        vaultFromX = x;
        vaultToX = landingX;
    }

    public boolean canVault() { return !hasVaulted && !vaulting; }
    public boolean isVaulting() { return vaulting; }
    public boolean hasVaulted() { return hasVaulted; }

    @Override
    public void drawDebug(SpriteBatch batch) {
        Color c;
        if (vaulting) c = new Color(1f, 0.6f, 0f, 1f);           // cam khi nhay
        else if (!hasVaulted) c = new Color(0.4f, 0.5f, 0.7f, 1f); // xanh truoc nhay
        else c = (state == State.EATING) ? Color.RED : Color.GRAY; // nhu thuong sau nhay
        // ve cao hon mot chut khi nhay de thay "bay len"
        float yo = vaulting ? (float) Math.sin(vaultProgress * Math.PI) * 40f : 0f;
        DebugDraw.get().rectCentered(batch, x, y + yo, width, height, c);
    }
}
