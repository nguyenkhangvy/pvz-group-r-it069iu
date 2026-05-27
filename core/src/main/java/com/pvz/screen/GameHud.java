package com.pvz.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.core.GameConfig;
import com.pvz.data.PlantData;
import com.pvz.manager.AssetProvider;
import com.pvz.manager.DataManager;
import com.pvz.util.DebugDraw;

/**
 * GameHud: ve toan bo giao dien HUD trong man choi.
 *   - Sun box + so sun (text)
 *   - Seed cards (card_xxx)
 *   - Shovel, Speed, Setting buttons
 *   - Progress bar + wave markers (anh)
 *   - Placement preview
 *
 * Tach tu GameScreen de giam kich thuoc file. Nhan du lieu qua GameContext.
 */
public final class GameHud {

    // ===== BO CUC (trai -> phai) =====
    static final float TOP_Y = GameConfig.WORLD_HEIGHT - 95f;
    static final float HUD_H = 70f;

    static final float SUN_X = 55f;
    static final float SUN_W = 100f;

    static final float CARD_X0 = SUN_X + SUN_W + 30f;
    static final float CARD_Y = TOP_Y;
    static final float CARD_W = 80f;
    static final float CARD_H = HUD_H;
    static final float CARD_GAP = 8f;

    static final float SHOVEL_W = 70f;

    static final float SETTING_W = 110f;
    static final float SETTING_X = GameConfig.WORLD_WIDTH - SETTING_W - 20f;

    static final float SPEED_W = 70f;
    static final float SPEED_X = SETTING_X - SPEED_W - 50f;

    // mau
    private static final Color BAR_BG = new Color(0f, 0f, 0f, 0.45f);
    private static final Color BAR_FG = new Color(0.4f, 0.9f, 0.2f, 1f);
    private static final Color CARD_SEL = new Color(1f, 1f, 0.3f, 1f);

    private final GameContext ctx;

    public GameHud(GameContext ctx) {
        this.ctx = ctx;
    }

    public void draw() {
        DebugDraw dd = DebugDraw.get();
        drawSunBox(dd);
        drawSeedCards(dd);
        drawShovel();
        drawSpeedButton();
        drawSettingButton();
        drawProgressBar(dd);
    }

    public void drawPlacementPreview(float touchX, float touchY) {
        if (ctx.getSelectedPlant() == null) return;
        int col = ctx.getGrid().pixelXToCol(touchX);
        int row = ctx.getGrid().pixelYToRow(touchY);
        if (!ctx.getGrid().isValidCell(row, col)) return;

        float cx = GameConfig.LAWN_X + col * GameConfig.CELL_WIDTH;
        float cy = GameConfig.LAWN_Y + row * GameConfig.CELL_HEIGHT;
        boolean canPlace = ctx.getPlantGrid()[row][col] == null;
        Color hl = canPlace ? new Color(0.4f, 1f, 0.4f, 0.35f)
                            : new Color(1f, 0.3f, 0.3f, 0.35f);
        DebugDraw.get().rect(ctx.getBatch(), cx, cy,
            GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT, hl);
    }

    // ---------- Sun Box ----------
    private void drawSunBox(DebugDraw dd) {
        SpriteBatch batch = ctx.getBatch();
        TextureRegion img = AssetProvider.get().region("hud_sun");
        if (img != null) {
            batch.setColor(Color.WHITE);
            batch.draw(img, SUN_X, TOP_Y, SUN_W, HUD_H);
        }
        if (ctx.getFont() != null) {
            ctx.getFont().setColor(0f, 0f, 0f, 1f);
            ctx.getFont().draw(batch, String.valueOf(ctx.getSun()),
                SUN_X + SUN_W * 0.42f - 3f, TOP_Y + HUD_H / 2f + 8f);
            ctx.getFont().setColor(Color.WHITE);
        }
    }

