package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.pvz.core.GameConfig;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.util.DebugDraw;

/**
 * PauseMenu: menu cai dat hien khi PAUSE giua game (overlay).
 *
 * Ve tay bang DebugDraw + BitmapFont cho dong nhat voi GameScreen (khong tron Scene2D).
 * Xu ly nut bam bang chuot. Vi the gioi DUNG khi pause, menu nay duoc cap nhat
 * bang realDelta (luon song) - dung tinh than "van khoa thoi gian".
 *
 * Cac muc:
 *  - Resume
 *  - Theme Music  : ON/OFF
 *  - Click Sound  : ON/OFF
 *  - Game Sound   : ON/OFF
 *  - Restart      : choi lai level (ve choosing plant cua level do)
 *  - Main Menu    : ve man hinh chinh
 *
 * PauseMenu KHONG tu chuyen man; no bao hanh dong ra ngoai qua getAction()
 * de GameScreen quyet dinh (giu quyen dieu huong o GameScreen).
 */
public class PauseMenu {

    /** Hanh dong nguoi choi chon trong menu (GameScreen doc va xu ly). */
    public enum Action { NONE, RESUME, RESTART, MAIN_MENU }

    // bo cuc panel
    private static final float PANEL_W = 460f;
    private static final float PANEL_H = 470f;
    private static final float BTN_W = 360f;
    private static final float BTN_H = 56f;
    private static final float BTN_GAP = 14f;

    // mau
    private static final Color DIM      = new Color(0f, 0f, 0f, 0.6f);   // lop mo nen
    private static final Color PANEL_BG = new Color(0.16f, 0.13f, 0.10f, 0.96f);
    private static final Color PANEL_BD = new Color(0.55f, 0.42f, 0.22f, 1f);
    private static final Color BTN_BG   = new Color(0.30f, 0.45f, 0.22f, 1f);
    private static final Color BTN_HOV  = new Color(0.42f, 0.62f, 0.30f, 1f);
    private static final Color ON_COLOR  = new Color(0.35f, 0.75f, 0.35f, 1f);
    private static final Color OFF_COLOR = new Color(0.55f, 0.30f, 0.30f, 1f);
    private static final Color TXT      = new Color(1f, 1f, 1f, 1f);

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final Vector3 mouse = new Vector3();

    // toa do panel (tinh 1 lan)
    private final float panelX, panelY;
    private final float btnX;
    private final float[] btnY = new float[6]; // 6 nut

    private Action action = Action.NONE;

    public PauseMenu(BitmapFont font) {
        this.font = font;
        panelX = GameConfig.WORLD_WIDTH / 2f - PANEL_W / 2f;
        panelY = GameConfig.WORLD_HEIGHT / 2f - PANEL_H / 2f;
        btnX = GameConfig.WORLD_WIDTH / 2f - BTN_W / 2f;

        // xep 6 nut tu tren xuong trong panel
        float top = panelY + PANEL_H - 90f;
        for (int i = 0; i < btnY.length; i++) {
            btnY[i] = top - i * (BTN_H + BTN_GAP);
        }
    }

    /** Reset hanh dong moi lan mo menu. */
    public void reset() { action = Action.NONE; }

    public Action getAction() { return action; }

    /**
     * Xu ly input cua menu (goi khi paused). camera dung de unproject chuot.
     * justTouched: true neu vua click frame nay.
     */
    public void handleInput(com.badlogic.gdx.graphics.OrthographicCamera camera) {
        if (!Gdx.input.justTouched()) return;
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        SaveManager.SettingsData s = SaveManager.get().settings();

        if (hit(0)) { action = Action.RESUME; return; }
        if (hit(1)) { AudioManager.get().setThemeOn(!s.themeMusicOn); return; }
        if (hit(2)) { AudioManager.get().setClickOn(!s.clickSoundOn); return; }
        if (hit(3)) { AudioManager.get().setGameSoundOn(!s.gameSoundOn); return; }
        if (hit(4)) { action = Action.RESTART; return; }
        if (hit(5)) { action = Action.MAIN_MENU; return; }
    }

    private boolean hit(int i) {
        return mouse.x >= btnX && mouse.x <= btnX + BTN_W
            && mouse.y >= btnY[i] && mouse.y <= btnY[i] + BTN_H;
    }

    /** Ve overlay menu. Goi trong batch.begin()/end() cua GameScreen. */
    public void draw(SpriteBatch batch, com.badlogic.gdx.graphics.OrthographicCamera camera) {
        DebugDraw dd = DebugDraw.get();
        SaveManager.SettingsData s = SaveManager.get().settings();

        // lop mo toan man
        dd.rect(batch, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, DIM);

        // vien panel + nen
        dd.rect(batch, panelX - 4, panelY - 4, PANEL_W + 8, PANEL_H + 8, PANEL_BD);
        dd.rect(batch, panelX, panelY, PANEL_W, PANEL_H, PANEL_BG);

        // tieu de
        font.setColor(1f, 0.9f, 0.4f, 1f);
        drawCentered(batch, "SETTINGS", GameConfig.WORLD_WIDTH / 2f, panelY + PANEL_H - 36f);

        // toa do chuot hien tai (de to mau hover)
        mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mouse);

        drawButton(batch, 0, "Resume");
        drawToggle(batch, 1, "Theme Music", s.themeMusicOn);
        drawToggle(batch, 2, "Click Sound", s.clickSoundOn);
        drawToggle(batch, 3, "Game Sound", s.gameSoundOn);
        drawButton(batch, 4, "Restart Level");
        drawButton(batch, 5, "Main Menu");
    }

    private void drawButton(SpriteBatch batch, int i, String text) {
        DebugDraw dd = DebugDraw.get();
        boolean hov = isOver(i);
        dd.rect(batch, btnX, btnY[i], BTN_W, BTN_H, hov ? BTN_HOV : BTN_BG);
        font.setColor(TXT);
        drawCentered(batch, text, GameConfig.WORLD_WIDTH / 2f, btnY[i] + BTN_H / 2f + 7f);
    }

    private void drawToggle(SpriteBatch batch, int i, String label, boolean on) {
        DebugDraw dd = DebugDraw.get();
        boolean hov = isOver(i);
        dd.rect(batch, btnX, btnY[i], BTN_W, BTN_H, hov ? BTN_HOV : BTN_BG);
        // o trang thai ON/OFF ben phai
        float tagW = 70f, tagH = 36f;
        float tagX = btnX + BTN_W - tagW - 12f;
        float tagY = btnY[i] + (BTN_H - tagH) / 2f;
        dd.rect(batch, tagX, tagY, tagW, tagH, on ? ON_COLOR : OFF_COLOR);

        font.setColor(TXT);
        font.draw(batch, label, btnX + 18f, btnY[i] + BTN_H / 2f + 7f);
        drawCentered(batch, on ? "ON" : "OFF", tagX + tagW / 2f, tagY + tagH / 2f + 7f);
    }

    private boolean isOver(int i) {
        return mouse.x >= btnX && mouse.x <= btnX + BTN_W
            && mouse.y >= btnY[i] && mouse.y <= btnY[i] + BTN_H;
    }

    private void drawCentered(SpriteBatch batch, String text, float centerX, float baselineY) {
        layout.setText(font, text);
        font.draw(batch, text, centerX - layout.width / 2f, baselineY + layout.height / 2f);
    }
}
