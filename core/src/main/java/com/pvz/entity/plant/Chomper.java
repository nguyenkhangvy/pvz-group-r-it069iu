package com.pvz.entity.plant;

import com.badlogic.gdx.utils.Array;
import com.pvz.data.PlantData;
import com.pvz.entity.zombie.Zombie;

/**
 * Chomper:
 *  - Khi co zombie vao cung o (va Chomper dang RANH) -> AN nguyen con zombie do
 *    (gay chompDamage rat lon = giet ngay zombie thuong), roi chuyen sang NHAI.
 *  - Trong luc NHAI (chewTime, vd 42s): KHONG an duoc zombie khac, NHUNG van la
 *    vat can (block) -> zombie thu hai dung lai an Chomper. Chomper mat mau dan,
 *    co the CHET truoc khi nhai xong.
 *  - Nhai xong -> ranh lai.
 *
 * Luu y: Buckethead/Conehead hp > chompDamage? Khong - chompDamage = 999999 nen
 * Chomper nuot chung MOI loai zombie (dung PvZ goc: chomp giet bat ke loai nao).
 */
public class Chomper extends Plant {

    public enum ChompState { READY, CHEWING }

    private ChompState chompState = ChompState.READY;
    private float chewTimer = 0f;

    public Chomper(PlantData data, int row, int col, float cx, float cy, float w, float h) {
        super(data, row, col, cx, cy, w, h);
        anim.setState("idle");
    }

    @Override
    public void update(float delta) {
        anim.update(delta);
    }

    @Override
    public void updateWithContext(float delta, PlantContext ctx) {
        update(delta); // chay animation
        if (chompState == ChompState.CHEWING) {
            chewTimer += delta;
            if (chewTimer >= data.chew.chewTime) {
                chompState = ChompState.READY;
                chewTimer = 0f;
                anim.setState("idle"); // nhai xong -> ve idle
            }
            return;
        }

        // READY: tim zombie cung o de an
        Array<Zombie> here = ctx.zombiesInCell(row, col);
        if (here.size > 0) {
            Zombie target = null;
            float bestX = Float.MAX_VALUE;
            for (Zombie z : here) {
                if (z.isAlive() && !z.isDying() && z.getX() < bestX) { target = z; bestX = z.getX(); }
            }
            if (target != null) {
                target.takeDamage(data.chew.chompDamage);
                chompState = ChompState.CHEWING;
                chewTimer = 0f;
                anim.setState("special"); // an -> chuyen animation nhai
                com.pvz.manager.AudioManager.get().playGameSound(
                    com.pvz.manager.AudioManager.CHOMP, 0.9f);
            }
        }
    }

    /** Chomper LUON khien zombie dung lai an (ke ca khi dang nhai). */
    @Override
    public boolean isEatable() {
        return true;
    }

    public boolean isChewing() { return chompState == ChompState.CHEWING; }
}
