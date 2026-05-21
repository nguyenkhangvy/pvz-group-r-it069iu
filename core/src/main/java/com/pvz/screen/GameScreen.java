package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pvz.core.GameClock;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.data.PlantData;
import com.pvz.entity.LawnMower;
import com.pvz.entity.Sun;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.plant.PlantContext;
import com.pvz.entity.projectile.Projectile;
import com.pvz.entity.zombie.Zombie;
import com.pvz.entity.zombie.PoleVaultZombie;
import com.pvz.factory.PlantFactory;
import com.pvz.factory.ProjectileFactory;
import com.pvz.factory.ZombieFactory;
import com.pvz.manager.DataManager;
import com.pvz.manager.ScreenManager;
import com.pvz.system.GridSystem;
import com.pvz.util.DebugDraw;
import com.pvz.system.WaveSystem;

/**
 * GameScreen: man choi chinh.
 *
 * DAY LA NOI VAN KHOA THOI GIAN HOAT DONG:
 *  - clock.tick(rawDelta)  -> gameDelta (world: zombie/sun/cooldown/progress)
 *  - clock.realDelta(...)  -> realDelta (UI/menu khi pause)
 *  - Pause = clock.pause() -> gameDelta = 0, world dung, UI van song.
 *  - Speed 2x = clock.setSpeed2x(true) -> gameDelta x2 (progress cung nhanh gap doi).
 *
 * MOI he thong nhan delta qua tham so. KHONG cho goi Gdx.graphics.getDeltaTime()
 * trong logic world.
 *
 * Phien ban khung: dat cay bang chuot (cay dau tien trong danh sach chon),
 * wave system spawn zombie, va cham co ban, lawnmower, win/lose.
 * Hanh vi entity dac biet se duoc dap them sau.
 */
public class GameScreen extends BaseScreen implements PlantContext {

    private final int level;
    private final Array<String> chosenPlants;
    private final LevelData levelData;

    private final GameClock clock = new GameClock();
    private final GridSystem grid = new GridSystem();
    private final PlantFactory plantFactory;
    private final ZombieFactory zombieFactory;
    private final ProjectileFactory projectileFactory;
    private final WaveSystem waveSystem;

    private final Plant[][] plantGrid = new Plant[GameConfig.GRID_ROWS][GameConfig.GRID_COLS];
    private final Array<Plant> plants = new Array<>();
    private final Array<Zombie> zombies = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();
    private final Array<Sun> suns = new Array<>();
    private final Array<LawnMower> mowers = new Array<>();

    private int sun = GameConfig.START_SUN_DEFAULT;
    private boolean paused = false;
    private boolean ended = false;

    // the bai cay tren dinh man hinh + cay dang chon de dat
    private final Array<String> seedCards = new Array<>();
    private String selectedPlant = null;          // cay dang "cam" de dat
    private final com.badlogic.gdx.utils.ObjectMap<String, Float> cardCooldown = new com.badlogic.gdx.utils.ObjectMap<>();

    private final Vector3 touch = new Vector3();
    private com.badlogic.gdx.graphics.g2d.BitmapFont font;
    private PauseMenu pauseMenu;

    // ===== BO CUC HUD (dai tren cung, trai -> phai) =====
    // [SUN] [CARD CARD CARD ...] [SHOVEL] ............... [SETTING o goc phai]
    private static final float TOP_Y = GameConfig.WORLD_HEIGHT - 90f; // day cua dai HUD
    private static final float HUD_H = 70f;

    // 1) o SUN (goc trai cung)
    private static final float SUN_X = 20f;
    private static final float SUN_W = 100f;

    // 2) bang CARD (ke ben SUN)
    private static final float CARD_X0 = SUN_X + SUN_W + 16f;
    private static final float CARD_Y = TOP_Y;
    private static final float CARD_W = 80f;
    private static final float CARD_H = HUD_H;
    private static final float CARD_GAP = 8f;

    // 3) o SHOVEL (ke sau cac card - x tinh dong theo so card)
    private static final float SHOVEL_W = 70f;

    // 4) nut SETTING (gim o goc phai tren cung)
    private static final float SETTING_W = 110f;
    private static final float SETTING_X = GameConfig.WORLD_WIDTH - SETTING_W - 20f;

