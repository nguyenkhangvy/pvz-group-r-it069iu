package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.pvz.core.GameConfig;
import com.pvz.manager.AssetProvider;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.MenuButton;

/**
 * MusicScreen (SOUND SETTINGS) - phien ban don gian:
 * chi can 3 anh insert vao assets/images/ (folder nao cung duoc, ten khong trung):
 *
 *   sound_panel  - anh KHUNG lon: da co san logo + 3 hang (icon + nhan
 *                  THEME MUSIC / CLICK SOUND / GAME SOUND). Ve full man hoac
 *                  can giua tuy ban thiet ke anh (o day ve full 1280x720).
 *   toggle_on    - anh nut bat (ON).
 *   toggle_off   - anh nut tat (OFF).
 *   btn_back     - nut BACK.
 *
 * Code chi: ve khung + dat 3 toggle dung vi tri 3 hang + nut back.
 * Bam vao vung hang (ca dai) -> dao ON/OFF cho de bam.
 *
 * Neu vi tri toggle/back lech so voi khung cua ban -> chinh cac hang so
 * ROW_*_Y, TOGGLE_X, TOGGLE_W/H, BACK_* o dau file.
 */
public class MusicScreen extends BaseScreen {

    private final Vector3 tmp = new Vector3();
    private final MenuButton backBtn;

    // ---- vi tri toggle 3 hang (chinh cho khop khung cua ban) ----
    // toggle nam ben phai moi hang
    private static final float TOGGLE_X = 800f;   // x goc trai cua toggle
    private static final float TOGGLE_W = 124f, TOGGLE_H = 50f;
    private static final float ROW_THEME_Y = 444f;  // y cua toggle hang Theme
    private static final float ROW_CLICK_Y = 334f;  // y hang Click
    private static final float ROW_GAME_Y  = 224f;  // y hang Game

    // vung bam (ca hang) - rong tu trai panel toi het toggle
    private static final float ROW_HIT_X = 331f, ROW_HIT_W = 600f, ROW_HIT_H = 77f;

    // nut back
    private static final float BACK_W = 260f, BACK_H = 66f, BACK_Y = 124f;

    private final float[] toggleY = { ROW_THEME_Y, ROW_CLICK_Y, ROW_GAME_Y };

    public MusicScreen() {
        float cx = GameConfig.WORLD_WIDTH / 2f;
        backBtn = new MenuButton("BACK", cx - BACK_W / 2f, BACK_Y, BACK_W, BACK_H)
                      .setImage("btn_back", false);
    }

    @Override
    protected void update(float delta) {
        backBtn.update(viewport);

        // bam vung hang -> toggle. Vung hang tinh theo tam toggle (cao ROW_HIT_H).
        if (Gdx.input.justTouched()) {
            tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(tmp);
            for (int i = 0; i < 3; i++) {
                float rowY = toggleY[i] + TOGGLE_H / 2f - ROW_HIT_H / 2f; // canh giua theo toggle
                if (tmp.x >= ROW_HIT_X && tmp.x <= ROW_HIT_X + ROW_HIT_W
                        && tmp.y >= rowY && tmp.y <= rowY + ROW_HIT_H) {
                    toggleRow(i);
                    AudioManager.get().playClick();
                    break;
                }
            }
        }

        if (backBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    private void toggleRow(int i) {
        AudioManager.get().toggleSetting(i);
    }

    @Override
    protected void draw() {
        SaveManager.SettingsData s = SaveManager.get().settings();
        boolean[] on = { s.themeMusicOn, s.clickSoundOn, s.gameSoundOn };
        AssetProvider ap = AssetProvider.get();

        batch.begin();
        batch.setColor(Color.WHITE);

        // 1) khung lon (da co logo + icon + nhan san)
        drawBackground("sound_panel");

        // 2) 3 toggle on/off dat dung vi tri
        for (int i = 0; i < 3; i++) {
            TextureRegion tg = ap.region(on[i] ? "toggle_on" : "toggle_off");
            if (tg != null) {
                batch.draw(tg, TOGGLE_X, toggleY[i], TOGGLE_W, TOGGLE_H);
            }
        }

        // 3) nut back
        backBtn.draw(batch, null);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}