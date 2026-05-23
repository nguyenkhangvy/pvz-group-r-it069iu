package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.core.GameConfig;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.manager.AudioManager;
import com.pvz.util.DebugDraw;

/**
 * StartupScreen (man WELCOME).
 *  - Start new: bat dau level 1 -> ChoosePlantScreen cua 1-1.
 *  - Continue : vao level cuoi da unlock (ChoosePlantScreen cua level do).
 *  - Music    : bat/tat 3 loai am thanh.
 */
public class StartupScreen extends BaseScreen {

    private final Stage stage;
    private BitmapFont titleFont;
    private final GlyphLayout layout = new GlyphLayout();

    private static final Color BG_TOP   = new Color(0.10f, 0.28f, 0.06f, 1f);
    private static final Color BG_BOT   = new Color(0.18f, 0.42f, 0.08f, 1f);
    private static final Color TITLE_CLR = new Color(1.00f, 0.90f, 0.10f, 1f);
    private static final Color SUB_CLR   = new Color(0.75f, 0.95f, 0.55f, 1f);

    public StartupScreen() {
        stage = new Stage(viewport, batch);
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        Gdx.input.setInputProcessor(stage);
        buildUi();
    }

    private void buildUi() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        TextButton startBtn = new TextButton("Start New", UiAssets.skin());
        TextButton continueBtn = new TextButton("Continue", UiAssets.skin());
        TextButton musicBtn = new TextButton("Music", UiAssets.skin());

        startBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, com.badlogic.gdx.scenes.scene2d.Actor a) {
                AudioManager.get().playClick();
                // Start New: dat lai tien do ve level 1 (Continue sau do cung se la 1)
                SaveManager.get().resetToFirstLevel();
                ScreenManager.get().setScreen(new ChoosePlantScreen(GameConfig.FIRST_LEVEL));
            }
        });

        continueBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, com.badlogic.gdx.scenes.scene2d.Actor a) {
                AudioManager.get().playClick();
                int lv = SaveManager.get().getLastUnlockedLevel();
                ScreenManager.get().setScreen(new ChoosePlantScreen(lv));
            }
        });

        musicBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, com.badlogic.gdx.scenes.scene2d.Actor a) {
                AudioManager.get().playClick();
                ScreenManager.get().setScreen(new MusicScreen());
            }
        });

        root.add(startBtn).width(260).height(60).padBottom(20).row();
        root.add(continueBtn).width(260).height(60).padBottom(20).row();
        root.add(musicBtn).width(260).height(60).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) {
        stage.act(delta);
    }

    @Override protected void draw() {
        DebugDraw dd = DebugDraw.get();
        batch.begin();
        float mid = GameConfig.WORLD_HEIGHT / 2f;
        dd.rect(batch, 0, mid, GameConfig.WORLD_WIDTH, mid, BG_TOP);
        dd.rect(batch, 0, 0,   GameConfig.WORLD_WIDTH, mid, BG_BOT);

        // Tieu de lon
        titleFont.setColor(TITLE_CLR);
        layout.setText(titleFont, "Plants vs Zombies");
        titleFont.draw(batch, "Plants vs Zombies",
            (GameConfig.WORLD_WIDTH - layout.width) / 2f,
            GameConfig.WORLD_HEIGHT - 60f);

        // Sub
        titleFont.getData().setScale(1.2f);
        titleFont.setColor(SUB_CLR);
        layout.setText(titleFont, "Defend your garden!");
        titleFont.draw(batch, "Defend your garden!",
            (GameConfig.WORLD_WIDTH - layout.width) / 2f,
            GameConfig.WORLD_HEIGHT - 115f);
        titleFont.getData().setScale(3f);
        batch.end();
        stage.draw();
    }

    @Override public void dispose() {
        super.dispose();
        stage.dispose();
        if (titleFont != null) titleFont.dispose();
    }
}
