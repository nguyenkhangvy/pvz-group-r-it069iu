package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.manager.AudioManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;

/**
 * MusicScreen: bat/tat 3 loai am thanh (theme, click, game sound).
 * Setting luu file RIENG qua SaveManager.persistSettings().
 */
public class MusicScreen extends BaseScreen {

    private final Stage stage;
    private final Skin skin;

    public MusicScreen() {
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        build();
    }

    private void build() {
        SaveManager.SettingsData s = SaveManager.get().settings();
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        final CheckBox theme = new CheckBox(" Theme Music", skin);
        final CheckBox click = new CheckBox(" Click Sound", skin);
        final CheckBox gameS = new CheckBox(" Game Sound", skin);
        theme.setChecked(s.themeMusicOn);
        click.setChecked(s.clickSoundOn);
        gameS.setChecked(s.gameSoundOn);

        theme.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                AudioManager.get().setThemeOn(theme.isChecked());
                AudioManager.get().playClick();
            }
        });
        click.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                // set TRUOC, roi phat de nguoi dung nghe phan hoi khi vua bat
                AudioManager.get().setClickOn(click.isChecked());
                AudioManager.get().playClick();
            }
        });
        gameS.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                AudioManager.get().setGameSoundOn(gameS.isChecked());
                AudioManager.get().playClick();
            }
        });

        TextButton back = new TextButton("Back", skin);
        back.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                AudioManager.get().playClick();
                ScreenManager.get().setScreen(new StartupScreen());
            }
        });

        root.add(theme).left().padBottom(15).row();
        root.add(click).left().padBottom(15).row();
        root.add(gameS).left().padBottom(30).row();
        root.add(back).width(200).height(55).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() { stage.draw(); }
    @Override public void dispose() { super.dispose(); stage.dispose(); skin.dispose(); }
}
