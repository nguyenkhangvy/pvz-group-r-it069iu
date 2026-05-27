package com.pvz.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.pvz.core.GameClock;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.entity.LawnMower;
import com.pvz.entity.Sun;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.plant.PlantContext;
import com.pvz.entity.projectile.Projectile;
import com.pvz.entity.zombie.Zombie;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ProjectileFactory;
import com.pvz.factory.ZombieFactory;
import com.pvz.manager.AudioManager;
import com.pvz.manager.DataManager;
import com.pvz.manager.ScreenManager;
import com.pvz.system.GridSystem;
import com.pvz.system.LawnSystem;
import com.pvz.system.WaveSystem;

/**
 * GameScreen: man choi chinh - DIEU PHOI.
 *
 * Logic thuc te duoc uy thac cho 3 thanh phan:
 *   GameWorld  — update world (entity, va cham, wave, win/lose)
 *   GameInput  — xu ly touch/key
 *   GameHud    — ve HUD (sun, card, progress, shovel, speed, setting)
 *
 * GameScreen giu vai tro: so huu du lieu, van khoa thoi gian (GameClock),
 * implement GameContext + PlantContext, va dieu phoi update → draw.
 */
public class GameScreen extends BaseScreen implements GameContext, PlantContext {

    private final int level;
    private final LevelData levelData;

    private final GameClock clock = new GameClock();
    private final GridSystem grid = new GridSystem();
    private final PlantFactory plantFactory;
    private final ZombieFactory zombieFactory;
    private final ProjectileFactory projectileFactory;
    private final WaveSystem waveSystem;
    private final LawnSystem lawnSystem;

    private final Plant[][] plantGrid = new Plant[GameConfig.GRID_ROWS][GameConfig.GRID_COLS];
    private final Array<Plant> plants = new Array<>();
    private final Array<Zombie> zombies = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();
    private final Array<Sun> suns = new Array<>();
    private final Array<LawnMower> mowers = new Array<>();
    private final Array<String> seedCards = new Array<>();
    private final ObjectMap<String, Float> cardCooldown = new ObjectMap<>();

    private int sun = GameConfig.START_SUN_DEFAULT;
    private boolean paused = false;
    private boolean ended = false;
    private String selectedPlant = null;
    private boolean shovelSelected = false;

    private BitmapFont font;
    private PauseMenu pauseMenu;

    // 3 thanh phan con
    private final GameWorld world;
    private final GameInput input;
    private final GameHud hud;

    public GameScreen(int level, Array<String> chosenPlants) {
        this.level = level;
        this.plantFactory = new PlantFactory(grid);
        this.zombieFactory = new ZombieFactory(grid);
        this.projectileFactory = new ProjectileFactory();

        this.levelData = DataManager.get().level(level);
        this.lawnSystem = new LawnSystem(levelData);
        this.waveSystem = new WaveSystem(levelData, zombieFactory, lawnSystem);

        clock.resetForNewLevel();
        spawnMowers();

        if (chosenPlants != null && chosenPlants.size > 0) {
            seedCards.addAll(chosenPlants);
        } else {
            seedCards.addAll(com.pvz.system.PlantUnlockSystem.getUnlockedPlants(level));
        }
        selectedPlant = null;

        font = new BitmapFont();
        pauseMenu = new PauseMenu(font);

        // Khoi tao 3 thanh phan con
        this.world = new GameWorld(this);
        this.input = new GameInput(this);
        this.hud = new GameHud(this);
    }

    private void spawnMowers() {
        float w = 60f, h = 70f;
        for (int r = 0; r < GameConfig.GRID_ROWS; r++) {
            if (!lawnSystem.isRowActive(r)) continue;
            float x = grid.houseX() - 30f;
            float y = grid.rowToPixelY(r);
            mowers.add(new LawnMower(r, x, y, w, h));
        }
    }

    // ===================== UPDATE =====================
    @Override
    protected void update(float rawDelta) {
        input.handleGlobalInput();
        if (paused) clock.pause(); else clock.resume();
        float gameDelta = clock.tick(rawDelta);

        if (paused) {
            pauseMenu.handleInput(camera);
            switch (pauseMenu.getAction()) {
                case RESUME:
                    paused = false;
                    pauseMenu.reset();
                    break;
                case RESTART:
                    pauseMenu.reset();
                    markSwitched();
                    ScreenManager.get().setScreen(new ChoosePlantScreen(level));
                    return;
                case MAIN_MENU:
                    pauseMenu.reset();
                    markSwitched();
                    ScreenManager.get().setScreen(new StartupScreen());
                    return;
                default: break;
            }
            return;
        }

        if (ended) return;

        input.handleWorldInput();
        world.update(gameDelta);
    }

