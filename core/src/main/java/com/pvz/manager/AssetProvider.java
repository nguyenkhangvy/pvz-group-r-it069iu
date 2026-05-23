package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * AssetProvider (SINGLETON): noi duy nhat nap va cap phat texture.
 *
 * Cach hoat dong (don gian, de ban bo anh vao):
 *  - Anh dat trong thu muc assets/images/ duoi dang PNG roi (moi anh 1 file).
 *  - Ten "region" ma JSON tham chieu chinh la TEN FILE (khong duoi .png).
 *      vi du JSON "idle": ["peashooter_idle"]  ->  file assets/images/peashooter_idle.png
 *  - get(name) tra ve TextureRegion, cache lai de khong nap trung.
 *  - Neu file khong ton tai -> tra ve null (entity se fallback ve khoi mau).
 *
 * Vi sao dung PNG roi thay vi TextureAtlas?
 *  - Don gian nhat cho ban: chi can keo tha file vao thu muc, dat dung ten.
 *  - Khong can cong cu dong goi atlas.
 *  - Sau nay neu muon toi uu, co the chuyen sang atlas ma KHONG doi code goi
 *    (chi sua ben trong AssetProvider).
 */
public final class AssetProvider {

    private static final String IMAGE_DIR = "images/";

    private static AssetProvider instance;

    private final ObjectMap<String, TextureRegion> cache = new ObjectMap<>();
    private final ObjectMap<String, Texture> textures = new ObjectMap<>();

    /** AssetManager da nap san o LoadingScreen (neu co). */
    private com.badlogic.gdx.assets.AssetManager assets;

    private AssetProvider() {}

    public static AssetProvider get() {
        if (instance == null) instance = new AssetProvider();
        return instance;
    }

    /** Nhan AssetManager da nap san tu LoadingScreen. */
    public void attachAssetManager(com.badlogic.gdx.assets.AssetManager am) {
        this.assets = am;
    }

    /**
     * Tra ve TextureRegion theo ten (vd "peashooter_idle").
     * Tra ve null neu khong tim thay file -> caller fallback ve khoi mau.
     */
    public TextureRegion region(String name) {
        if (name == null || name.isEmpty()) return null;
        if (cache.containsKey(name)) return cache.get(name); // co the la null da cache

        // 1) Uu tien: Texture da nap san trong AssetManager (LoadingScreen)
        if (assets != null) {
            String path = IMAGE_DIR + name + ".png";
            if (assets.isLoaded(path)) {
                Texture tex = assets.get(path, Texture.class);
                TextureRegion region = new TextureRegion(tex);
                cache.put(name, region);
                return region;
            }
        }

        // 2) Fallback: nap lazy tu dia
        FileHandle f = Gdx.files.internal(IMAGE_DIR + name + ".png");
        if (!f.exists()) {
            cache.put(name, null); // cache "khong co" de khoi kiem tra lai
            return null;
        }
        Texture tex = new Texture(f);
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textures.put(name, tex);
        TextureRegion region = new TextureRegion(tex);
        cache.put(name, region);
        return region;
    }

    /** Co it nhat 1 anh that duoc nap chua (de biet game co dang dung hinh that). */
    public boolean hasAnyImage() {
        return textures.size > 0;
    }

    public void dispose() {
        for (Texture t : textures.values()) t.dispose();
        textures.clear();
        cache.clear();
    }
}
