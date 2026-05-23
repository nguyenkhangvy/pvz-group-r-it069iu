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
import com.pvz.data.LevelData;
import com.pvz.manager.DataManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.DebugDraw;

/**
 * WinScreen: hien phan thuong (cay moi, zombie moi, tinh nang moi neu co),
 * nut Next Level va Menu.
 * Goi SaveManager.onLevelWon (mo khoa level tiep; neu het thi reset ve 1).
 * Neu vua thang level cuoi -> chuyen sang CompleteScreen.
 */
public class WinScreen extends BaseScreen {

    private final int level;
    private final Stage stage;
    

    public WinScreen(int level) {
        this.level = level;
        stage = new Stage(viewport, batch);
        
        Gdx.input.setInputProcessor(stage);

        boolean wasLast = level >= GameConfig.LAST_LEVEL;
        SaveManager.get().onLevelWon(level); // cap nhat save TRUOC khi build UI
        build(wasLast);
    }

    private void build(boolean wasLast) {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("LEVEL 1-" + level + " CLEARED!", UiAssets.skin())).padBottom(20).row();

        LevelData ld = DataManager.get().level(level);
        if (ld != null) {
            if (ld.unlockPlant != null)   root.add(new Label("New Plant: " + ld.unlockPlant, UiAssets.skin())).padBottom(8).row();
            if (ld.unlockZombie != null)  root.add(new Label("New Zombie: " + ld.unlockZombie, UiAssets.skin())).padBottom(8).row();
            if (ld.unlockFeature != null) root.add(new Label("New Feature: " + ld.unlockFeature, UiAssets.skin())).padBottom(8).row();
        }

        if (wasLast) {
            TextButton next = new TextButton("Finish", UiAssets.skin());
            next.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    ScreenManager.get().setScreen(new CompleteScreen());
                }
            });
            root.add(next).width(220).height(55).padTop(20).row();
        } else {
            TextButton next = new TextButton("Next Level", UiAssets.skin());
            next.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    ScreenManager.get().setScreen(new ChoosePlantScreen(level + 1));
                }
            });
            TextButton menu = new TextButton("Menu", UiAssets.skin());
            menu.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    ScreenManager.get().setScreen(new StartupScreen());
                }
            });
            root.add(next).width(220).height(55).padTop(20).padBottom(10).row();
            root.add(menu).width(220).height(55).row();
        }
        stage.addActor(root);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() {
        DebugDraw dd = DebugDraw.get();
        batch.begin();
        dd.rect(batch, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, new Color(0.10f, 0.25f, 0.05f, 1f));
        batch.end();
        stage.draw();
    }
    @Override public void dispose() { super.dispose(); stage.dispose();  }
}
