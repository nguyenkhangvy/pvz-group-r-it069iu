package com.pvz.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.core.Difficulty;
import com.pvz.core.GameConfig;
import com.pvz.manager.AssetProvider;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.MenuButton;

/**
 * StartupScreen - layout theo mockup "CAPYBARA vs ZOMBARA".
 *
 * Bo cuc (toa do the gioi 1280x720, goc duoi-trai):
 *  - Nen full man           : anh "ui_bg_menu"
 *  - Logo giua tren         : anh "logo_title"
 *  - 3 nut doc giua:
 *       START NEW (xanh la)  : anh "btn_start_new"
 *       CONTINUE  (cam)      : anh "btn_continue"
 *       MUSIC     (xanh duong): anh "btn_music"
 *  - Nhan "DIFFICULTY" (vang).
 *  - 3 nut do kho:
 *       EASY  (xanh la)  : anh "btn_easy"
 *       NORMAL(vang)     : anh "btn_normal"
 *       HARD  (do)       : anh "btn_hard"
 *    + NORMAL khoa cho den khi hoan thanh full EASY.
 *    + HARD   khoa cho den khi hoan thanh full NORMAL.
 *    + Nut khoa ve mo, bam khong an.
 */
public class StartupScreen extends BaseScreen {

    private BitmapFont btnFont, smallFont;
    private final MenuButton startBtn, continueBtn, musicBtn;
    private final MenuButton easyBtn, normalBtn, hardBtn;

    // ---- toa do quy doi tu mockup (xem tinh toan trong tai lieu) ----
    // 3 nut doc giua
    private static final float MAIN_X = 428f, MAIN_W = 424f, MAIN_H = 80f;
    private static final float START_Y = 300f;
    private static final float ROW_GAP = 80f;

    // 3 nut difficulty
    private static final float DIFF_Y = 40f, DIFF_H = 54f;

    public StartupScreen() {
        float x = MAIN_X;

        startBtn = new MenuButton("START NEW", x, START_Y, MAIN_W, MAIN_H)
                .setImage("btn_start_new", false);

        continueBtn = new MenuButton("CONTINUE", x, START_Y - ROW_GAP, MAIN_W, MAIN_H)
                .setImage("btn_continue", false);

        musicBtn = new MenuButton("MUSIC", x, START_Y - 2 * ROW_GAP, MAIN_W, MAIN_H)
                .setImage("btn_music", false);

        // 3 nut difficulty - can giua, 3 o ngang
        float dw = 150f, dgap = 22f;
        float totalW = 3 * dw + 2 * dgap;
        float dx = GameConfig.WORLD_WIDTH / 2f - totalW / 2f;

        easyBtn = new MenuButton("EASY", dx, DIFF_Y, dw, DIFF_H)
                .setImage("btn_easy", false);

        normalBtn = new MenuButton("NORMAL", dx + (dw + dgap), DIFF_Y, dw, DIFF_H)
                .setImage("btn_normal", false);

        hardBtn = new MenuButton("HARD", dx + 2 * (dw + dgap), DIFF_Y, dw, DIFF_H)
                .setImage("btn_hard", false);
    }

    @Override
    protected void update(float delta) {
        SaveManager sm = SaveManager.get();

        // cap nhat trang thai khoa cho 3 nut difficulty truoc khi update
        normalBtn.setLocked(!sm.isDifficultyUnlocked(Difficulty.NORMAL));
        hardBtn.setLocked(!sm.isDifficultyUnlocked(Difficulty.HARD));

        startBtn.update(viewport);
        continueBtn.update(viewport);
        musicBtn.update(viewport);
        easyBtn.update(viewport);
        normalBtn.update(viewport);
        hardBtn.update(viewport);

        // chon do kho (nut locked tu tra false trong pollClick)
        if (easyBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            sm.setDifficulty(Difficulty.EASY);

        } else if (normalBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            sm.setDifficulty(Difficulty.NORMAL);

        } else if (hardBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            sm.setDifficulty(Difficulty.HARD);
        }

        // Start / Continue / Music
        else if (startBtn.pollClick(viewport)) {
            AudioManager.get().playClick();

            sm.resetToFirstLevel();

            markSwitched();

            ScreenManager.get().setScreen(
                    new ChoosePlantScreen(GameConfig.FIRST_LEVEL)
            );

        } else if (continueBtn.pollClick(viewport)) {
            AudioManager.get().playClick();

            int lv = sm.getLastUnlockedLevel();

            markSwitched();

            ScreenManager.get().setScreen(
                    new ChoosePlantScreen(lv)
            );

        } else if (musicBtn.pollClick(viewport)) {
            AudioManager.get().playClick();

            markSwitched();

            ScreenManager.get().setScreen(new MusicScreen());
        }
    }

    @Override
    protected void draw() {
        SaveManager sm = SaveManager.get();
        Difficulty cur = sm.getDifficulty();

        batch.begin();

        // 1) Nen full man
        drawBackground("ui_bg_menu");
        // 3) 3 nut doc
        startBtn.draw(batch, btnFont);
        continueBtn.draw(batch, btnFont);
        musicBtn.draw(batch, btnFont);

        // 5) 3 nut do kho
        drawDifficultyButton(easyBtn, Difficulty.EASY, cur);
        drawDifficultyButton(normalBtn, Difficulty.NORMAL, cur);
        drawDifficultyButton(hardBtn, Difficulty.HARD, cur);

        batch.end();
    }

    // ve nut difficulty + highlight nut dang chon
    private void drawDifficultyButton(
            MenuButton btn,
            Difficulty d,
            Difficulty cur
    ) {
        boolean selected = (cur == d);

        // ve nut
        btn.draw(batch, smallFont);

        // neu dang chon 
        if (selected && !btn.isLocked()) {
            TextureRegion frame = AssetProvider.get().region("select_frame");
            float pad = 8f;
            batch.setColor(Color.WHITE);
            batch.draw(frame, btn.x - pad+2f, btn.y - pad + 2f, btn.w + pad * 2-2f, btn.h + pad * 2);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        // KHONG dispose font: dung chung qua FontProvider
    }
}