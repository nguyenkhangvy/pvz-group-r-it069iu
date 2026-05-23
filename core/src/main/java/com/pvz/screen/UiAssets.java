package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * UiAssets: quan ly Skin duy nhat cho toan bo game.
 * Load 1 lan, dung nhieu Screen, dispose khi thoat game.
 * Tranh viec moi Screen tu new Skin(...) rieng -> ton RAM & cham.
 */
public class UiAssets {
    private static Skin skin;

    public static Skin skin() {
        if (skin == null)
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        return skin;
    }

    public static void dispose() {
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
    }
}
