package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.pvz.core.GameConfig;
import com.pvz.data.PlantData;
import com.pvz.entity.Sun;
import com.pvz.entity.plant.Plant;
import com.pvz.manager.AudioManager;
import com.pvz.manager.DataManager;
import com.pvz.system.GridSystem;

/**
 * GameInput: xu ly toan bo input cua nguoi choi trong man choi.
 *   - Phim tat (P/ESC = pause, S = speed)
 *   - Touch: chon card, dat cay, nhat sun, shovel, pause/speed button
 *
 * Tach tu GameScreen de giam kich thuoc file. Nhan du lieu qua GameContext.
 */
public final class GameInput {

    private final GameContext ctx;
    private final Vector3 touch = new Vector3();

    public GameInput(GameContext ctx) {
        this.ctx = ctx;
    }

    /** Xu ly phim tat toan cuc (pause, speed). Goi moi frame. */
    public void handleGlobalInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            ctx.triggerPause();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S) && ctx.getLevel() >= GameConfig.SPEED_UNLOCK_LEVEL) {
            ctx.getClock().setSpeed2x(!ctx.getClock().isSpeed2x());
        }
    }

    /** Xu ly touch tren the gioi game. Goi moi frame khi KHONG pause. */
    public void handleWorldInput() {
        if (!Gdx.input.justTouched()) return;
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        ctx.getCamera().unproject(touch);

        // Setting button
        if (hitRect(touch.x, touch.y, GameHud.SETTING_X, GameHud.TOP_Y, GameHud.SETTING_W, GameHud.HUD_H)) {
            ctx.triggerPause();
            ctx.setSelectedPlant(null);
            ctx.setShovelSelected(false);
            return;
        }

        // Speed button
        if (ctx.getLevel() >= GameConfig.SPEED_UNLOCK_LEVEL
                && hitRect(touch.x, touch.y, GameHud.SPEED_X, GameHud.TOP_Y, GameHud.SPEED_W, GameHud.HUD_H)) {
            ctx.getClock().setSpeed2x(!ctx.getClock().isSpeed2x());
            return;
        }

        // Shovel button
        float shovelX = GameHud.SHOVEL_X;
        if (ctx.getLevel() >= GameConfig.SHOVEL_UNLOCK_LEVEL
                && hitRect(touch.x, touch.y, shovelX, GameHud.TOP_Y, GameHud.SHOVEL_W, GameHud.HUD_H)) {
            ctx.setShovelSelected(!ctx.isShovelSelected());
            ctx.setSelectedPlant(null);
            return;
        }

        // Shovel xoa cay
        if (ctx.isShovelSelected()) {
            GridSystem grid = ctx.getGrid();
            int rcol = grid.pixelXToCol(touch.x);
            int rrow = grid.pixelYToRow(touch.y);
            if (grid.isValidCell(rrow, rcol) && ctx.getPlantGrid()[rrow][rcol] != null) {
                Plant target = ctx.getPlantGrid()[rrow][rcol];
                ctx.getPlantGrid()[rrow][rcol] = null;
                target.kill();
                AudioManager.get().playGameSound(AudioManager.SHOVEL);
            }
            ctx.setShovelSelected(false);
            return;
        }

        // Card selection
        int cardIdx = cardIndexAt(touch.x, touch.y);
        if (cardIdx >= 0) {
            String cardId = ctx.getSeedCards().get(cardIdx);
            if (cardId.equals(ctx.getSelectedPlant())) {
                ctx.setSelectedPlant(null);
                return;
            }
            PlantData pd = DataManager.get().plant(cardId);
            int cost = (pd != null) ? pd.cost : 0;
            boolean ready = ctx.getCardCooldown().get(cardId, 0f) <= 0f && ctx.getSun() >= cost;
            ctx.setSelectedPlant(ready ? cardId : null);
            return;
        }

        // Pick sun
        for (Sun s : ctx.getSuns()) {
            if (s.isAlive() && s.contains(touch.x, touch.y)) {
                ctx.addSun(s.getValue());
                AudioManager.get().playGameSound(AudioManager.SUN_PICK);
                s.kill();
                return;
            }
        }

        // Place plant
        GridSystem grid = ctx.getGrid();
        int col = grid.pixelXToCol(touch.x);
        int row = grid.pixelYToRow(touch.y);
        if (grid.isValidCell(row, col) && ctx.getLawnSystem().isPlantable(row, col)
                && ctx.getPlantGrid()[row][col] == null && ctx.getSelectedPlant() != null) {
            float cd = ctx.getCardCooldown().get(ctx.getSelectedPlant(), 0f);
            if (cd > 0f) { ctx.setSelectedPlant(null); return; }
            Plant p = ctx.getPlantFactory().create(ctx.getSelectedPlant(), row, col);
            if (p != null && ctx.getSun() >= p.getData().cost) {
                ctx.spendSun(p.getData().cost);
                ctx.getPlantGrid()[row][col] = p;
                AudioManager.get().playGameSound(AudioManager.PLANT);
                ctx.getPlants().add(p);
                ctx.getCardCooldown().put(ctx.getSelectedPlant(), p.getData().cooldown);
            }
            ctx.setSelectedPlant(null);
        } else if (grid.isValidCell(row, col) && ctx.getPlantGrid()[row][col] != null) {
            ctx.setSelectedPlant(null);
        }
    }

    /** Lay toa do touch hien tai (unprojected) de GameHud ve preview. */
    public Vector3 getCurrentTouch() {
        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        ctx.getCamera().unproject(touch);
        return touch;
    }

    private int cardIndexAt(float px, float py) {
        for (int i = 0; i < ctx.getSeedCards().size; i++) {
            float cx = GameHud.CARD_X0 + i * (GameHud.CARD_W + GameHud.CARD_GAP);
            if (px >= cx && px <= cx + GameHud.CARD_W && py >= GameHud.CARD_Y && py <= GameHud.CARD_Y + GameHud.CARD_H) {
                return i;
            }
        }
        return -1;
    }

    private boolean hitRect(float px, float py, float x, float y, float w, float h) {
        return px >= x && px <= x + w && py >= y && py <= y + h;
    }
}
