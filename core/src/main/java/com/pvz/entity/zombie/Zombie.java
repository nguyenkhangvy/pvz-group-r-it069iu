package com.pvz.entity.zombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.data.ZombieData;
import com.pvz.entity.AnimationComponent;
import com.pvz.entity.Entity;

/**
 * Zombie: lop co so. Di chuyen PIXEL-based sang trai tren 1 hang co dinh.
 * "Dung" cay khi vao cung cell (col). Lop dieu phoi se tinh va cham.
 *
 * Trang thai: WALKING / EATING. (Cac zombie dac biet nhu PoleVault se la lop con.)
 * Khong can thuoc tinh armor: conehead/buckethead chi can hp lon hon trong JSON.
 */
public class Zombie extends Entity {

    public enum State { WALKING, EATING, DYING }

    protected final ZombieData data;
    protected final int row;

    protected State state = State.WALKING;
    protected float eatTimer = 0f;

    // Hieu ung lam cham (tu Snow Pea)
    protected float slowTimer = 0f;
    protected float slowFactor = 1f;

    // He so DO KHO (mac dinh 1.0 = Normal khong doi). Set ngay sau khi tao.
    protected float damageMul = 1f;
    protected float speedMul = 1f;

    protected final AnimationComponent anim = new AnimationComponent();

    public Zombie(ZombieData data, int row, float startX, float centerY,
                  float width, float height) {
        super(startX, centerY, width, height, data.hp);
        this.data = data;
        this.row = row;
        loadAnimations();
    }

    /**
     * Ap he so do kho. hpMul nhan vao hp (vi luc nay zombie vua tao).
     * Goi NGAY sau khi tao trong Factory.
     */
    public void applyDifficulty(float hpMul, float damageMul, float speedMul) {
        this.hp *= hpMul;
        this.damageMul = damageMul;
        this.speedMul = speedMul;
    }

    /** Damage moi don can, da nhan he so do kho. */
    public float getBiteDamage() { return data.damage * damageMul; }

    /** Nap animation tu JSON (fallback khoi mau neu chua co anh). */
    protected void loadAnimations() {
        if (data.animations == null) return;
        anim.addState("walking", data.animations.walking);
        anim.addState("eating", data.animations.eating);
        anim.addState("dying", data.animations.dying);
        anim.addState("special", data.animations.special);
        anim.setState("walking");
    }

    /** Thoi gian hien animation dying truoc khi bien mat (giay). */
    protected static final float DYING_DURATION = 0.6f;
    protected float dyingTimer = 0f;

    @Override
    public void update(float delta) {
        // cap nhat slow
        if (slowTimer > 0f) {
            slowTimer -= delta;
            if (slowTimer <= 0f) slowFactor = 1f;
        }

        if (state == State.DYING) {
            dyingTimer += delta;
            anim.update(delta);
            if (dyingTimer >= DYING_DURATION) {
                finishDying();
            }
            return; // khong di chuyen, khong an
        }

        if (state == State.WALKING) {
            x -= data.speed * speedMul * slowFactor * delta;
            anim.setState("walking");
        } else if (state == State.EATING) {
            eatTimer += delta;
            anim.setState("eating");
        }
        anim.update(delta);
    }

    public boolean canEatBite() {
        if (eatTimer >= data.eatInterval) {
            eatTimer = 0f;
            return true;
        }
        return false;
    }

    public void applySlow(float factor, float duration) {
        this.slowFactor = factor;
        this.slowTimer = duration;
    }

    /**
     * Override kill(): chuyen sang DYING state de hien animation dying,
     * roi moi that su bien mat sau DYING_DURATION.
     */
    @Override
    public void kill() {
        if (!alive || state == State.DYING) return; // da chet/dang dying -> bo qua
        state = State.DYING;
        anim.setState("dying");
        dyingTimer = 0f;
        com.pvz.manager.AudioManager.get().playGameSound(
            com.pvz.manager.AudioManager.ZOMBIE_DIE, 0.7f);
    }

    /** That su bien mat (goi sau khi dying animation xong). */
    protected void finishDying() {
        alive = false;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = anim.getFrame();
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        }
    }

    public int getRow() { return row; }
    public State getState() { return state; }
    public void setState(State s) { this.state = s; }
    public ZombieData getData() { return data; }

    /** Zombie dang trong animation dying (con hien anh nhung khong tuong tac). */
    public boolean isDying() { return state == State.DYING; }
}
