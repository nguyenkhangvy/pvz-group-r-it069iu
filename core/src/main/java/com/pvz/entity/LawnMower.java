package com.pvz.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.util.DebugDraw;
import com.pvz.manager.AssetProvider;

/**
 * LawnMower: la ENTITY (theo yeu cau). Phong thu cuoi cung moi hang (1 cai/hang).
 *
 * Hanh vi:
 *  - Dung yen sat nha cho den khi 1 zombie cham vao no -> kich hoat.
 *  - Khi kich hoat: chay het hang sang phai, giet MOI zombie cung hang khi di qua,
 *    roi bien mat (dung 1 lan).
 *  - Mac dinh moi man choi deu reset (co lai du).
 *  - Neu da dung roi ma zombie lai toi sat nha lan nua -> THUA ngay.
 */
public class LawnMower extends Entity {

    public enum State { READY, RUNNING, USED }

    private static final float MOWER_SPEED = 600f; // pixel/giay khi chay

    private final int row;
    private State state = State.READY;

    public LawnMower(int row, float x, float y, float width, float height) {
        super(x, y, width, height, 1f);
        this.row = row;
    }

    @Override
    public void update(float delta) {
        if (state == State.RUNNING) {
            x += MOWER_SPEED * delta;
        }
    }

    public void trigger() {
        if (state == State.READY) state = State.RUNNING;
    }

    public void markUsedOffscreen() { state = State.USED; alive = false; }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = AssetProvider.get().region("lawnmower");
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        } else {
            drawDebug(batch);
        }
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height, Color.YELLOW);
    }

    public int getRow() { return row; }
    public State getState() { return state; }
    public boolean isReady() { return state == State.READY; }
    public boolean isRunning() { return state == State.RUNNING; }
}