    // ===================== DRAW =====================
    @Override
    protected void draw() {
        batch.begin();

        drawBackground("bg_lawn");
        lawnSystem.draw(batch);

        for (LawnMower m : mowers) if (m.isAlive()) m.draw(batch);
        for (Plant p : plants) if (p.isAlive()) p.draw(batch);
        for (Zombie z : zombies) if (z.isAlive()) z.draw(batch);
        for (Projectile p : projectiles) if (p.isAlive()) p.draw(batch);
        for (Sun s : suns) if (s.isAlive()) s.draw(batch);

        Vector3 t = input.getCurrentTouch();
        hud.drawPlacementPreview(t.x, t.y);
        hud.draw();

        if (paused) {
            pauseMenu.draw(batch, camera);
        }

        batch.end();
    }

    // ham cho WaveSystem goi de them zombie
    public void spawnZombie(Zombie z) { if (z != null) zombies.add(z); }

    // ===================== PlantContext =====================
    @Override
    public Array<Zombie> zombiesInCell(int row, int col) {
        Array<Zombie> out = new Array<>();
        for (Zombie z : zombies) {
            if (z.isAlive() && z.getRow() == row && grid.pixelXToCol(z.getX()) == col)
                out.add(z);
        }
        return out;
    }

    @Override
    public Array<Zombie> zombiesInArea(int row, int col, int radius) {
        Array<Zombie> out = new Array<>();
        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            int zc = grid.pixelXToCol(z.getX());
            if (Math.abs(z.getRow() - row) <= radius && Math.abs(zc - col) <= radius)
                out.add(z);
        }
        return out;
    }

    @Override
    public void damageArea(int row, int col, int radius, float damage) {
        for (Zombie z : zombiesInArea(row, col, radius)) z.takeDamage(damage);
    }

    @Override
    public void removePlant(Plant plant) {
        int r = plant.getRow(), c = plant.getCol();
        if (grid.isValidCell(r, c) && plantGrid[r][c] == plant) plantGrid[r][c] = null;
        plant.kill();
        AudioManager.get().playGameSound(AudioManager.SHOVEL);
    }

    // ===================== GameContext =====================
    @Override public int getLevel() { return level; }
    @Override public LevelData getLevelData() { return levelData; }
    @Override public GameClock getClock() { return clock; }
    @Override public GridSystem getGrid() { return grid; }
    @Override public LawnSystem getLawnSystem() { return lawnSystem; }
    @Override public WaveSystem getWaveSystem() { return waveSystem; }
    @Override public PlantFactory getPlantFactory() { return plantFactory; }
    @Override public ProjectileFactory getProjectileFactory() { return projectileFactory; }
    @Override public Plant[][] getPlantGrid() { return plantGrid; }
    @Override public Array<Plant> getPlants() { return plants; }
    @Override public Array<Zombie> getZombies() { return zombies; }
    @Override public Array<Projectile> getProjectiles() { return projectiles; }
    @Override public Array<Sun> getSuns() { return suns; }
    @Override public Array<LawnMower> getMowers() { return mowers; }
    @Override public Array<String> getSeedCards() { return seedCards; }
    @Override public int getSun() { return sun; }
    @Override public void addSun(int amount) { sun += amount; }
    @Override public void spendSun(int amount) { sun -= amount; }
    @Override public String getSelectedPlant() { return selectedPlant; }
    @Override public void setSelectedPlant(String id) { selectedPlant = id; }
    @Override public boolean isShovelSelected() { return shovelSelected; }
    @Override public void setShovelSelected(boolean on) { shovelSelected = on; }
    @Override public ObjectMap<String, Float> getCardCooldown() { return cardCooldown; }
    @Override public SpriteBatch getBatch() { return batch; }
    @Override public BitmapFont getFont() { return font; }
    @Override public OrthographicCamera getCamera() { return camera; }
    @Override public float getBattleTime() { return world.getBattleTime(); }

    @Override
    public void triggerWin() {
        if (ended) return;
        ended = true;
        markSwitched();
        AudioManager.get().playGameSound(AudioManager.WIN);
        ScreenManager.get().setScreen(new WinScreen(level));
    }

    @Override
    public void triggerLose() {
        if (ended) return;
        ended = true;
        markSwitched();
        AudioManager.get().playGameSound(AudioManager.LOSE);
        ScreenManager.get().setScreen(new LoseScreen(level));
    }

    @Override
    public void triggerPause() {
        paused = true;
        pauseMenu.reset();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (font != null) font.dispose();
    }
}
