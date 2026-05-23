package com.pvz;

import com.badlogic.gdx.Game;
import com.pvz.manager.AudioManager;
import com.pvz.manager.DataManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.screen.LoadingScreen;

/**
 * PvzGame: diem khoi dau. Khoi tao cac Singleton manager va vao LoadingScreen.
 * LoadingScreen nap toan bo am thanh (progress bar), xong moi sang StartupScreen
 * va bat nhac nen.
 */
public class PvzGame extends Game {

    @Override
    public void create() {
        SaveManager.get();              // nap save + settings
        DataManager.get().loadAll();    // nap toan bo JSON data
        AudioManager.get();             // san sang am thanh (nap thuc su o LoadingScreen)
        ScreenManager.get().init(this); // gan tham chieu Game de dieu huong

        setScreen(new LoadingScreen()); // -> nap asset -> StartupScreen
    }

    @Override
    public void dispose() {
        super.dispose();
        AudioManager.get().dispose();
        com.pvz.util.DebugDraw.get().dispose();
        com.pvz.manager.AssetProvider.get().dispose();
    }
}
