package com.pvz.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.core.GameConfig;
import com.pvz.data.LevelData;
import com.pvz.util.DebugDraw;

/**
 * LawnSystem: quan ly san co cua mot level.
 *
 * Hai trach nhiem:
 *  1) Biet HANG (lane) nao HOAT DONG (doc tu LevelData.activeRows). Chi hang
 *     active moi co co, moi trong duoc cay, moi co zombie.
 *  2) Hieu ung CO TRAI mo man: dau tran co "moc" tu trai (sat nha) sang phai.
 *     Trong luc trai (chua xong): chua trong duoc, chua spawn zombie.
 *     Trai xong -> tran bat dau binh thuong.
 *
 * Cach trai: theo COT. Tat ca hang active cung tien sang phai dong thoi
 * (theo dung yeu cau). spreadProgress chay tu 0 -> 1 trong SPREAD_DURATION giay.
 *
 * Thoi gian o day dung gameDelta (tu GameClock) de pause/speed van dung.
 */
public final class LawnSystem {

    /** Thoi gian co trai het san (giay, theo game-time). */
    private static final float SPREAD_DURATION = 1.6f;

    private final boolean[] rowActive = new boolean[GameConfig.GRID_ROWS];
    private float spreadProgress = 0f; // 0 = chua trai, 1 = trai xong
    private boolean finished = false;

    // mau co (2 sac xen ke cho de nhin o luoi)
    private static final Color GRASS_A = new Color(0.30f, 0.62f, 0.22f, 1f);
    private static final Color GRASS_B = new Color(0.34f, 0.68f, 0.26f, 1f);

    public LawnSystem(LevelData level) {
        // xac dinh hang active
        if (level != null && level.activeRows != null && level.activeRows.length > 0) {
            for (int r : level.activeRows) {
                if (r >= 0 && r < GameConfig.GRID_ROWS) rowActive[r] = true;
            }
        } else {
            // mac dinh: tat ca hang
            for (int r = 0; r < GameConfig.GRID_ROWS; r++) rowActive[r] = true;
        }
    }

    /** Cap nhat hieu ung trai co. delta tu GameClock (scaled/pause). */
    public void update(float delta) {
        if (finished) return;
        spreadProgress += delta / SPREAD_DURATION;
        if (spreadProgress >= 1f) {
            spreadProgress = 1f;
            finished = true;
        }
    }

    /** Co da trai xong chua (de GameScreen biet khi nao bat dau spawn zombie). */
    public boolean isReady() { return finished; }

    /** Hang co hoat dong khong (bat ke co da trai toi chua). */
    public boolean isRowActive(int row) {
        return row >= 0 && row < GameConfig.GRID_ROWS && rowActive[row];
    }

    /**
     * O (row, col) da co co phu toi chua -> co trong cay duoc khong.
     * Dieu kien: hang phai active VA cot phai nam trong vung co da trai toi.
     */
    public boolean isPlantable(int row, int col) {
        if (!isRowActive(row)) return false;
        if (col < 0 || col >= GameConfig.GRID_COLS) return false;
        // so cot da duoc co phu = progress * tong cot
        float coveredCols = spreadProgress * GameConfig.GRID_COLS;
        return col < coveredCols;
    }

    /** Ve san co. Chi ve phan da trai toi, tren cac hang active. */
    public void draw(SpriteBatch batch) {
        DebugDraw dd = DebugDraw.get();
        float coveredCols = spreadProgress * GameConfig.GRID_COLS;

        for (int r = 0; r < GameConfig.GRID_ROWS; r++) {
            if (!rowActive[r]) continue;
            float y = GameConfig.LAWN_Y + r * GameConfig.CELL_HEIGHT;
            for (int c = 0; c < GameConfig.GRID_COLS; c++) {
                float colEdge = c; // o thu c duoc phu khi coveredCols > c
                if (coveredCols <= colEdge) break; // cac cot sau chua trai toi

                float x = GameConfig.LAWN_X + c * GameConfig.CELL_WIDTH;
                float w = GameConfig.CELL_WIDTH;
                // cot dang trai do (mep) -> ve mot phan chieu rong cho muot
                float fillRatio = Math.min(1f, coveredCols - colEdge);
                w *= fillRatio;

                Color col = ((r + c) % 2 == 0) ? GRASS_A : GRASS_B;
                dd.rect(batch, x, y, w, GameConfig.CELL_HEIGHT, col);
            }
        }
    }

    public float getSpreadProgress() { return spreadProgress; }
}
