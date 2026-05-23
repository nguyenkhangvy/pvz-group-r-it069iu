package com.pvz.entity.zombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.util.DebugDraw;
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

    protected final AnimationComponent anim = new AnimationComponent();

    public Zombie(ZombieData data, int row, float startX, float centerY,
                  float width, float height) {
        super(startX, centerY, width, height, data.hp);
        this.data = data;
        this.row = row;
        loadAnimations();
    }

    /** Nap animation tu JSON (fallback khoi mau neu chua co anh). */
    protected void loadAnimations() {
        if (data.animations == null) return;
        anim.addState("walking", data.animations.walking);
        anim.addState("eating", data.animations.eating);
        anim.addState("dying", data.animations.dying);
        anim.addState("special", data.animations.special);
        anim.setState("walking");
    }

    @Override
    public void update(float delta) {
        // cap nhat slow
        if (slowTimer > 0f) {
            slowTimer -= delta;
            if (slowTimer <= 0f) slowFactor = 1f;
        }

        if (state == State.WALKING) {
            x -= data.speed * slowFactor * delta;
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
     * Override kill(): phat tieng zombie chet MOT LAN, bat ke nguon giet la gi
     * (pea, chomper, cherry bomb, potato mine, lawn mower...). Dung co diedSoundPlayed
     * de khong phat lap neu kill() bi goi nhieu lan.
     */
    @Override
    public void kill() {
        if (!alive) return;            // da chet roi, bo qua
        super.kill();
        com.pvz.manager.AudioManager.get().playGameSound(
            com.pvz.manager.AudioManager.ZOMBIE_DIE, 0.7f);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = anim.getFrame();
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        } else {
            drawDebug(batch);
        }
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height,
            state == State.EATING ? Color.RED : Color.GRAY);
    }

    public int getRow() { return row; }
    public State getState() { return state; }
    public void setState(State s) { this.state = s; }
    public ZombieData getData() { return data; }
}
