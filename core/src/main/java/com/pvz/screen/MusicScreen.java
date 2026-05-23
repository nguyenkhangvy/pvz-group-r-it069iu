package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.core.GameConfig;
import com.pvz.manager.ScreenManager;
import com.pvz.util.DebugDraw;

/**
 * MusicScreen: bat/tat 3 loai am thanh (theme, click, game sound).
 * Setting luu file RIENG qua SaveManager.persistSettings().
 */
public class MusicScreen extends BaseScreen {

    private final Stage stage;
    

    public MusicScreen() {
        stage = new Stage(viewport, batch);
        
        Gdx.input.setInputProcessor(stage);
        build();
    }

    private void build() {
        SaveManager.SettingsData s = SaveManager.get().settings();
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        final CheckBox theme = new CheckBox(" Theme Music", UiAssets.skin());
        final CheckBox click = new CheckBox(" Click Sound", UiAssets.skin());
        final CheckBox gameS = new CheckBox(" Game Sound", UiAssets.skin());
        theme.setChecked(s.themeMusicOn);
        click.setChecked(s.clickSoundOn);
        gameS.setChecked(s.gameSoundOn);

        theme.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { AudioManager.get().setThemeOn(theme.isChecked()); }
        });
        click.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { AudioManager.get().setClickOn(click.isChecked()); }
        });
        gameS.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { AudioManager.get().setGameSoundOn(gameS.isChecked()); }
        });

        TextButton back = new TextButton("Back", UiAssets.skin());
        back.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) { ScreenManager.get().setScreen(new StartupScreen()); }
        });

        root.add(theme).left().padBottom(15).row();
        root.add(click).left().padBottom(15).row();
        root.add(gameS).left().padBottom(30).row();
        root.add(back).width(200).height(55).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() {
        DebugDraw dd = DebugDraw.get();
        batch.begin();
        dd.rect(batch, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new Color(0.10f, 0.25f, 0.06f, 1f));
        batch.end();
        stage.draw();
    }
    @Override public void dispose() { super.dispose(); stage.dispose();  }
}
