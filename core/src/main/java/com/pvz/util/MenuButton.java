package com.pvz.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pvz.manager.AssetProvider;

/**
 * MenuButton: nut bam kieu PvZ.
 *
 * NANG CAP: ho tro ANH RIENG cho tung nut (de lam menu nhieu mau nhu mockup
 * CAPYBARA vs ZOMBARA: START NEW xanh, CONTINUE cam, MUSIC xanh duong...).
 *
 *  - setImage(name, drawLabel): dat ten anh PNG rieng (vd "btn_start_new").
 *    Khi co anh, nut ve anh do; khi nhan ve anh "<name>_down" neu co.
 *    Chua co anh -> fallback ve placeholder UiKit nhu cu (khong vo gi).
 *    drawLabel=false neu anh da ve san chu (khoi ve chu de len).
 *  - Hover: phong to nhe. Press: ve anh nhan / placeholder toi hon.
 *  - Locked: ve mo, bam khong an (dung cho nut difficulty bi khoa).
 *
 * Toa do the gioi (1280x720) qua Viewport.unproject -> chuan voi FitViewport.
 */
public final class MenuButton {

    public float x, y, w, h;
    public String text;

    private String imageName;         // ten anh rieng (null = dung placeholder UiKit)
    private boolean drawLabel = true; // co ve chu len nut khong
    private boolean locked = false;

    private boolean hovered = false;
    private boolean pressedInside = false;

    private final GlyphLayout layout = new GlyphLayout();
    private final Vector3 tmp = new Vector3();

    public MenuButton(String text, float x, float y, float w, float h) {
        this.text = text;
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    /** Dat anh rieng cho nut. drawLabel=false neu anh da co san chu. */
    public MenuButton setImage(String imageName, boolean drawLabel) {
        this.imageName = imageName;
        this.drawLabel = drawLabel;
        return this;
    }

    public MenuButton setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public boolean isLocked() { return locked; }
    public boolean isHovered() { return hovered; }

    /** Cap nhat hover/press. Goi moi frame TRUOC khi ve. */
    public void update(Viewport viewport) {
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);
        hovered = contains(tmp.x, tmp.y);
        if (Gdx.input.isTouched() && hovered) pressedInside = true;
        else if (!Gdx.input.isTouched()) pressedInside = false;
    }

    /** True dung 1 frame khi vua nhan chuot ben trong nut. Locked -> luon false. */
    public boolean pollClick(Viewport viewport) {
        if (locked) return false;
        if (!Gdx.input.justTouched()) return false;
        tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(tmp);
        return contains(tmp.x, tmp.y);
    }

    public void draw(SpriteBatch batch, BitmapFont font) {
        float dx = x, dy = y, dw = w, dh = h;
        if (hovered && !locked) {
            float grow = 6f;
            dx -= grow; dy -= grow; dw += grow * 2; dh += grow * 2;
        }

        boolean pressed = pressedInside && !locked;
        TextureRegion img = resolveImage(pressed);

        if (img != null) {
            batch.setColor(locked ? new Color(1f, 1f, 1f, 0.45f) : Color.WHITE);
            batch.draw(img, dx, dy, dw, dh);
            batch.setColor(Color.WHITE);
        } else {
            if (locked) batch.setColor(1f, 1f, 1f, 0.45f);
            UiKit.get().button(batch, dx, dy, dw, dh, pressed);
            batch.setColor(Color.WHITE);
        }

        if (drawLabel && font != null && text != null) {
            font.setColor(locked ? new Color(0.9f, 0.9f, 0.9f, 0.6f) : UiKit.CREAM);
            layout.setText(font, text);
            float tx = dx + (dw - layout.width) / 2f;
            float ty = dy + (dh + layout.height) / 2f;
            font.draw(batch, text, tx, ty);
            font.setColor(Color.WHITE);
        }
    }

    /** Tim anh phu hop: uu tien "<name>_down" khi nhan, roi "<name>". */
    private TextureRegion resolveImage(boolean pressed) {
        if (imageName == null) return null;
        AssetProvider ap = AssetProvider.get();
        if (pressed) {
            TextureRegion down = ap.region(imageName + "_down");
            if (down != null) return down;
        }
        return ap.region(imageName);
    }

    public boolean contains(float px, float py) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }
}
