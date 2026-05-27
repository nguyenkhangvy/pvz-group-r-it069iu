package com.pvz.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.manager.AssetProvider;

/**
 * UiKit: bo cong cu ve UI kieu Plants vs Zombies (tone tuoi sang xanh la/vang).
 *
 * Triet ly "khung lap anh":
 *  - Moi thanh phan UI (panel, nut) deu THU nap anh that tu assets/images/ theo
 *    ten quy uoc. NEU CO anh -> ve anh. NEU CHUA -> tu ve placeholder dep
 *    (bo goc, gradient, vien) de game van dep ngay bay gio.
 *  - Khi ban bo anh that vao dung ten, no tu hien len, KHONG phai sua code.
 *
 * Ten anh quy uoc (dat trong assets/images/):
 *  - ui_panel        : nen panel/khung cua so
 *  - ui_button       : nut binh thuong
 *  - ui_button_down  : nut khi nhan (tuy chon; khong co thi dung ui_button toi hon)
 *  - ui_bg_menu      : anh nen man menu (tuy chon)
 *
 * Texture placeholder duoc sinh 1 lan bang Pixmap (bo goc + gradient).
 */
public final class UiKit {

    private static UiKit instance;

    // texture placeholder sinh trong code
    private final Texture panelTex;       // panel bo goc mau kem/nau nhat
    private final Texture buttonTex;      // nut gradient xanh la
    private final Texture buttonDownTex;  // nut khi nhan (toi hon)
    private final Texture white;          // 1x1 trang (vien/overlay)

    // tone mau PvZ
    public static final Color CREAM      = new Color(0.96f, 0.91f, 0.74f, 1f); // kem (panel)
    public static final Color BROWN      = new Color(0.45f, 0.31f, 0.16f, 1f); // nau (vien)
    public static final Color GREEN      = new Color(0.40f, 0.74f, 0.22f, 1f); // xanh la (nut)
    public static final Color GREEN_DARK = new Color(0.27f, 0.55f, 0.15f, 1f);
    public static final Color YELLOW     = new Color(0.98f, 0.82f, 0.25f, 1f); // vang (nhan manh)
    public static final Color TEXT_DARK  = new Color(0.20f, 0.13f, 0.05f, 1f);
    public static final Color SKY_TOP    = new Color(0.52f, 0.80f, 0.96f, 1f); // nen troi
    public static final Color SKY_BOT    = new Color(0.78f, 0.93f, 0.70f, 1f);

    private static final int CORNER = 16; // ban kinh bo goc cua texture sinh ra

    private UiKit() {
        white = make1x1();
        panelTex = makeRounded(CREAM, BROWN, 4);
        buttonTex = makeGradientRounded(GREEN, GREEN_DARK, BROWN, 4);
        buttonDownTex = makeGradientRounded(GREEN_DARK, GREEN_DARK, BROWN, 4);
    }

    public static UiKit get() {
        if (instance == null) instance = new UiKit();
        return instance;
    }

    // ---------- API ve ----------

    /** Ve panel/khung (uu tien anh ui_panel, khong co thi placeholder bo goc). */
    public void panel(SpriteBatch batch, float x, float y, float w, float h) {
        TextureRegion img = AssetProvider.get().region("ui_panel");
        batch.setColor(Color.WHITE);
        if (img != null) {
            batch.draw(img, x, y, w, h);
        } else {
            draw9(batch, panelTex, x, y, w, h);
        }
    }

    /** Ve nut. pressed=true -> dung anh/placeholder nhan. Tra ve khong; chi ve. */
    public void button(SpriteBatch batch, float x, float y, float w, float h, boolean pressed) {
        TextureRegion img = AssetProvider.get().region(pressed ? "ui_button_down" : "ui_button");
        if (img == null && pressed) img = AssetProvider.get().region("ui_button"); // fallback
        batch.setColor(Color.WHITE);
        if (img != null) {
            batch.draw(img, x, y, w, h);
        } else {
            draw9(batch, pressed ? buttonDownTex : buttonTex, x, y, w, h);
        }
    }

    /** Ve nen man (uu tien anh ui_bg_menu; khong co thi gradient troi xanh). */
    public void background(SpriteBatch batch, float w, float h) {
        TextureRegion img = AssetProvider.get().region("ui_bg_menu");
        batch.setColor(Color.WHITE);
        if (img != null) {
            batch.draw(img, 0, 0, w, h);
        } else {
            // gradient doc: tu SKY_TOP xuong SKY_BOT (ve bang nhieu dai ngang)
            int bands = 60;
            for (int i = 0; i < bands; i++) {
                float t = i / (float) (bands - 1);
                float by = h * (1f - (i + 1) / (float) bands);
                float bh = h / bands + 1f;
                batch.setColor(lerp(SKY_TOP, SKY_BOT, t));
                batch.draw(white, 0, by, w, bh);
            }
            batch.setColor(Color.WHITE);
        }
    }

    /** Ve hinh chu nhat dac (tien ich). */
    public void fill(SpriteBatch batch, float x, float y, float w, float h, Color c) {
        batch.setColor(c);
        batch.draw(white, x, y, w, h);
        batch.setColor(Color.WHITE);
    }

    // ---------- helper ----------

