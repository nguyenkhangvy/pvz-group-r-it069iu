package com.pvz.entity.zombie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.util.DebugDraw;
import com.pvz.data.ZombieData;
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

    public Zombie(ZombieData data, int row, float startX, float centerY,
                  float width, float height) {
        super(startX, centerY, width, height, data.hp);
        this.data = data;
        this.row = row;
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
        } else if (state == State.EATING) {
            eatTimer += delta;
        }
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

    @Override
    public void draw(SpriteBatch batch) {
        // TODO: animation tu atlas
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
