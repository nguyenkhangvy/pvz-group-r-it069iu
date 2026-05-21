package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.manager.ScreenManager;

/**
 * CompleteScreen: khi nguoi choi hoan thanh TAT CA level.
 * Hien congratulation + cac tinh nang "coming soon". Nut Next -> ve man chinh.
 * (Save da duoc reset ve 1 trong SaveManager.onLevelWon khi thang level cuoi.)
 */
public class CompleteScreen extends BaseScreen {

    private final Stage stage;
    private final Skin skin;

    public CompleteScreen() {
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        build();
    }

    private void build() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("CONGRATULATIONS!", skin)).padBottom(15).row();
        root.add(new Label("You completed all levels.", skin)).padBottom(10).row();
        root.add(new Label("Coming soon: more plants, zombies & night levels!", skin)).padBottom(25).row();

        TextButton next = new TextButton("Back to Menu", skin);
        next.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new StartupScreen());
            }
        });
        root.add(next).width(240).height(60).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() { stage.draw(); }
    @Override public void dispose() { super.dispose(); stage.dispose(); skin.dispose(); }
}