    // 5) nut SPEED (1x/2x) - ngay ben trai SETTING. Chi dung tu level 4.
    private static final float SPEED_W = 70f;
    private static final float SPEED_X = SETTING_X - SPEED_W - 12f;

    // shovel: dang cam de go cay (chi dung khi da unlock o level 3)
    private boolean shovelSelected = false;

    // mau placeholder
    private static final Color GRID_COLOR = new Color(0.3f, 0.3f, 0.3f, 1f);
    private static final Color BAR_BG = new Color(0.3f, 0.3f, 0.3f, 1f);
    private static final Color BAR_FG = new Color(0.4f, 0.9f, 0.2f, 1f);
    private static final Color CARD_BG = new Color(0.85f, 0.78f, 0.55f, 1f);
    private static final Color CARD_SEL = new Color(1f, 1f, 0.3f, 1f);
    private static final Color CARD_DISABLED = new Color(0.4f, 0.4f, 0.4f, 1f);
    private static final Color SUN_HUD = new Color(1f, 0.85f, 0.1f, 1f);
    private static final Color SUN_BG = new Color(0.2f, 0.18f, 0.10f, 1f);
    private static final Color SHOVEL_BG = new Color(0.55f, 0.40f, 0.25f, 1f);
    private static final Color SHOVEL_SEL = new Color(1f, 0.7f, 0.3f, 1f);
    private static final Color SHOVEL_LOCKED = new Color(0.3f, 0.3f, 0.3f, 1f);
    private static final Color SETTING_BG = new Color(0.30f, 0.45f, 0.55f, 1f);
    private static final Color SPEED_BG = new Color(0.35f, 0.40f, 0.30f, 1f);
    private static final Color SPEED_ON = new Color(0.3f, 0.85f, 0.85f, 1f);
    private static final Color SPEED_LOCKED = new Color(0.3f, 0.3f, 0.3f, 1f);

    public GameScreen(int level, Array<String> chosenPlants) {
        this.level = level;
        this.chosenPlants = chosenPlants;
        this.plantFactory = new PlantFactory(grid);
        this.zombieFactory = new ZombieFactory(grid);
        this.projectileFactory = new ProjectileFactory();

        this.levelData = DataManager.get().level(level);
        this.waveSystem = new WaveSystem(levelData, zombieFactory);

        clock.resetForNewLevel();
        spawnMowers();

        // Lap the bai tu danh sach cay da chon. Neu rong (nguoi choi chua chon),
        // tu dong dung tat ca cay da unlock cua level de van choi duoc.
        if (chosenPlants != null && chosenPlants.size > 0) {
            seedCards.addAll(chosenPlants);
        } else {
            seedCards.addAll(com.pvz.system.PlantUnlockSystem.getUnlockedPlants(level));
        }
        // KHONG tu dong chon cay. Nguoi choi phai bam the bai truoc moi cam duoc cay.
        selectedPlant = null;

        font = new com.badlogic.gdx.graphics.g2d.BitmapFont();
        pauseMenu = new PauseMenu(font);
    }

    private void spawnMowers() {
        float w = 60f, h = 70f;
        for (int r = 0; r < GameConfig.GRID_ROWS; r++) {
            float x = grid.houseX() - 30f;
            float y = grid.rowToPixelY(r);
            mowers.add(new LawnMower(r, x, y, w, h));
        }
    }

