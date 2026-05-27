package com.pvz.screen;

import com.badlogic.gdx.utils.Array;
import com.pvz.core.GameConfig;
import com.pvz.entity.LawnMower;
import com.pvz.entity.Sun;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.plant.ShooterPlant;
import com.pvz.entity.plant.SunflowerPlant;
import com.pvz.entity.projectile.Projectile;
import com.pvz.entity.zombie.PoleVaultZombie;
import com.pvz.entity.zombie.Zombie;
import com.pvz.manager.AudioManager;
import com.pvz.system.GridSystem;

/**
 * GameWorld: cap nhat logic the gioi game moi frame.
 *   - Update plants (ban, san sun)
 *   - Update zombies (di, an cay, pole vault)
 *   - Update projectiles, suns, mowers
 *   - Va cham (pea-zombie, zombie-nha)
 *   - Win/Lose detection
 *   - Sun roi tu troi
 *   - Card cooldown
 *
 * Tach tu GameScreen de giam kich thuoc file. Nhan du lieu qua GameContext.
 */
public final class GameWorld {

    private final GameContext ctx;
    private float skyFallTimer = 0f;
    private float battleTime = 0f;

    public GameWorld(GameContext ctx) {
        this.ctx = ctx;
    }

    public float getBattleTime() { return battleTime; }

    /** Cap nhat toan bo world. Goi moi frame voi gameDelta (da scale/pause). */
    public void update(float gameDelta) {
        ctx.getLawnSystem().update(gameDelta);
        updateCardCooldown(gameDelta);

        if (ctx.getLawnSystem().isReady()) {
            battleTime += gameDelta;
            updateSkyFall(gameDelta);
            ctx.getWaveSystem().update(gameDelta, (GameScreen) ctx);
        }

        updatePlants(gameDelta);
        updateZombies(gameDelta);
        updateProjectiles(gameDelta);
        updateSuns(gameDelta);
        updateMowers(gameDelta);
        checkCollisions();
        checkWinLose();
    }

    // ---------- Card cooldown ----------
    private void updateCardCooldown(float d) {
        for (String key : ctx.getCardCooldown().keys().toArray()) {
            float v = ctx.getCardCooldown().get(key, 0f) - d;
            ctx.getCardCooldown().put(key, Math.max(0f, v));
        }
    }

    // ---------- Sun from sky ----------
    private void updateSkyFall(float d) {
        float interval = (ctx.getLevelData() != null && ctx.getLevelData().sunFallInterval > 0)
            ? ctx.getLevelData().sunFallInterval : 10f;
        int amount = (ctx.getLevelData() != null && ctx.getLevelData().sunFallAmount > 0)
            ? ctx.getLevelData().sunFallAmount : 25;
        skyFallTimer += d;
        if (skyFallTimer >= interval) {
            skyFallTimer = 0f;
            float x = GameConfig.LAWN_X + (float) Math.random()
                * (GameConfig.GRID_COLS * GameConfig.CELL_WIDTH);
            float targetY = GameConfig.LAWN_Y + (float) Math.random()
                * (GameConfig.GRID_ROWS * GameConfig.CELL_HEIGHT * 0.6f);
            ctx.getSuns().add(new Sun(amount, x, GameConfig.WORLD_HEIGHT - 60f, targetY, 36f));
        }
    }

    // ---------- Plants ----------
    private void updatePlants(float d) {
        Array<Plant> plants = ctx.getPlants();
        for (Plant p : plants) {
            if (!p.isAlive()) continue;
            p.updateWithContext(d, (GameScreen) ctx);
            if (!p.isAlive()) continue;

            if (p instanceof ShooterPlant) {
                ShooterPlant shooter = (ShooterPlant) p;
                if (zombieInRow(p.getRow()) && shooter.canAttack()) {
                    p.playShootAnim();
                    AudioManager.get().playGameSound(AudioManager.SHOOT, 0.6f);
                    int shots = shooter.getProjectilePerShot();
                    for (int s = 0; s < shots; s++) {
                        Projectile proj = ctx.getProjectileFactory().create(
                            shooter.getProjectileType(), p.getRow(),
                            p.getX() + p.getWidth() / 2f + s * 18f, p.getY());
                        if (proj != null) ctx.getProjectiles().add(proj);
                    }
                }
            }

            if (p instanceof SunflowerPlant) {
                SunflowerPlant sf = (SunflowerPlant) p;
                if (sf.canProduceSun()) {
                    p.playSpecialAnim();
                    ctx.getSuns().add(new Sun(sf.getSunAmount(), p.getX(), p.getY() + 30, p.getY(), 36f));
                }
            }
        }
    }

    private boolean zombieInRow(int row) {
        for (Zombie z : ctx.getZombies()) if (z.isAlive() && !z.isDying() && z.getRow() == row) return true;
        return false;
    }

