package com.pvz.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * DebugDraw: ve placeholder bang SpriteBatch (duong ve on dinh nhat cua libGDX),
 * tranh dung ShapeRenderer von co the gay crash native tren mot so driver.
 *
 * Dung 1 texture pixel trang 1x1, to mau bang batch.setColor() roi keo gian
 * thanh hinh chu nhat. Hinh tron xap xi bang hinh vuong (du cho placeholder).
 *
 * Singleton-lite: tao 1 lan, dispose khi thoat.
 */
public final class DebugDraw {

    private static DebugDraw instance;
    private final Texture white;

    private DebugDraw() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        white = new Texture(pm);
        pm.dispose();
    }

    public static DebugDraw get() {
        if (instance == null) instance = new DebugDraw();
        return instance;
    }

    /** Ve hinh chu nhat tu goc duoi-trai (x,y) kich thuoc w,h. */
    public void rect(SpriteBatch batch, float x, float y, float w, float h, Color color) {
        batch.setColor(color);
        batch.draw(white, x, y, w, h);
        batch.setColor(Color.WHITE); // reset
    }

    public void dispose() {
        white.dispose();
    }
}
