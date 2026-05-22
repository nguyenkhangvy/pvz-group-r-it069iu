package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.pvz.data.PlantData;
import com.pvz.entity.zombie.Zombie;
import com.pvz.util.DebugDraw;

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
    }

    @Override
    public void updateWithContext(float delta, PlantContext ctx) {
        if (chompState == ChompState.CHEWING) {
            chewTimer += delta;
            if (chewTimer >= data.chew.chewTime) {
                chompState = ChompState.READY;
                chewTimer = 0f;
            }
            return; // dang nhai, khong an them
        }

        // READY: tim zombie cung o de an
        Array<Zombie> here = ctx.zombiesInCell(row, col);
        if (here.size > 0) {
            // an con gan nhat (x nho nhat -> tien nhat ve nha)
            Zombie target = null;
            float bestX = Float.MAX_VALUE;
            for (Zombie z : here) {
                if (z.isAlive() && z.getX() < bestX) { target = z; bestX = z.getX(); }
            }
            if (target != null) {
                target.takeDamage(data.chew.chompDamage); // nuot chung
                chompState = ChompState.CHEWING;
                chewTimer = 0f;
            }
        }
    }

    /** Chomper LUON khien zombie dung lai an (ke ca khi dang nhai). */
    @Override
    public boolean isEatable() {
        return true;
    }

    public boolean isChewing() { return chompState == ChompState.CHEWING; }

    @Override
    public void drawDebug(SpriteBatch batch) {
        Color c = isChewing() ? new Color(0.5f, 0.1f, 0.5f, 1f)   // tim dam khi nhai
                              : new Color(0.7f, 0.1f, 0.7f, 1f);   // tim sang khi ranh
        DebugDraw.get().rectCentered(batch, x, y, width, height, c);
    }
}
