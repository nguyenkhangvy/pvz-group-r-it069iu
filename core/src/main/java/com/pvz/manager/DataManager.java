package com.pvz.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.pvz.data.LevelData;
import com.pvz.data.PlantData;
import com.pvz.data.ProjectileData;
import com.pvz.data.ZombieData;

/**
 * DataManager (SINGLETON): nap va cache toan bo du lieu JSON.
 *
 * Doc tu cac thu muc:
 *   assets/data/plants/*.json
 *   assets/data/zombies/*.json
 *   assets/data/projectiles/*.json
 *   assets/data/levels/level_1_X.json
 *
 * Vi ban se DE (ghi de) data that vao cac file nay sau, DataManager chi can
 * dung dung ten file + ten field. Neu file trong/loi, no log canh bao thay vi crash.
 */
public final class DataManager {

    private static DataManager instance;

    private final ObjectMap<String, PlantData> plants = new ObjectMap<>();
    private final ObjectMap<String, ZombieData> zombies = new ObjectMap<>();
    private final ObjectMap<String, ProjectileData> projectiles = new ObjectMap<>();
    private final ObjectMap<String, LevelData> levels = new ObjectMap<>();

    private DataManager() {}

    public static DataManager get() {
        if (instance == null) instance = new DataManager();
        return instance;
    }

    /** Goi 1 lan khi khoi dong game. */
    public void loadAll() {
        Json json = new Json();
        loadDir(json, "data/plants", PlantData.class, plants);
        loadDir(json, "data/zombies", ZombieData.class, zombies);
        loadDir(json, "data/projectiles", ProjectileData.class, projectiles);
        loadLevels(json);
    }

    private <T> void loadDir(Json json, String dir, Class<T> type, ObjectMap<String, T> target) {
        FileHandle folder = Gdx.files.internal(dir);
        if (!folder.exists()) {
            Gdx.app.log("DataManager", "Thu muc chua ton tai: " + dir);
            return;
        }
        for (FileHandle f : folder.list(".json")) {
            try {
                T data = json.fromJson(type, f);
                String key = f.nameWithoutExtension();
                target.put(key, data);
            } catch (Exception e) {
                Gdx.app.error("DataManager", "Loi doc " + f.path() + " (co the file dang trong)", e);
            }
        }
    }

    private void loadLevels(Json json) {
        FileHandle folder = Gdx.files.internal("data/levels");
        if (!folder.exists()) {
            Gdx.app.log("DataManager", "Chua co thu muc data/levels");
            return;
        }
        for (FileHandle f : folder.list(".json")) {
            try {
                LevelData data = json.fromJson(LevelData.class, f);
                levels.put(f.nameWithoutExtension(), data);
            } catch (Exception e) {
                Gdx.app.error("DataManager", "Loi doc level " + f.path(), e);
            }
        }
    }

    // ----- truy van -----
    public PlantData plant(String id) { return plants.get(id); }
    public ZombieData zombie(String id) { return zombies.get(id); }
    public ProjectileData projectile(String id) { return projectiles.get(id); }

    /** levelNumber 1..8 -> doc file "level_1_<n>". */
    public LevelData level(int levelNumber) {
        return levels.get("level_1_" + levelNumber);
    }

    public ObjectMap<String, PlantData> allPlants() { return plants; }
}
