package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.core.GameConfig;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.manager.AudioManager;

/**
 * StartupScreen (man WELCOME).
 *  - Start new: bat dau level 1 -> ChoosePlantScreen cua 1-1.
 *  - Continue : vao level cuoi da unlock (ChoosePlantScreen cua level do).
 *  - Music    : bat/tat 3 loai am thanh.
 */
public class StartupScreen extends BaseScreen {

    private final Stage stage;
    private final Skin skin;

    public StartupScreen() {
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        buildUi();
    }

    private void buildUi() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        TextButton startBtn = new TextButton("Start New", skin);
        TextButton continueBtn = new TextButton("Continue", skin);
        TextButton musicBtn = new TextButton("Music", skin);

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
        stage.draw();
    }

    @Override public void dispose() {
        super.dispose();
        stage.dispose();
        skin.dispose();
    }
}
