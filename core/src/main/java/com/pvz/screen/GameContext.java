package com.pvz.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.pvz.core.GameClock;
import com.pvz.data.LevelData;
import com.pvz.entity.LawnMower;
import com.pvz.entity.Sun;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.projectile.Projectile;
import com.pvz.entity.zombie.Zombie;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ProjectileFactory;
import com.pvz.system.GridSystem;
import com.pvz.system.LawnSystem;
import com.pvz.system.WaveSystem;

/**
 * GameContext: cau noi giua GameScreen va cac thanh phan con (GameHud, GameInput, GameWorld).
 * Thay vi truyen 15+ tham so qua constructor, cac thanh phan chi can 1 tham chieu GameContext.
 */
public interface GameContext {
    int getLevel();
    LevelData getLevelData();
    GameClock getClock();
    GridSystem getGrid();
    LawnSystem getLawnSystem();
    WaveSystem getWaveSystem();

    PlantFactory getPlantFactory();
    ProjectileFactory getProjectileFactory();

    Plant[][] getPlantGrid();
    Array<Plant> getPlants();
    Array<Zombie> getZombies();
    Array<Projectile> getProjectiles();
    Array<Sun> getSuns();
    Array<LawnMower> getMowers();
    Array<String> getSeedCards();

    int getSun();
    void addSun(int amount);
    void spendSun(int amount);

    String getSelectedPlant();
    void setSelectedPlant(String id);

    boolean isShovelSelected();
    void setShovelSelected(boolean on);

    ObjectMap<String, Float> getCardCooldown();

    SpriteBatch getBatch();
    BitmapFont getFont();
    OrthographicCamera getCamera();

    void triggerWin();
    void triggerLose();
    void triggerPause();

    float getBattleTime();
}
