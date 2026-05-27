package com.pvz.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.manager.AssetProvider;
import com.pvz.util.DebugDraw;

/**
 * LawnSystem: quan ly san co cua mot level.
 *
 * THEO YEU CAU MOI:
 *  - KHONG co hieu ung co trai dan. Ca hang hien NGAY tu dau.
 *  - Hang ACTIVE  -> ve co xanh (anh "tile_grass" lap moi o, khong thi khoi xanh).
 *  - Hang CHUA active -> ve dat nau (anh "tile_dirt" lap moi o, khong thi khoi nau).
 *  - Trong cay duoc ngay tu dau (chi can hang active).
 *
 * Neu da co anh nen full man (bg_lawn) ve ca san co san, ban co the BO QUA
 * viec ve co o day (xem comment DRAW_LANES o duoi) - chi giu logic active row.
 *
 * Cac method cu (isReady/isPlantable) duoc giu de GameScreen khong vo:
 *  - isReady()    : luon true (khong con cho trai).
 *  - isPlantable(): chi can hang active + cot hop le.
 */
public final class LawnSystem {

    /** Bat/tat viec ve dai co o day. Neu anh nen (bg_lawn) da ve san co -> de false. */
    private static final boolean DRAW_LANES = true;

    private final boolean[] rowActive = new boolean[GameConfig.GRID_ROWS];

    // mau fallback khi chua co anh
    private static final Color GRASS_A = new Color(0.42f, 0.72f, 0.30f, 1f);
    private static final Color GRASS_B = new Color(0.38f, 0.66f, 0.26f, 1f);
    private static final Color DIRT_A  = new Color(0.46f, 0.34f, 0.20f, 1f);
    private static final Color DIRT_B  = new Color(0.42f, 0.30f, 0.17f, 1f);

    public LawnSystem(LevelData level) {
        if (level != null && level.activeRows != null && level.activeRows.length > 0) {
            for (int r : level.activeRows) {
                if (r >= 0 && r < GameConfig.GRID_ROWS) rowActive[r] = true;
            }
        } else {
            // mac dinh: tat ca hang active
            for (int r = 0; r < GameConfig.GRID_ROWS; r++) rowActive[r] = true;
        }
    }

    /** Khong con hieu ung trai -> goi cho co le, khong lam gi. */
    public void update(float delta) { /* no-op: ca row hien ngay tu dau */ }

    /** Khong con cho trai -> san san sang ngay. */
    public boolean isReady() { return true; }

    public boolean isRowActive(int row) {
        return row >= 0 && row < GameConfig.GRID_ROWS && rowActive[row];
    }

    /** Tra ve mang cac hang dang active (de WaveSystem random). */
    public int[] getActiveRows() {
        int count = 0;
        for (boolean b : rowActive) if (b) count++;
        int[] result = new int[count];
        int idx = 0;
        for (int r = 0; r < GameConfig.GRID_ROWS; r++) {
            if (rowActive[r]) result[idx++] = r;
        }
        return result;
    }

    /** Trong cay duoc khong: chi can hang active + cot hop le (khong cho trai nua). */
    public boolean isPlantable(int row, int col) {
        return isRowActive(row) && col >= 0 && col < GameConfig.GRID_COLS;
    }

    /**
     * Ve san: moi O ve mot tile (active=co, chua active=dat). Lap tile cho ca hang.
     *  - active   -> anh "tile_grass" (1 o co)
     *  - inactive -> anh "tile_dirt"  (1 o dat)
     * Chua co anh -> khoi mau xen ke.
     */
    public void draw(SpriteBatch batch) {
        if (!DRAW_LANES) return;

        DebugDraw dd = DebugDraw.get();
        AssetProvider ap = AssetProvider.get();
        TextureRegion grass = ap.region("tile_grass"); // 1 O co (tuy chon)
        TextureRegion dirt  = ap.region("tile_dirt");  // 1 O dat

        for (int r = 0; r < GameConfig.GRID_ROWS; r++) {
            float y = GameConfig.LAWN_Y + r * GameConfig.CELL_HEIGHT;
            boolean active = rowActive[r];
            TextureRegion tile = active ? grass : dirt;

            for (int c = 0; c < GameConfig.GRID_COLS; c++) {
                float x = GameConfig.LAWN_X + c * GameConfig.CELL_WIDTH;
                if (tile != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(tile, x, y, GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT);
                } else {
                    // fallback khoi mau xen ke cho de phan biet o
                    Color col;
                    if (active) col = ((r + c) % 2 == 0) ? GRASS_A : GRASS_B;
                    else        col = ((r + c) % 2 == 0) ? DIRT_A  : DIRT_B;
                    dd.rect(batch, x, y, GameConfig.CELL_WIDTH, GameConfig.CELL_HEIGHT, col);
                }
            }
        }
    }
}