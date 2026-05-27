package com.pvz.core;

/**
 * Hang so cau hinh toan cuc cua game. Tap trung mot cho de chinh.
 * Cac gia tri lien quan gameplay rieng tung level/entity nam trong JSON;
 * day chi chua nhung gi co dinh ve khung game.
 */
public final class GameConfig {

    private GameConfig() {}

    // ----- Do phan giai target -----
    public static final int WORLD_WIDTH = 1280;
    public static final int WORLD_HEIGHT = 720;

    // ----- Luoi san co (grid) -----
    public static final int GRID_ROWS = 5;
    public static final int GRID_COLS = 9;

    /**
     * Vung dat san co tinh bang pixel. Cac so duoi day la GIA TRI MAU,
     * ban se chinh lai cho khop voi anh nen that sau nay.
     * Goc (LAWN_X, LAWN_Y) la goc duoi-trai cua o (row=0, col=0).
     */
    public static final float LAWN_X = 250f;   // le trai cua san
    public static final float LAWN_Y = 117f;    // le duoi cua san
    public static final float CELL_WIDTH = 95f;
    public static final float CELL_HEIGHT = 95f;

    // ----- Sun -----
    public static final int START_SUN_DEFAULT = 150;    // sun ban dau
    public static final float SUN_LIFETIME = 20f;       // sun bien mat sau 20s neu khong nhat

    // ----- Save / level -----
    public static final int FIRST_LEVEL = 1;
    public static final int LAST_LEVEL = 8;             // co 8 level (1-1 .. 1-8)

    // ----- Feature unlock (theo yeu cau) -----
    public static final int SHOVEL_UNLOCK_LEVEL = 3;    // shovel mo o level 1-3
    public static final int SPEED_UNLOCK_LEVEL = 4;     // speed 2x mo o level 1-4
}