    // ===================== UPDATE (van khoa thoi gian) =====================
    @Override
    protected void update(float rawDelta) {
        handleGlobalInput();

        // realDelta cho UI (luon chay, ke ca khi pause)
        float realDelta = clock.realDelta(rawDelta);
        // gameDelta cho world (= 0 khi pause, x2 khi speed)
        if (paused) clock.pause(); else clock.resume();
        float gameDelta = clock.tick(rawDelta);

        // --- UI/menu cap nhat bang realDelta (van song khi pause) ---
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
                default:
                    break;
            }
            return; // world dung han khi pause
        }

        if (ended) return;

        // --- WORLD cap nhat bang gameDelta ---
        handleWorldInput();
        updateCardCooldown(gameDelta);
        updateSkyFall(gameDelta);
        waveSystem.update(gameDelta, this);
        updatePlants(gameDelta);
        updateZombies(gameDelta);
        updateProjectiles(gameDelta);
        updateSuns(gameDelta);
        updateMowers(gameDelta);
        checkCollisions();
        checkWinLose();
    }

    private void updateCardCooldown(float d) {
        for (String key : cardCooldown.keys().toArray()) {
            float v = cardCooldown.get(key, 0f) - d;
            cardCooldown.put(key, Math.max(0f, v));
        }
    }

    // sun roi tu troi theo interval (doc tu level JSON)
    private float skyFallTimer = 0f;
    private void updateSkyFall(float d) {
        float interval = (levelData != null && levelData.sunFallInterval > 0) ? levelData.sunFallInterval : 10f;
        int amount = (levelData != null && levelData.sunFallAmount > 0) ? levelData.sunFallAmount : 25;
        skyFallTimer += d;
        if (skyFallTimer >= interval) {
            skyFallTimer = 0f;
            float x = GameConfig.LAWN_X + (float) Math.random()
                * (GameConfig.GRID_COLS * GameConfig.CELL_WIDTH);
            float targetY = GameConfig.LAWN_Y + (float) Math.random()
                * (GameConfig.GRID_ROWS * GameConfig.CELL_HEIGHT * 0.6f);
            suns.add(new Sun(amount, x, GameConfig.WORLD_HEIGHT - 60f, targetY, 36f));
        }
    }

    private void handleGlobalInput() {
        // P = pause/resume (mo menu giua game)
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) pauseMenu.reset();
        }
        // S = speed 2x (chi khi da unlock o level 4)
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) && level >= GameConfig.SPEED_UNLOCK_LEVEL) {
            clock.setSpeed2x(!clock.isSpeed2x());
        }
    }

    private void handleWorldInput() {
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);

            // 0a) bam nut SETTING (goc phai tren) -> mo menu pause
            if (hitRect(touch.x, touch.y, SETTING_X, TOP_Y, SETTING_W, HUD_H)) {
                paused = true;
                pauseMenu.reset();
                selectedPlant = null;
                shovelSelected = false;
                return;
            }

            // 0a2) bam nut SPEED (1x/2x) - chi khi da unlock o level 4
            if (level >= GameConfig.SPEED_UNLOCK_LEVEL
                    && hitRect(touch.x, touch.y, SPEED_X, TOP_Y, SPEED_W, HUD_H)) {
                clock.setSpeed2x(!clock.isSpeed2x());
                return;
            }

            // 0b) bam nut SHOVEL (chi khi da unlock o level 3) -> cam/nha shovel
            if (level >= GameConfig.SHOVEL_UNLOCK_LEVEL
                    && hitRect(touch.x, touch.y, shovelX(), TOP_Y, SHOVEL_W, HUD_H)) {
                shovelSelected = !shovelSelected;
                selectedPlant = null; // cam shovel thi nha cay
                return;
            }

            // 0c) neu dang cam SHOVEL va bam vao o co cay -> go cay do
            if (shovelSelected) {
                int rcol = grid.pixelXToCol(touch.x);
                int rrow = grid.pixelYToRow(touch.y);
                if (grid.isValidCell(rrow, rcol) && plantGrid[rrow][rcol] != null) {
                    removePlant(plantGrid[rrow][rcol]);
                }
                shovelSelected = false; // dung 1 lan roi nha
                return;
            }

            // 1) bam vao THE BAI tren dinh -> chon/bo chon cay de cam
            int cardIdx = cardIndexAt(touch.x, touch.y);
            if (cardIdx >= 0) {
                String cardId = seedCards.get(cardIdx);
                // bam lai dung the dang cam -> nha cay ra
                if (cardId.equals(selectedPlant)) {
                    selectedPlant = null;
                    return;
                }
                // chi cam duoc khi the san sang (het cooldown va du sun)
                PlantData pd = DataManager.get().plant(cardId);
                int cost = (pd != null) ? pd.cost : 0;
                boolean ready = cardCooldown.get(cardId, 0f) <= 0f && sun >= cost;
                selectedPlant = ready ? cardId : null;
                return;
            }

            // 2) thu nhat sun
            for (Sun s : suns) {
                if (s.isAlive() && s.contains(touch.x, touch.y)) {
                    sun += s.getValue();
                    s.kill();
                    return;
                }
            }

            // 3) dat cay dang chon vao o
            int col = grid.pixelXToCol(touch.x);
            int row = grid.pixelYToRow(touch.y);
            if (grid.isValidCell(row, col) && plantGrid[row][col] == null && selectedPlant != null) {
                // kiem tra cooldown the
                float cd = cardCooldown.get(selectedPlant, 0f);
                if (cd > 0f) { selectedPlant = null; return; } // dang hoi chieu -> nha cay
                Plant p = plantFactory.create(selectedPlant, row, col);
                if (p != null && sun >= p.getData().cost) {
                    sun -= p.getData().cost;
                    plantGrid[row][col] = p;
                    plants.add(p);
                    cardCooldown.put(selectedPlant, p.getData().cooldown); // bat dau hoi chieu
                }
                // dat xong (hoac that bai) -> nha cay, muon dat tiep phai bam the lai
                selectedPlant = null;
            } else if (grid.isValidCell(row, col) && plantGrid[row][col] != null) {
                // bam vao o da co cay -> nha cay dang cam (giong PvZ that)
                selectedPlant = null;
            }
        }
    }

    /** Tra ve index the bai tai diem (px,py), hoac -1 neu khong trung the nao. */
    private int cardIndexAt(float px, float py) {
        for (int i = 0; i < seedCards.size; i++) {
            float cx = CARD_X0 + i * (CARD_W + CARD_GAP);
            if (px >= cx && px <= cx + CARD_W && py >= CARD_Y && py <= CARD_Y + CARD_H) {
                return i;
            }
        }
        return -1;
    }

    /** Kiem tra diem (px,py) co nam trong hcn (x,y,w,h) khong. */
    private boolean hitRect(float px, float py, float x, float y, float w, float h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }

    /** Vi tri x cua o shovel = ngay sau the bai cuoi cung. */
    private float shovelX() {
        return CARD_X0 + seedCards.size * (CARD_W + CARD_GAP) + 12f;
    }

    private void updatePlants(float d) {
        for (Plant p : plants) {
            if (!p.isAlive()) continue;
            // hanh vi dac biet (cherry no, potato arm/no, chomper an) qua context
            p.updateWithContext(d, this);
            if (!p.isAlive()) continue; // co the da chet/bien mat sau update (cherry/potato)

            // ban dan: chi ban khi la cay ban va co zombie cung hang phia phai
            if (p.isShooter() && zombieInRow(p.getRow()) && p.canAttack()) {
                int shots = Math.max(1, p.getData().projectilePerShot);
                for (int s = 0; s < shots; s++) {
                    // Repeater: 2 pea cach nhau mot chut theo x de khong chong khit
                    Projectile proj = projectileFactory.create(
                        p.getData().projectileType, p.getRow(),
                        p.getX() + p.getWidth() / 2f + s * 18f, p.getY());
                    if (proj != null) projectiles.add(proj);
                }
            }
            // san xuat sun
            if (p.canProduceSun()) {
                suns.add(new Sun(p.getData().sunAmount, p.getX(), p.getY() + 30, p.getY(), 36f));
            }
        }
    }

    private boolean zombieInRow(int row) {
        // chi tinh zombie con o ben phai cay gan nhat tren hang (de khong ban khi zombie da qua)
        for (Zombie z : zombies) if (z.isAlive() && z.getRow() == row) return true;
        return false;
    }

    private void updateZombies(float d) {
        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            int col = grid.pixelXToCol(z.getX());
            Plant blocking = grid.isValidCell(z.getRow(), col) ? plantGrid[z.getRow()][col] : null;
            boolean realBlock = blocking != null && blocking.isAlive() && blocking.isEatable();

            // --- Pole Vault: gap cay dau tien (con block) thi NHAY qua ---
            if (z instanceof PoleVaultZombie) {
                PoleVaultZombie pv = (PoleVaultZombie) z;
                if (pv.isVaulting()) { pv.update(d); continue; }
                if (realBlock && pv.canVault()) {
                    // nhay qua: ha canh o ben TRAI cua cay (col - 1)
                    float landingX = grid.colToPixelX(Math.max(0, col - 1));
                    pv.startVault(landingX);
                    continue;
                }
            }

            if (realBlock) {
                z.setState(Zombie.State.EATING);
                z.update(d);
                if (z.canEatBite()) {
                    blocking.takeDamage(z.getData().damage);
                    if (!blocking.isAlive()) {
                        plantGrid[z.getRow()][col] = null;
                    }
                }
            } else {
                z.setState(Zombie.State.WALKING);
                z.update(d);
            }
        }
    }

    private void updateProjectiles(float d) {
        for (Projectile p : projectiles) {
            if (p.isAlive()) p.update(d);
            if (p.getX() > GameConfig.WORLD_WIDTH) p.kill();
        }
    }

    private void updateSuns(float d) {
        for (Sun s : suns) if (s.isAlive()) s.update(d);
    }

    private void updateMowers(float d) {
        for (LawnMower m : mowers) {
            if (!m.isAlive()) continue;
            m.update(d);
            if (m.isRunning()) {
                // giet moi zombie cung hang khi di qua
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getRow() == m.getRow() && z.getX() <= m.getX()) {
                        z.kill();
                    }
                }
                if (m.getX() > GameConfig.WORLD_WIDTH) m.markUsedOffscreen();
            }
        }
    }

    private void checkCollisions() {
        // pea trung zombie dau tien cung hang
        for (Projectile p : projectiles) {
            if (!p.isAlive()) continue;
            Zombie target = null;
            float bestX = Float.MAX_VALUE;
            for (Zombie z : zombies) {
                if (z.isAlive() && z.getRow() == p.getRow()
                    && Math.abs(z.getX() - p.getX()) < 30f && z.getX() < bestX) {
                    target = z; bestX = z.getX();
                }
            }
            if (target != null) {
                target.takeDamage(p.getData().damage);
                if (p.getData().slows) target.applySlow(p.getData().slowFactor, p.getData().slowDuration);
                p.kill();
            }
        }

        // zombie cham nha -> kich hoat mower hoac thua
        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            if (z.getX() <= grid.houseX()) {
                LawnMower m = mowerAt(z.getRow());
                if (m != null && m.isReady()) {
                    m.trigger();           // kich hoat mower phong thu
                } else if (m != null && m.isRunning()) {
                    // mower dang chay se giet zombie nay ngay sau day -> chua thua
                } else {
                    lose();                // khong con mower (da dung) -> thua ngay
                    return;
                }
            }
        }
        cleanupDead();
    }

    private LawnMower mowerAt(int row) {
        for (LawnMower m : mowers) if (m.getRow() == row && m.isAlive()) return m;
        return null;
    }

    private void cleanupDead() {
        for (int i = zombies.size - 1; i >= 0; i--) if (!zombies.get(i).isAlive()) zombies.removeIndex(i);
        for (int i = projectiles.size - 1; i >= 0; i--) if (!projectiles.get(i).isAlive()) projectiles.removeIndex(i);
        for (int i = suns.size - 1; i >= 0; i--) if (!suns.get(i).isAlive()) suns.removeIndex(i);
        for (int i = plants.size - 1; i >= 0; i--) if (!plants.get(i).isAlive()) plants.removeIndex(i);
    }

    private void checkWinLose() {
        if (ended) return;
        // WIN: tat ca wave da spawn xong VA khong con zombie song
        if (waveSystem.isFinished() && zombies.size == 0) {
            win();
        }
    }

    private void win() {
        ended = true;
        markSwitched();
        ScreenManager.get().setScreen(new WinScreen(level));
    }

    private void lose() {
        ended = true;
        markSwitched();
        ScreenManager.get().setScreen(new LoseScreen(level));
    }

    // ham cho WaveSystem goi de them zombie
    public void spawnZombie(Zombie z) { if (z != null) zombies.add(z); }

    // ===================== PlantContext (cay tac dong len the gioi) =====================
    @Override
    public Array<Zombie> zombiesInCell(int row, int col) {
        Array<Zombie> out = new Array<>();
        for (Zombie z : zombies) {
            if (z.isAlive() && z.getRow() == row && grid.pixelXToCol(z.getX()) == col) {
                out.add(z);
            }
        }
        return out;
    }

    @Override
    public Array<Zombie> zombiesInArea(int row, int col, int radius) {
        Array<Zombie> out = new Array<>();
        for (Zombie z : zombies) {
            if (!z.isAlive()) continue;
            int zc = grid.pixelXToCol(z.getX());
            if (Math.abs(z.getRow() - row) <= radius && Math.abs(zc - col) <= radius) {
                out.add(z);
            }
        }
        return out;
    }

    @Override
    public void damageArea(int row, int col, int radius, float damage) {
        for (Zombie z : zombiesInArea(row, col, radius)) {
            z.takeDamage(damage);
        }
    }

    @Override
    public void removePlant(Plant plant) {
        int r = plant.getRow(), c = plant.getCol();
        if (grid.isValidCell(r, c) && plantGrid[r][c] == plant) {
            plantGrid[r][c] = null;
        }
        plant.kill();
    }

    // ===================== DRAW =====================
    @Override
    protected void draw() {
        DebugDraw dd = DebugDraw.get();
        batch.begin();

        // --- luoi ---
        for (int r = 0; r <= GameConfig.GRID_ROWS; r++) {
            float y = GameConfig.LAWN_Y + r * GameConfig.CELL_HEIGHT;
            dd.hLine(batch, GameConfig.LAWN_X, y,
                GameConfig.GRID_COLS * GameConfig.CELL_WIDTH, 2f, GRID_COLOR);
        }
        for (int c = 0; c <= GameConfig.GRID_COLS; c++) {
            float x = GameConfig.LAWN_X + c * GameConfig.CELL_WIDTH;
            dd.vLine(batch, x, GameConfig.LAWN_Y,
                GameConfig.GRID_ROWS * GameConfig.CELL_HEIGHT, 2f, GRID_COLOR);
        }

        // --- entity ---
        for (LawnMower m : mowers) if (m.isAlive()) m.drawDebug(batch);
        for (Plant p : plants) if (p.isAlive()) p.drawDebug(batch);
        for (Zombie z : zombies) if (z.isAlive()) z.drawDebug(batch);
        for (Projectile p : projectiles) if (p.isAlive()) p.drawDebug(batch);
        for (Sun s : suns) if (s.isAlive()) s.drawDebug(batch);

        // --- preview o dat cay khi dang cam cay ---
        drawPlacementPreview(dd);

        // --- thanh progress (goc phai DUOI) ---
        drawProgressBar(dd);

        // --- HUD tren cung: SUN -> CARD -> SHOVEL -> SETTING ---
        drawSunBox(dd);
        drawSeedCards(dd);
        drawShovel(dd);
        drawSpeedButton(dd);
        drawSettingButton(dd);
        drawHud();

        // --- overlay menu setting khi pause (ve cuoi cung de nam tren) ---
        if (paused) {
            pauseMenu.draw(batch, camera);
        }

        batch.end();
    }

    /** O hien SUN o goc trai cung tren cung. */
    private void drawSunBox(DebugDraw dd) {
        dd.rect(batch, SUN_X, TOP_Y, SUN_W, HUD_H, SUN_BG);
        if (font != null) {
            font.setColor(1f, 0.5f, 0.1f, 1f);
            font.draw(batch, "SUN", SUN_X + 10, TOP_Y + HUD_H - 12);
            font.setColor(1f, 0.9f, 0.2f, 1f);
            font.draw(batch, String.valueOf(sun), SUN_X + 10, TOP_Y + 26);
        }
    }

    /** O SHOVEL (ke sau card). Khoa neu chua unlock (level < 3). */
    private void drawShovel(DebugDraw dd) {
        float sx = shovelX();
        boolean unlocked = level >= GameConfig.SHOVEL_UNLOCK_LEVEL;
        Color bg = !unlocked ? SHOVEL_LOCKED : (shovelSelected ? SHOVEL_SEL : SHOVEL_BG);
        dd.rect(batch, sx, TOP_Y, SHOVEL_W, HUD_H, bg);
        if (font != null) {
            font.setColor(unlocked ? 1f : 0.6f, unlocked ? 1f : 0.6f, unlocked ? 1f : 0.6f, 1f);
            font.draw(batch, unlocked ? "Shovel" : "Lock", sx + 8, TOP_Y + HUD_H / 2f + 6f);
        }
    }

    /** Nut SPEED (1x/2x). Khoa neu chua unlock (level < 4). */
    private void drawSpeedButton(DebugDraw dd) {
        boolean unlocked = level >= GameConfig.SPEED_UNLOCK_LEVEL;
        boolean on = clock.isSpeed2x();
        Color bg = !unlocked ? SPEED_LOCKED : (on ? SPEED_ON : SPEED_BG);
        dd.rect(batch, SPEED_X, TOP_Y, SPEED_W, HUD_H, bg);
        if (font != null) {
            if (!unlocked) {
                font.setColor(0.6f, 0.6f, 0.6f, 1f);
                font.draw(batch, "Lock", SPEED_X + 14, TOP_Y + HUD_H / 2f + 6f);
            } else {
                font.setColor(0f, 0f, 0f, 1f);
                font.draw(batch, on ? "2x" : "1x", SPEED_X + 24, TOP_Y + HUD_H / 2f + 6f);
            }
        }
    }

    /** Nut SETTING gim o goc phai tren cung. */
    private void drawSettingButton(DebugDraw dd) {
        dd.rect(batch, SETTING_X, TOP_Y, SETTING_W, HUD_H, SETTING_BG);
        if (font != null) {
            font.setColor(1f, 1f, 1f, 1f);
            font.draw(batch, "Setting", SETTING_X + 18, TOP_Y + HUD_H / 2f + 6f);
        }
    }

    private void drawSeedCards(DebugDraw dd) {
        for (int i = 0; i < seedCards.size; i++) {
            String id = seedCards.get(i);
            float cx = CARD_X0 + i * (CARD_W + CARD_GAP);
            PlantData pd = DataManager.get().plant(id);
            int cost = (pd != null) ? pd.cost : 0;
            float cd = cardCooldown.get(id, 0f);

            boolean affordable = sun >= cost && cd <= 0f;
            Color bg = id.equals(selectedPlant) ? CARD_SEL : (affordable ? CARD_BG : CARD_DISABLED);
            dd.rect(batch, cx, CARD_Y, CARD_W, CARD_H, bg);

            if (font != null) {
                font.setColor(0f, 0f, 0f, 1f);
                font.draw(batch, shortName(id), cx + 4, CARD_Y + CARD_H - 8);
                font.draw(batch, "$" + cost, cx + 4, CARD_Y + 20);
                if (cd > 0f) font.draw(batch, String.format("%.0f", cd), cx + CARD_W - 24, CARD_Y + 20);
            }
        }
    }

    private void drawHud() {
        if (font == null) return;
        // tip nho o duoi dai HUD
        font.setColor(0.85f, 0.85f, 0.85f, 1f);
        String tip = "Bam the chon cay | Click o de dat | P/Setting=pause"
            + (level >= GameConfig.SPEED_UNLOCK_LEVEL ? " | Speed=1x/2x" : "");
        font.draw(batch, tip, SUN_X, TOP_Y - 10);
    }

    private String shortName(String id) {
        return id.length() > 9 ? id.substring(0, 9) : id;
    }

    private void drawPlacementPreview(DebugDraw dd) {
        if (selectedPlant == null) return;
        // lay vi tri chuot hien tai -> o luoi
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touch);
        int col = grid.pixelXToCol(touch.x);
        int row = grid.pixelYToRow(touch.y);
        if (!grid.isValidCell(row, col)) return;

        float cx = GameConfig.LAWN_X + col * GameConfig.CELL_WIDTH;
        float cy = GameConfig.LAWN_Y + row * GameConfig.CELL_HEIGHT;
        boolean canPlace = plantGrid[row][col] == null;
        Color hl = canPlace ? new Color(0.4f, 1f, 0.4f, 0.35f)
                            : new Color(1f, 0.3f, 0.3f, 0.35f);
        dd.rect(batch, cx, cy, GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT, hl);
    }

    private void drawProgressBar(DebugDraw dd) {
        float total = (levelData != null && levelData.progressDuration > 0) ? levelData.progressDuration : 60f;
        float ratio = Math.min(1f, clock.getWorldTime() / total);
        float barW = 360f, barH = 18f;
        // goc PHAI DUOI cung
        float bx = GameConfig.WORLD_WIDTH - barW - 30f;
        float by = 24f;
        dd.rect(batch, bx, by, barW, barH, BAR_BG);
        dd.rect(batch, bx, by, barW * ratio, barH, BAR_FG);
        if (font != null) {
            font.setColor(1f, 1f, 1f, 1f);
            font.draw(batch, "Progress", bx, by + barH + 16f);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (font != null) font.dispose();
    }
}
