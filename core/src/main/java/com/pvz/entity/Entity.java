package com.pvz.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Entity: lop GOC truu tuong cho moi thuc the trong game world.
 *
 * update(float delta) — delta luon tu GameClock, KHONG tu lay tu Gdx.
 */
public abstract class Entity {

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float hp;
    protected boolean alive = true;

    protected Entity(float x, float y, float width, float height, float hp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hp = hp;
    }

    /** Cap nhat logic. delta LUON tu GameClock (da scale/pause/clamp). */
    public abstract void update(float delta);

    /** Ve entity. */
    public abstract void draw(SpriteBatch batch);

    /** Tru mau. Khi hp <= 0 -> chet. */
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            kill();
        }
    }

    public void kill() { alive = false; }

    public boolean isAlive() { return alive; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getHp() { return hp; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
}
