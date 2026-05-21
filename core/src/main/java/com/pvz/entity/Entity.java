package com.pvz.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Entity: lop GOC truu tuong cho moi thuc the trong game world
 * (Plant, Zombie, Projectile, LawnMower, Sun...).
 *
 * Nguyen tac SOLID:
 *  - Single Responsibility: Entity chi lo vi tri, hp, trang thai song/chet, va vong doi update/draw.
 *  - Open/Closed: them loai entity moi bang cach ke thua, khong sua lop nay.
 *  - Liskov: moi entity con deu dung duoc qua tham chieu Entity.
 *
 * Quan trong: update(float delta) - delta luon do GameClock cap, KHONG tu lay tu Gdx.
 */
public abstract class Entity {

    protected float x;          // toa do pixel (tam entity)
    protected float y;
    protected float width;
    protected float height;

    protected float hp;
    protected float maxHp;

    protected boolean alive = true;

    protected Entity(float x, float y, float width, float height, float maxHp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    /** Cap nhat logic. delta LUON tu GameClock (da scale/pause/clamp). */
    public abstract void update(float delta);

    /** Ve bang texture that (khi co asset). */
    public abstract void draw(SpriteBatch batch);

    /** Ve placeholder khoi mau (khi chua co asset) - de test logic ngay. Dung SpriteBatch cho on dinh. */
    public abstract void drawDebug(SpriteBatch batch);

    /** Tru mau. Khi hp <= 0 -> chet. */
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp <= 0) {
            hp = 0;
            kill();
        }
    }

    public void kill() { alive = false; }

    // ----- getter/setter -----
    public boolean isAlive() { return alive; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getHp() { return hp; }
    public float getMaxHp() { return maxHp; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
}
