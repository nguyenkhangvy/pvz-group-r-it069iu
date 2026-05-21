package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.pvz.data.LevelData;
import com.pvz.manager.DataManager;
import com.pvz.manager.ScreenManager;
import com.pvz.system.PlantUnlockSystem;

/**
 * ChoosePlantScreen: chon cay truoc khi vao tran.
 *  - Hien cac cay nguoi choi DA CO (tinh tu lastUnlockedLevel qua PlantUnlockSystem).
 *  - So cay toi da chon = maxPlants trong level JSON. Co the chon IT hon.
 *  - Khong cho chon vuot maxPlants (nut bi khoa mau xam khi da day).
 *  - Bam Start -> vao GameScreen voi danh sach cay da chon.
 */
public class ChoosePlantScreen extends BaseScreen {

    private final int level;
    private final Stage stage;
    private final Skin skin;
    private final Array<String> chosen = new Array<>();
    private final int maxPlants;
    private Label counter;

    public ChoosePlantScreen(int level) {
        this.level = level;
        LevelData ld = DataManager.get().level(level);
        this.maxPlants = (ld != null && ld.maxPlants > 0) ? ld.maxPlants : 6;
        stage = new Stage(viewport, batch);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);
        build();
    }

    private void build() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        root.add(new Label("Level 1-" + level + " : Choose Plants", skin)).padBottom(8).row();
        counter = new Label("", skin);
        updateCounter();
        root.add(counter).padBottom(16).row();

        Array<String> available = PlantUnlockSystem.getUnlockedPlants(level);
        if (available.size == 0) {
            root.add(new Label("(Chua co data cay - se hien sau khi them JSON)", skin)).padBottom(20).row();
        }
        for (final String plantId : available) {
            final TextButton b = new TextButton(plantId, skin);
            b.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent e, Actor a) {
                    if (chosen.contains(plantId, false)) {
                        // bo chon
                        chosen.removeValue(plantId, false);
                        b.setColor(1, 1, 1, 1);
                    } else {
                        // chon them - chi khi chua day maxPlants
                        if (chosen.size >= maxPlants) return;
                        chosen.add(plantId);
                        b.setColor(0.4f, 1f, 0.4f, 1f);
                    }
                    updateCounter();
                }
            });
            root.add(b).width(220).height(45).padBottom(8).row();
        }

        TextButton start = new TextButton("Start Battle", skin);
        start.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new GameScreen(level, chosen));
            }
        });

        TextButton back = new TextButton("Back", skin);
        back.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                ScreenManager.get().setScreen(new StartupScreen());
            }
        });

        root.add(start).width(240).height(60).padTop(20).padBottom(10).row();
        root.add(back).width(240).height(50).row();
        stage.addActor(root);
    }

    private void updateCounter() {
        counter.setText("Selected: " + chosen.size + " / " + maxPlants);
    }

    @Override protected void update(float delta) { stage.act(delta); }
    @Override protected void draw() { stage.draw(); }
    @Override public void dispose() { super.dispose(); stage.dispose(); skin.dispose(); }
}
