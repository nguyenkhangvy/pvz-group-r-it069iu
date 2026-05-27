package com.pvz.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.pvz.manager.AssetProvider;

/**
 * AnimationComponent: quan ly animation nhieu frame theo TRANG THAI.
 *
 * Cach dung:
 *  - Khi tao entity, nap cac trang thai tu danh sach ten region (lay tu JSON).
 *      vd: addState("idle", ["peashooter_idle"]) hoac
 *          addState("shooting", ["pea_shoot_0","pea_shoot_1","pea_shoot_2"])
 *  - Moi frame goi update(delta) de chay thoi gian (delta tu GameClock).
 *  - Goi setState("shooting") khi doi trang thai.
 *  - getFrame() tra ve TextureRegion hien tai (hoac null neu trang thai do
 *    khong co anh -> entity fallback ve khoi mau).
 *
 * Neu mot ten region khong co file anh, AssetProvider tra null va frame do bi
 * bo qua. Neu CA trang thai khong co anh nao -> getFrame() tra null.
 *
 * 1 frame = anh tinh (khong "chay"). Nhieu frame = animation lap.
 */
public class AnimationComponent {

    /** Bao lau doi 1 frame (giay). Co the chinh rieng tung entity neu can. */
    private float frameDuration = 0.15f;

    private final ObjectMap<String, Array<TextureRegion>> states = new ObjectMap<>();
    private String current = null;
    private float timer = 0f;
    private int frameIndex = 0;

    public AnimationComponent() {}

    public AnimationComponent(float frameDuration) {
        this.frameDuration = frameDuration;
    }

    /**
     * Nap 1 trang thai tu mang ten region. Cac ten khong co file se bi bo qua.
     * Neu khong co ten nao hop le, trang thai do se rong (getFrame tra null).
     */
    public void addState(String stateName, String[] regionNames) {
        if (stateName == null || regionNames == null) return;
        Array<TextureRegion> frames = new Array<>();
        for (String rn : regionNames) {
            TextureRegion r = AssetProvider.get().region(rn);
            if (r != null) frames.add(r);
        }
        states.put(stateName, frames);
        if (current == null && frames.size > 0) current = stateName;
    }

    /** Doi trang thai. Reset frame ve dau neu khac trang thai cu. */
    public void setState(String stateName) {
        if (stateName == null || stateName.equals(current)) return;
        if (!states.containsKey(stateName)) return;
        current = stateName;
        timer = 0f;
        frameIndex = 0;
    }

    /** Chay thoi gian animation. delta tu GameClock (da scale/pause). */
    public void update(float delta) {
        if (current == null) return;
        Array<TextureRegion> frames = states.get(current);
        if (frames == null || frames.size <= 1) return; // 1 frame thi khong can chay
        timer += delta;
        while (timer >= frameDuration) {
            timer -= frameDuration;
            frameIndex = (frameIndex + 1) % frames.size;
        }
    }

    /** Frame hien tai, hoac null neu trang thai hien tai khong co anh. */
    public TextureRegion getFrame() {
        if (current == null) return null;
        Array<TextureRegion> frames = states.get(current);
        if (frames == null || frames.size == 0) return null;
        if (frameIndex >= frames.size) frameIndex = 0;
        return frames.get(frameIndex);
    }

    public String getCurrentState() { return current; }
}
