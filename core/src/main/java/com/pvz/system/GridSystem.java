package com.pvz.system;

import com.pvz.core.GameConfig;

/**
 * GridSystem: chuyen doi giua toa do PIXEL va o luoi (row, col).
 *
 * Theo dung thiet ke cua ban:
 *  - Cay dat theo O (grid 5x9).
 *  - Zombie giu toa do PIXEL float, di chuyen muot; moi frame tinh col hien tai
 *    de check va cham "cung cell" voi cay.
 *  - Pea cung pixel-based, tinh row de biet dang o hang nao.
 *
 * row = 0 la hang DUOI cung, col = 0 la cot TRAI cung (sat nha).
 * Quy uoc nay co the doi tuy anh nen; chi can sua cong thuc o day.
 */
public final class GridSystem {

    /** Tam (center) pixel X cua mot cot. */
    public float colToPixelX(int col) {
        return GameConfig.LAWN_X + col * GameConfig.CELL_WIDTH + GameConfig.CELL_WIDTH / 2f;
    }

    /** Tam (center) pixel Y cua mot hang. */
    public float rowToPixelY(int row) {
        return GameConfig.LAWN_Y + row * GameConfig.CELL_HEIGHT + GameConfig.CELL_HEIGHT / 2f;
    }

    /** Pixel X -> col. Tra ve -1 neu nam ngoai san. */
    public int pixelXToCol(float x) {
        if (x < GameConfig.LAWN_X) return -1;
        int col = (int) ((x - GameConfig.LAWN_X) / GameConfig.CELL_WIDTH);
        return (col >= 0 && col < GameConfig.GRID_COLS) ? col : -1;
    }

    /** Pixel Y -> row. Tra ve -1 neu nam ngoai san. */
    public int pixelYToRow(float y) {
        if (y < GameConfig.LAWN_Y) return -1;
        int row = (int) ((y - GameConfig.LAWN_Y) / GameConfig.CELL_HEIGHT);
        return (row >= 0 && row < GameConfig.GRID_ROWS) ? row : -1;
    }

    public boolean isValidCell(int row, int col) {
        return row >= 0 && row < GameConfig.GRID_ROWS
            && col >= 0 && col < GameConfig.GRID_COLS;
    }

    /** Pixel X tan cung ben phai cua san (noi zombie spawn). */
    public float rightSpawnX() {
        return GameConfig.LAWN_X + GameConfig.GRID_COLS * GameConfig.CELL_WIDTH + 40f;
    }

    /** Pixel X tan cung ben trai (sat nha) - zombie cham day = thua. */
    public float houseX() {
        return GameConfig.LAWN_X - 20f;
    }
}
