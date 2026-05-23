package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.core.GameConfig;
import com.pvz.manager.ScreenManager;
import com.pvz.util.DebugDraw;

/**
 * CompleteScreen: khi nguoi choi hoan thanh TAT CA level.
 * Hien congratulation + cac tinh nang "coming soon". Nut Next -> ve man chinh.
 * (Save da duoc reset ve 1 trong SaveManager.onLevelWon khi thang level cuoi.)
 */
public class CompleteScreen extends BaseScreen {

    private final Stage stage;
    

    public CompleteScreen() {
        stage = new Stage(viewport, batch);
        
        Gdx.input.setInputProcessor(stage);
        build();
    }

    private void build() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("CONGRATULATIONS!", UiAssets.skin())).padBottom(15).row();
        root.add(new Label("You completed all levels.", UiAssets.skin())).padBottom(10).row();
        root.add(new Label("Coming soon: more plants, zombies & night levels!", UiAssets.skin())).padBottom(25).row();

        TextButton next = new TextButton("Back to Menu", UiAssets.skin());
        next.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new StartupScreen());
            }
        });
        root.add(next).width(240).height(60).row();
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() {
        DebugDraw dd = DebugDraw.get();
        batch.begin();
        dd.rect(batch, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new Color(0.08f, 0.20f, 0.04f, 1f));
        batch.end();
        stage.draw();
    }
    @Override public void dispose() { super.dispose(); stage.dispose();  }
}
