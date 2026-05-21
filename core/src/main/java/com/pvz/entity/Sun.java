package com.pvz.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.util.DebugDraw;
import com.pvz.core.GameConfig;

/**
 * Sun: la ENTITY. Roi tu troi hoac tu Sunflower.
 * Bien mat sau GameConfig.SUN_LIFETIME (20s) neu khong duoc nhat.
 * Roi xuong toi targetY thi dung lai (nam cho).
 */
public class Sun extends Entity {

    private final int value;
    private final float targetY;
    private float fallSpeed = 60f;
    private float lifeTimer = 0f;

    public Sun(int value, float x, float startY, float targetY, float size) {
        super(x, startY, size, size, 1f);
        this.value = value;
        this.targetY = targetY;
    }

    @Override
    public void update(float delta) {
        if (y > targetY) {
            y -= fallSpeed * delta;
            if (y < targetY) y = targetY;
        } else {
            lifeTimer += delta;
            if (lifeTimer >= GameConfig.SUN_LIFETIME) kill();
        }
    }

    /** Kiem tra diem (px,py) co cham vao sun khong (de nhat bang chuot). */
    public boolean contains(float px, float py) {
        return px >= x - width / 2f && px <= x + width / 2f
            && py >= y - height / 2f && py <= y + height / 2f;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // TODO: sprite sun
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height, Color.GOLD);
    }

    public int getValue() { return value; }
}
