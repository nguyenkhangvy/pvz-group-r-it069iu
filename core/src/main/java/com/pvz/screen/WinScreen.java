package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.manager.DataManager;
import com.pvz.manager.SaveManager;
import com.pvz.manager.ScreenManager;

/**
 * WinScreen: hien phan thuong (cay moi, zombie moi, tinh nang moi neu co),
 * nut Next Level va Menu.
 * Goi SaveManager.onLevelWon (mo khoa level tiep; neu het thi reset ve 1).
 * Neu vua thang level cuoi -> chuyen sang CompleteScreen.
 */
public class WinScreen extends BaseScreen {

    private final int level;
    private final Stage stage;
    private final Skin skin;

    public WinScreen(int level) {
        this.level = level;
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        boolean wasLast = level >= GameConfig.LAST_LEVEL;
        SaveManager.get().onLevelWon(level); // cap nhat save TRUOC khi build UI
        build(wasLast);
    }

    private void build(boolean wasLast) {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(new Label("LEVEL 1-" + level + " CLEARED!", skin)).padBottom(20).row();

        LevelData ld = DataManager.get().level(level);
        if (ld != null) {
            if (ld.unlockPlant != null)   root.add(new Label("New Plant: " + ld.unlockPlant, skin)).padBottom(8).row();
            if (ld.unlockZombie != null)  root.add(new Label("New Zombie: " + ld.unlockZombie, skin)).padBottom(8).row();
            if (ld.unlockFeature != null) root.add(new Label("New Feature: " + ld.unlockFeature, skin)).padBottom(8).row();
        }

        if (wasLast) {
            TextButton next = new TextButton("Finish", skin);
            next.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    ScreenManager.get().setScreen(new CompleteScreen());
                }
            });
            root.add(next).width(220).height(55).padTop(20).row();
        } else {
            TextButton next = new TextButton("Next Level", skin);
            next.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    ScreenManager.get().setScreen(new ChoosePlantScreen(level + 1));
                }
            });
            TextButton menu = new TextButton("Menu", skin);
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
    @Override protected void draw() { stage.draw(); }
    @Override public void dispose() { super.dispose(); stage.dispose(); skin.dispose(); }
}