    // ---------- Zombies ----------
    private void updateZombies(float d) {
        GridSystem grid = ctx.getGrid();
        Plant[][] plantGrid = ctx.getPlantGrid();
        for (Zombie z : ctx.getZombies()) {
            if (!z.isAlive()) continue;
            // zombie dying: chi chay update (dem dying timer), khong an/di
            if (z.isDying()) {
                z.update(d);
                continue;
            }
            int col = grid.pixelXToCol(z.getX());
            Plant blocking = grid.isValidCell(z.getRow(), col) ? plantGrid[z.getRow()][col] : null;
            boolean realBlock = blocking != null && blocking.isAlive() && blocking.isEatable();

            if (z instanceof PoleVaultZombie) {
                PoleVaultZombie pv = (PoleVaultZombie) z;
                if (pv.isVaulting()) { pv.update(d); continue; }
                if (realBlock && pv.canVault()) {
                    float landingX = grid.colToPixelX(Math.max(0, col - 1));
                    pv.startVault(landingX);
                    AudioManager.get().playGameSound(AudioManager.VAULT, 0.8f);
                    continue;
                }
            }

            if (realBlock) {
                z.setState(Zombie.State.EATING);
                z.update(d);
                if (z.canEatBite()) {
                    blocking.takeDamage(z.getBiteDamage());
                    AudioManager.get().playGameSound(AudioManager.ZOMBIE_EAT, 0.5f);
                    if (!blocking.isAlive()) {
                        AudioManager.get().playGameSound(AudioManager.GULP, 0.7f);
                        plantGrid[z.getRow()][col] = null;
                    }
                }
            } else {
                z.setState(Zombie.State.WALKING);
                z.update(d);
            }
        }
    }

    // ---------- Projectiles / Suns / Mowers ----------
    private void updateProjectiles(float d) {
        for (Projectile p : ctx.getProjectiles()) {
            if (p.isAlive()) p.update(d);
            if (p.getX() > GameConfig.WORLD_WIDTH) p.kill();
        }
    }

    private void updateSuns(float d) {
        for (Sun s : ctx.getSuns()) if (s.isAlive()) s.update(d);
    }

    private void updateMowers(float d) {
        for (LawnMower m : ctx.getMowers()) {
            if (!m.isAlive()) continue;
            m.update(d);
            if (m.isRunning()) {
                for (Zombie z : ctx.getZombies()) {
                    if (z.isAlive() && !z.isDying() && z.getRow() == m.getRow() && z.getX() <= m.getX()) {
                        z.kill();
                    }
                }
                if (m.getX() > GameConfig.WORLD_WIDTH) m.markUsedOffscreen();
            }
        }
    }

    // ---------- Collisions ----------
    private void checkCollisions() {
        GridSystem grid = ctx.getGrid();
        // pea-zombie
        for (Projectile p : ctx.getProjectiles()) {
            if (!p.isAlive()) continue;
            Zombie target = null;
            float bestX = Float.MAX_VALUE;
            for (Zombie z : ctx.getZombies()) {
                if (z.isAlive() && !z.isDying() && z.getRow() == p.getRow()
                    && Math.abs(z.getX() - p.getX()) < 30f && z.getX() < bestX) {
                    target = z; bestX = z.getX();
                }
            }
            if (target != null) {
                target.takeDamage(p.getData().damage);
                AudioManager.get().playGameSound(AudioManager.HIT_ZOMBIE, 0.6f);
                if (p.getData().slows()) target.applySlow(p.getData().slow.factor, p.getData().slow.duration);
                p.kill();
            }
        }

        // zombie-house
        for (Zombie z : ctx.getZombies()) {
            if (!z.isAlive() || z.isDying()) continue;
            if (z.getX() <= grid.houseX()) {
                LawnMower m = mowerAt(z.getRow());
                if (m != null && m.isReady()) {
                    m.trigger();
                    AudioManager.get().playGameSound(AudioManager.MOWER);
                } else if (m != null && m.isRunning()) {
                    // mower se giet, chua thua
                } else {
                    ctx.triggerLose();
                    return;
                }
            }
        }
        cleanupDead();
    }

    private LawnMower mowerAt(int row) {
        for (LawnMower m : ctx.getMowers()) if (m.getRow() == row && m.isAlive()) return m;
        return null;
    }

    private void cleanupDead() {
        Array<Zombie> zombies = ctx.getZombies();
        Array<Projectile> projectiles = ctx.getProjectiles();
        Array<Sun> suns = ctx.getSuns();
        Array<Plant> plants = ctx.getPlants();
        for (int i = zombies.size - 1; i >= 0; i--) if (!zombies.get(i).isAlive()) zombies.removeIndex(i);
        for (int i = projectiles.size - 1; i >= 0; i--) if (!projectiles.get(i).isAlive()) projectiles.removeIndex(i);
        for (int i = suns.size - 1; i >= 0; i--) if (!suns.get(i).isAlive()) suns.removeIndex(i);
        for (int i = plants.size - 1; i >= 0; i--) if (!plants.get(i).isAlive()) plants.removeIndex(i);
    }

    // ---------- Win / Lose ----------
    private void checkWinLose() {
        if (!ctx.getLawnSystem().isReady()) return;
        if (!ctx.getWaveSystem().isFinished()) return;
        // win khi tat ca zombie da chet hoac dang dying
        for (Zombie z : ctx.getZombies()) {
            if (z.isAlive() && !z.isDying()) return; // con zombie song va chua dying
        }
        ctx.triggerWin();
    }
}
