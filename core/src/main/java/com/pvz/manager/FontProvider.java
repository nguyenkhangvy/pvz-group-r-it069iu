package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * FontProvider (SINGLETON): nap font TTF qua gdx-freetype va sinh BitmapFont
 * o bat ky size nao ngay luc chay.
 *
 * DOI KIEU CHU RAT DE: chi can
 *   1) Bo file .ttf vao assets/fonts/
 *   2) Sua hang FONT_FILE ben duoi thanh ten file do.
 * Khong can tao lai bitmap, khong cong cu ngoai.
 *
 * Vi du muon thu font khac: tai "Fredoka-Bold.ttf", bo vao assets/fonts/,
 * doi FONT_FILE = "fonts/Fredoka-Bold.ttf". Xong.
 *
 * Neu KHONG tim thay file TTF (ban chua tai ve), tu dong fallback sang
 * BitmapFont mac dinh cua libGDX de game van chay (khong crash).
 */
public final class FontProvider {

    // ====== DOI FONT O DAY ======
    // Bo file .ttf tuong ung vao assets/fonts/ roi sua ten nay.
    // Goi y font cartoon hop PvZ (tai mien phi tu Google Fonts):
    //   - "fonts/LuckiestGuy-Regular.ttf"  (giong PvZ nhat)
    //   - "fonts/Fredoka-Bold.ttf"
    //   - "fonts/Baloo2-Bold.ttf"
    //   - "fonts/Bangers-Regular.ttf"
    private static final String FONT_FILE = "fonts/LuckiestGuy-Regular.ttf";
    // =============================

    private static FontProvider instance;

    private FreeTypeFontGenerator generator; // null neu khong co file ttf
    private final ObjectMap<String, BitmapFont> cache = new ObjectMap<>();

    private FontProvider() {
        FileHandle f = Gdx.files.internal(FONT_FILE);
        if (f.exists()) {
            generator = new FreeTypeFontGenerator(f);
        } else {
            Gdx.app.log("FontProvider", "Chua co " + FONT_FILE + " -> dung font mac dinh. "
                + "Tai .ttf bo vao assets/fonts/ va sua FONT_FILE de doi kieu chu.");
        }
    }

    public static FontProvider get() {
        if (instance == null) instance = new FontProvider();
        return instance;
    }

    /**
     * Lay font theo size + mau + vien. Co cache theo (size,mau,vien) de khong
     * sinh lai moi frame.
     *
     * @param size      chieu cao chu (pixel)
     * @param color     mau chu
     * @param borderW   do day vien (0 = khong vien)
     * @param borderCol mau vien
     */
    public BitmapFont get(int size, Color color, float borderW, Color borderCol) {
        String key = size + "|" + color + "|" + borderW + "|" + borderCol;
        if (cache.containsKey(key)) return cache.get(key);

        BitmapFont font;
        if (generator != null) {
            FreeTypeFontParameter p = new FreeTypeFontParameter();
            p.size = size;
            p.color = color;
            p.borderWidth = borderW;
            p.borderColor = borderCol;
            p.shadowOffsetX = 2;
            p.shadowOffsetY = 2;
            p.shadowColor = new Color(0, 0, 0, 0.35f);
            // bo dau tieng Viet + ky tu thuong gap
            p.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "ăâđêôơưĂÂĐÊÔƠƯáàảãạấầẩẫậắằẳẵặéèẻẽẹếềểễệíìỉĩịóòỏõọốồổỗộớờởỡợúùủũụứừửữựýỳỷỹỵ"
                + "ÁÀẢÃẠẤẦẨẪẬẮẰẲẴẶÉÈẺẼẸẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌỐỒỔỖỘỚỜỞỠỢÚÙỦŨỤỨỪỬỮỰÝỲỶỹỴ";
            font = generator.generateFont(p);
        } else {
            font = new BitmapFont(); // fallback
            font.setColor(color);
            // scale xap xi theo size (font mac dinh ~15px)
            font.getData().setScale(size / 15f);
        }
        font.setUseIntegerPositions(false);
        cache.put(key, font);
        return font;
    }

    /** Tien ich: font trang khong vien. */
    public BitmapFont plain(int size, Color color) {
        return get(size, color, 0f, Color.CLEAR);
    }

    public void dispose() {
        for (BitmapFont f : cache.values()) f.dispose();
        cache.clear();
        if (generator != null) generator.dispose();
    }
}
