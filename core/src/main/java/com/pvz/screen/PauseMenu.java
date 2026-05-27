package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.pvz.core.GameConfig;
import com.pvz.manager.AssetProvider;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;

/**
 * PauseMenu (SETTINGS overlay khi pause) - phien ban don gian.
 *
 * Ban chi can them vao assets/images/ (folder nao cung duoc, ten khong trung):
 *   pause_panel    - anh nen overlay: da co title SETTINGS + 3 hang
 *                    (icon + nhan THEME / CLICK / GAME). Ve can giua man.
 *   toggle_on      - nut bat ON.
 *   toggle_off     - nut tat OFF.
 *   btn_resume     - nut RESUME.
 *   btn_restart    - nut RESTART LEVEL.
 *   btn_main_menu  - nut MAIN MENU.
 *
 * Code chi: lop mo + ve panel + dat 3 toggle dung vi tri 3 hang + 3 nut.
 * Bam vung hang -> dao ON/OFF.
 *
 * GIU NGUYEN API (handleInput/getAction/reset/draw + enum Action) -> GameScreen
 * khong phai sua. Cap nhat bang realDelta khi pause -> menu van song.
 *
 * Chinh toa do o dau file cho khop anh panel cua ban.
 */
public class PauseMenu {

    public enum Action { NONE, RESUME, RESTART, MAIN_MENU }

    // ---- panel (ve can giua man) ----
    private static final float PANEL_W = 767f, PANEL_H = 563f;
    private static final float PANEL_X = (GameConfig.WORLD_WIDTH - PANEL_W) / 2f;
    private static final float PANEL_Y = (GameConfig.WORLD_HEIGHT - PANEL_H) / 2f;

    // ---- 3 toggle on/off (vi tri tuyet doi tren man, chinh cho khop panel) ----
    private static final float TOGGLE_X = 675f;
    private static final float TOGGLE_W = 125f, TOGGLE_H = 46f;
    private static final float ROW_THEME_Y = 470f;
    private static final float ROW_CLICK_Y = 383f;
    private static final float ROW_GAME_Y  = 296f;
    // vung bam ca hang
    private static final float ROW_HIT_X = 342f, ROW_HIT_W = 600f, ROW_HIT_H = 70f;

    // ---- 3 nut ----
    private static final float BTN_W = 300f, BTN_H = 64f;
    private static final float RESUME_X = 357f, RESTART_X = 590f, BTN_TOP_Y = 192f;
    private static final float MENU_X = 475f, BTN_BOT_Y = 125f;

    private final Vector3 mouse = new Vector3();
    private final float[] toggleY = { ROW_THEME_Y, ROW_CLICK_Y, ROW_GAME_Y };

    private Action action = Action.NONE;

    /** Giu chu ky constructor cu (nhan font) de GameScreen khong phai sua. */
    public PauseMenu(BitmapFont font) {
        // font khong con dung (chu da nam trong anh), giu tham so cho tuong thich.
    }

    public void reset() { action = Action.NONE; }
    public Action getAction() { return action; }

    public void handleInput(OrthographicCamera camera) {
        if (!Gdx.input.justTouched()) return;
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        // 3 hang toggle (bam ca hang)
        for (int i = 0; i < 3; i++) {
            float rowY = toggleY[i] + TOGGLE_H / 2f - ROW_HIT_H / 2f;
            if (inRect(ROW_HIT_X, rowY, ROW_HIT_W, ROW_HIT_H)) {
                toggleRow(i);
                AudioManager.get().playClick();
                return;
            }
        }
        // 3 nut
        if (inRect(RESUME_X, BTN_TOP_Y, BTN_W, BTN_H)) {
            AudioManager.get().playClick(); action = Action.RESUME; return;
        }
        if (inRect(RESTART_X, BTN_TOP_Y, BTN_W, BTN_H)) {
            AudioManager.get().playClick(); action = Action.RESTART; return;
        }
        if (inRect(MENU_X, BTN_BOT_Y, BTN_W, BTN_H)) {
            AudioManager.get().playClick(); action = Action.MAIN_MENU; return;
        }
    }

    private boolean inRect(float x, float y, float w, float h) {
        return mouse.x >= x && mouse.x <= x + w && mouse.y >= y && mouse.y <= y + h;
    }

    private void toggleRow(int i) {
        AudioManager.get().toggleSetting(i);
    }

    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        AssetProvider ap = AssetProvider.get();
        SaveManager.SettingsData s = SaveManager.get().settings();
        boolean[] on = { s.themeMusicOn, s.clickSoundOn, s.gameSoundOn };

        // lop mo toan man (game dong bang phia sau)
        com.pvz.util.DebugDraw.get().rect(batch, 0, 0,
            GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new Color(0f, 0f, 0f, 0.6f));
        batch.setColor(Color.WHITE);

        // panel nen (title + icon + nhan da co san trong anh)
        TextureRegion panel = ap.region("pause_panel");
        if (panel != null) {
            batch.draw(panel, PANEL_X, PANEL_Y, PANEL_W, PANEL_H);
        }

        // 3 toggle
        for (int i = 0; i < 3; i++) {
            TextureRegion tg = ap.region(on[i] ? "toggle_on" : "toggle_off");
            if (tg != null) batch.draw(tg, TOGGLE_X, toggleY[i], TOGGLE_W, TOGGLE_H);
        }

        // 3 nut
        drawBtn(batch, ap, "btn_resume",    RESUME_X,  BTN_TOP_Y);
        drawBtn(batch, ap, "btn_restart",   RESTART_X, BTN_TOP_Y);
        drawBtn(batch, ap, "btn_main_menu", MENU_X,    BTN_BOT_Y);

        batch.setColor(Color.WHITE);
    }

    private void drawBtn(SpriteBatch batch, AssetProvider ap, String name, float x, float y) {
        TextureRegion img = ap.region(name);
        if (img != null) {
            batch.setColor(Color.WHITE);
            batch.draw(img, x, y, BTN_W, BTN_H);
        }
    }
}