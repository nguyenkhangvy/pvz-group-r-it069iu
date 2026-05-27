package com.pvz.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.pvz.core.GameConfig;
import com.pvz.manager.AudioManager;
import com.pvz.manager.FontProvider;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.MenuButton;
import com.pvz.util.UiKit;

/**
 * WinScreen (LEVEL CLEARED) - chi can 1 anh nen (da co logo LEVEL CLEARED)
 * va 2 nut anh (NEXT LEVEL, MENU).
 *
 * Anh can them vao assets/images/ (folder nao cung duoc, ten khong trung):
 *   bg_win        - anh nen full man 1280x720 (da ve san logo + bang bang-ron).
 *   btn_next      - nut NEXT LEVEL. (co the them btn_next_down)
 *   btn_menu      - nut MENU (dung chung voi GameOver).
 *
 * Logic GIU NGUYEN:
 *   - onLevelWon(level): mo khoa level tiep (hoac reset ve 1 neu vua thang 1-8).
 *   - Thang level cuoi -> nut chuyen sang CompleteScreen (thay vi level tiep).
 *
 * Ve so level "1-X COMPLETED": code ve chu len bang-ron (de dung 1 anh nen chung).
 * Neu anh nen cua ban DA ghi san so, hoac khong muon hien so -> xoa khoi
 * "=== VE SO LEVEL ===" o duoi.
 */
public class WinScreen extends BaseScreen {

    private final int level;
    private final boolean wasLast;
    private BitmapFont ribbonFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final MenuButton nextBtn, menuBtn;

    // ---- toa do tu mockup ----
    private static final float NEXT_X = 463f, NEXT_Y = 233f, NEXT_W = 350f, NEXT_H = 63f;
    private static final float MENU_X = 494f, MENU_Y = 164f, MENU_W = 288f, MENU_H = 58f;
    // vi tri ve so level (giua bang-ron do)
    private static final float RIBBON_CX = 640f, RIBBON_Y = 380f;

    public WinScreen(int level) {
        this.level = level;
        this.wasLast = level >= GameConfig.LAST_LEVEL;
        SaveManager.get().onLevelWon(level);

        ribbonFont = FontProvider.get().get(26, UiKit.CREAM, 2f, UiKit.BROWN);

        nextBtn = new MenuButton("NEXT LEVEL", NEXT_X, NEXT_Y, NEXT_W, NEXT_H)
                      .setImage("btn_next", false);
        menuBtn = new MenuButton("MENU",       MENU_X, MENU_Y, MENU_W, MENU_H)
                      .setImage("btn_menu", false);
    }

    @Override
    protected void update(float delta) {
        nextBtn.update(viewport);
        menuBtn.update(viewport);

        if (nextBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            if (wasLast) ScreenManager.get().setScreen(new CompleteScreen());
            else ScreenManager.get().setScreen(new ChoosePlantScreen(level + 1));
        } else if (menuBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    @Override
    protected void draw() {
        batch.begin();

        // anh nen full man (da co logo LEVEL CLEARED + bang bang-ron)
        drawBackground("bg_win");

        // === VE SO LEVEL === (xoa ca khoi nay neu anh nen da co san so)
        if (ribbonFont != null) {
            ribbonFont.setColor(UiKit.CREAM);
            String msg = "LEVEL 1-" + level + " COMPLETED";
            layout.setText(ribbonFont, msg);
            ribbonFont.draw(batch, msg, RIBBON_CX - layout.width / 2f, RIBBON_Y);
            ribbonFont.setColor(Color.WHITE);
        }
        // === het khoi ve so ===

        // 2 nut
        nextBtn.draw(batch, null);
        menuBtn.draw(batch, null);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}