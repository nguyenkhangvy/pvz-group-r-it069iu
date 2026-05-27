package com.pvz.screen;

import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.MenuButton;

/**
 * LoseScreen (GAME OVER) - chi can 1 anh nen (da co logo GAME OVER + chu)
 * va 2 nut anh (RESTART, MENU).
 *
 * Anh can them vao assets/images/ (folder nao cung duoc, ten khong trung):
 *   bg_gameover  - anh nen full man 1280x720 (da ve san logo + chu).
 *   btn_restart  - nut RESTART.   (co the them btn_restart_down cho luc nhan)
 *   btn_menu     - nut MENU.      (co the them btn_menu_down)
 *
 * Logic giu nguyen: Restart -> ChoosePlantScreen cua chinh level do (save khong tut).
 * Menu -> StartupScreen.
 */
public class LoseScreen extends BaseScreen {

    private final int level;
    private final MenuButton restartBtn, menuBtn;

    // ---- toa do 2 nut tu mockup ----
    private static final float BTN_W = 227f, BTN_H = 66f, BTN_Y = 156f;
    private static final float RESTART_X = 383f, MENU_X = 642f;

    public LoseScreen(int level) {
        this.level = level;
        SaveManager.get().onLevelLost(level); // save giu nguyen

        restartBtn = new MenuButton("RESTART", RESTART_X, BTN_Y, BTN_W, BTN_H)
                         .setImage("btn_restart", false);
        menuBtn    = new MenuButton("MENU",    MENU_X,    BTN_Y, BTN_W, BTN_H)
                         .setImage("btn_menu", false);
    }

    @Override
    protected void update(float delta) {
        restartBtn.update(viewport);
        menuBtn.update(viewport);
        if (restartBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new ChoosePlantScreen(level));
        } else if (menuBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    @Override
    protected void draw() {
        batch.begin();

        // anh nen full man (da co logo GAME OVER + chu + trang tri)
        drawBackground("bg_gameover");

        // 2 nut
        restartBtn.draw(batch, null);
        menuBtn.draw(batch, null);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}