    // ---------- Seed Cards ----------
    private void drawSeedCards(DebugDraw dd) {
        SpriteBatch batch = ctx.getBatch();
        AssetProvider ap = AssetProvider.get();
        for (int i = 0; i < ctx.getSeedCards().size; i++) {
            String id = ctx.getSeedCards().get(i);
            float cx = CARD_X0 + i * (CARD_W + CARD_GAP);
            PlantData pd = DataManager.get().plant(id);
            int cost = (pd != null) ? pd.cost : 0;
            float cd = ctx.getCardCooldown().get(id, 0f);
            boolean affordable = ctx.getSun() >= cost && cd <= 0f;

            TextureRegion card = ap.region("card_" + id);
            if (card != null) {
                batch.setColor(affordable ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 1f));
                batch.draw(card, cx, CARD_Y, CARD_W, CARD_H);
                batch.setColor(Color.WHITE);
                if (id.equals(ctx.getSelectedPlant())) {
                    dd.rect(batch, cx - 3, CARD_Y - 3, CARD_W + 6, 3, CARD_SEL);
                    dd.rect(batch, cx - 3, CARD_Y + CARD_H, CARD_W + 6, 3, CARD_SEL);
                    dd.rect(batch, cx - 3, CARD_Y - 3, 3, CARD_H + 6, CARD_SEL);
                    dd.rect(batch, cx + CARD_W, CARD_Y - 3, 3, CARD_H + 6, CARD_SEL);
                }
                if (cd > 0f) {
                    dd.rect(batch, cx, CARD_Y, CARD_W, CARD_H, new Color(0f, 0f, 0f, 0.5f));
                }
            }
        }
    }

    // ---------- Shovel ----------
    // shovel dat ngay truoc speed button (vi tri co dinh, khong phu thuoc so card)
    static final float SHOVEL_X = SPEED_X - SHOVEL_W - 20f;

    float shovelX() {
        return SHOVEL_X;
    }

    private void drawShovel() {
        if (ctx.getLevel() < GameConfig.SHOVEL_UNLOCK_LEVEL) return;
        SpriteBatch batch = ctx.getBatch();
        float sx = shovelX();
        TextureRegion icon = AssetProvider.get().region("hud_shovel");
        if (icon != null) {
            float pad = 6f, s = HUD_H - pad * 2;
            batch.setColor(ctx.isShovelSelected() ? Color.WHITE : new Color(0.8f, 0.8f, 0.8f, 1f));
            batch.draw(icon, sx + (SHOVEL_W - s) / 2f, TOP_Y + pad, s, s);
            batch.setColor(Color.WHITE);
        }
    }

    // ---------- Speed ----------
    private void drawSpeedButton() {
        if (ctx.getLevel() < GameConfig.SPEED_UNLOCK_LEVEL) return;
        SpriteBatch batch = ctx.getBatch();
        boolean on = ctx.getClock().isSpeed2x();
        TextureRegion icon = AssetProvider.get().region("hud_speed");
        if (icon != null) {
            float pad = 6f, s = HUD_H - pad * 2;
            batch.setColor(on ? Color.WHITE : new Color(0.7f, 0.7f, 0.7f, 1f));
            batch.draw(icon, SPEED_X + 6f, TOP_Y + pad, s, s);
            batch.setColor(Color.WHITE);
        }
    }

    // ---------- Setting ----------
    private void drawSettingButton() {
        SpriteBatch batch = ctx.getBatch();
        TextureRegion img = AssetProvider.get().region("hud_setting");
        if (img != null) {
            batch.setColor(Color.WHITE);
            batch.draw(img, SETTING_X, TOP_Y, SETTING_W, HUD_H);
        }
    }

    // ---------- Progress Bar ----------
    private void drawProgressBar(DebugDraw dd) {
        SpriteBatch batch = ctx.getBatch();
        float total = (ctx.getLevelData() != null && ctx.getLevelData().progressDuration > 0)
            ? ctx.getLevelData().progressDuration : 60f;
        float ratio = Math.min(1f, ctx.getBattleTime() / total);
        float barW = 360f, barH = 18f;
        float bx = GameConfig.WORLD_WIDTH - barW - 30f;
        float by = 24f;

        if (ctx.getFont() != null) {
            ctx.getFont().setColor(1f, 1f, 1f, 1f);
            ctx.getFont().draw(batch, "WAVE 1-" + ctx.getLevel(), bx - 120f, by + barH);
            ctx.getFont().setColor(Color.WHITE);
        }

        dd.rect(batch, bx, by, barW, barH, BAR_BG);
        dd.rect(batch, bx, by, barW * ratio, barH, BAR_FG);
    }
}
