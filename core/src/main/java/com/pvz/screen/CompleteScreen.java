package com.pvz.screen;

import com.pvz.manager.AudioManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.MenuButton;

/**
 * CompleteScreen (ALL LEVELS COMPLETED) - chi can 1 anh nen (da co logo + chu
 * chuc mung + 2 o COMING SOON) va 1 nut anh BACK.
 *
 * Anh can them vao assets/images/ (folder nao cung duoc, ten khong trung):
 *   bg_complete  - anh nen full man 1280x720 (da ve san moi thu).
 *   btn_back     - nut BACK (dung chung voi MusicScreen neu muon).
 *
 * Logic: save da duoc reset ve 1 truoc do (trong onLevelWon khi thang level cuoi).
 * Nut BACK -> ve StartupScreen.
 */
public class CompleteScreen extends BaseScreen {

    private final MenuButton backBtn;

    // ---- toa do nut BACK tu mockup ----
    private static final float BACK_X = 492f, BACK_Y = 3f, BACK_W = 296f, BACK_H = 63f;

    public CompleteScreen() {
        backBtn = new MenuButton("BACK", BACK_X, BACK_Y, BACK_W, BACK_H)
                      .setImage("btn_back", false);
    }

    @Override
    protected void update(float delta) {
        backBtn.update(viewport);
        if (backBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    @Override
    protected void draw() {
        batch.begin();

        // anh nen full man (da co logo ALL LEVELS COMPLETED + chu + COMING SOON)
        drawBackground("bg_complete");

        // nut BACK
        backBtn.draw(batch, null);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}