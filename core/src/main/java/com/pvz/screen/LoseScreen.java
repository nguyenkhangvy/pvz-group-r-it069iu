package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;

/**
 * LoseScreen: hien Menu va Restart.
 * Restart -> quay lai ChoosePlantScreen cua CHINH level do (theo yeu cau).
 * Save GIU nguyen (khong tut level).
 */
public class LoseScreen extends BaseScreen {

    private final int level;
    private final Stage stage;
    private final Skin skin;

    public LoseScreen(int level) {
        this.level = level;
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        SaveManager.get().onLevelLost(level); // no-op, giu nguyen save
        build();
    }

    private void build() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("GAME OVER - Level 1-" + level, skin)).padBottom(20).row();

        TextButton restart = new TextButton("Restart", skin);
        restart.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new ChoosePlantScreen(level));
            }
        });
        TextButton menu = new TextButton("Menu", skin);
        menu.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new StartupScreen());
            }
        });
        root.add(restart).width(220).height(55).padBottom(10).row();
        root.add(menu).width(220).height(55).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() { stage.draw(); }
    @Override public void dispose() { super.dispose(); stage.dispose(); skin.dispose(); }
}