    /** Ve texture bo goc theo kieu 9-patch don gian (giu goc khong bi keo gian). */
    private void draw9(SpriteBatch batch, Texture tex, float x, float y, float w, float h) {
        int c = CORNER;
        int tw = tex.getWidth(), th = tex.getHeight();
        float midW = w - 2 * c, midH = h - 2 * c;
        if (midW < 0) midW = 0;
        if (midH < 0) midH = 0;

        // 4 goc
        batch.draw(tex, x, y, c, c, 0, th - c, c, c, false, false);                 // duoi-trai
        batch.draw(tex, x + w - c, y, c, c, tw - c, th - c, c, c, false, false);    // duoi-phai
        batch.draw(tex, x, y + h - c, c, c, 0, 0, c, c, false, false);              // tren-trai
        batch.draw(tex, x + w - c, y + h - c, c, c, tw - c, 0, c, c, false, false); // tren-phai
        // 4 canh
        batch.draw(tex, x + c, y, midW, c, c, th - c, tw - 2 * c, c, false, false);          // duoi
        batch.draw(tex, x + c, y + h - c, midW, c, c, 0, tw - 2 * c, c, false, false);        // tren
        batch.draw(tex, x, y + c, c, midH, 0, c, c, th - 2 * c, false, false);                // trai
        batch.draw(tex, x + w - c, y + c, c, midH, tw - c, c, c, th - 2 * c, false, false);   // phai
        // giua
        batch.draw(tex, x + c, y + c, midW, midH, c, c, tw - 2 * c, th - 2 * c, false, false);
    }

    private static Color lerp(Color a, Color b, float t) {
        return new Color(
            a.r + (b.r - a.r) * t,
            a.g + (b.g - a.g) * t,
            a.b + (b.b - a.b) * t, 1f);
    }

    private Texture make1x1() {
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(Color.WHITE);
        pm.fill();
        Texture t = new Texture(pm);
        pm.dispose();
        return t;
    }

    /** Sinh texture bo goc mau dac + vien. */
    private Texture makeRounded(Color fill, Color border, int borderW) {
        int size = 64;
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(0, 0, 0, 0);
        pm.fill();
        // than bo goc
        pm.setColor(fill);
        fillRounded(pm, 0, 0, size, size, CORNER);
        // vien (ve vien bang cach ve khung mong mau border)
        pm.setColor(border);
        drawRoundedBorder(pm, 0, 0, size, size, CORNER, borderW);
        Texture t = new Texture(pm);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pm.dispose();
        return t;
    }

    /** Sinh texture bo goc GRADIENT doc (top->bottom) + vien. */
    private Texture makeGradientRounded(Color top, Color bottom, Color border, int borderW) {
        int size = 64;
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(0, 0, 0, 0);
        pm.fill();
        // ve gradient theo tung dong roi mask bo goc
        for (int yy = 0; yy < size; yy++) {
            float t = yy / (float) (size - 1);
            Color c = lerp(top, bottom, t);
            pm.setColor(c);
            pm.drawLine(0, yy, size - 1, yy);
        }
        // mask: xoa 4 goc ngoai ban kinh -> trong suot
        maskCorners(pm, size, CORNER);
        // vien
        pm.setColor(border);
        drawRoundedBorder(pm, 0, 0, size, size, CORNER, borderW);
        Texture t = new Texture(pm);
        t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pm.dispose();
        return t;
    }

    private void fillRounded(Pixmap pm, int x, int y, int w, int h, int r) {
        pm.fillRectangle(x + r, y, w - 2 * r, h);
        pm.fillRectangle(x, y + r, w, h - 2 * r);
        pm.fillCircle(x + r, y + r, r);
        pm.fillCircle(x + w - r - 1, y + r, r);
        pm.fillCircle(x + r, y + h - r - 1, r);
        pm.fillCircle(x + w - r - 1, y + h - r - 1, r);
    }

    private void maskCorners(Pixmap pm, int size, int r) {
        // dat trong suot cac pixel ngoai vung bo goc
        for (int yy = 0; yy < size; yy++) {
            for (int xx = 0; xx < size; xx++) {
                boolean outside = false;
                if (xx < r && yy < r) outside = dist(xx, yy, r, r) > r;
                else if (xx >= size - r && yy < r) outside = dist(xx, yy, size - r - 1, r) > r;
                else if (xx < r && yy >= size - r) outside = dist(xx, yy, r, size - r - 1) > r;
                else if (xx >= size - r && yy >= size - r) outside = dist(xx, yy, size - r - 1, size - r - 1) > r;
                if (outside) pm.drawPixel(xx, yy, 0); // trong suot
            }
        }
    }

    private void drawRoundedBorder(Pixmap pm, int x, int y, int w, int h, int r, int bw) {
        // ve vien xap xi: cac canh thang + 4 cung
        for (int i = 0; i < bw; i++) {
            pm.drawRectangle(x + i, y + i, w - 2 * i, h - 2 * i);
        }
    }

    private static float dist(int x1, int y1, int x2, int y2) {
        float dx = x1 - x2, dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void dispose() {
        panelTex.dispose();
        buttonTex.dispose();
        buttonDownTex.dispose();
        white.dispose();
    }
}
