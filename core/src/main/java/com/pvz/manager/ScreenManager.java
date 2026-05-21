package com.pvz.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

/**
 * ScreenManager (SINGLETON): trung tam dieu huong giua 6 man hinh.
 * Cac screen goi ScreenManager.get().goToXxx() de chuyen man.
 *
 * Giu tham chieu toi Game (PvzGame) de goi setScreen().
 */
public final class ScreenManager {

    private static ScreenManager instance;
    private Game game;

    private ScreenManager() {}

    public static ScreenManager get() {
        if (instance == null) instance = new ScreenManager();
        return instance;
    }

    public void init(Game game) { this.game = game; }

    public void setScreen(Screen screen) {
        Screen old = game.getScreen();
        game.setScreen(screen);
        if (old != null) old.dispose();
    }
}
