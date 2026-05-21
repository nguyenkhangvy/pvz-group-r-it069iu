package com.pvz.core;

/**
 * GameClock = "van khoa thoi gian" duy nhat cua game.
 *
 * Y tuong cot loi (theo dung yeu cau thiet ke):
 *  - Tach 2 dong thoi gian:
 *      + gameDelta : dung cho WORLD (zombie, sun, cooldown, progress bar...).
 *                    Bi anh huong boi PAUSE va SPEED (1x / 2x).
 *      + realDelta : dung cho UI/menu. Khong bao gio bi dong bang, de menu
 *                    van song khi pause.
 *  - PAUSE = ep gameDelta ve 0 (world dung lai, UI van chay).
 *  - SPEED 2x = nhan gameDelta voi timeScale (2.0). Vi progress bar cung dung
 *               gameDelta nen no cung chay nhanh gap doi -> dung yeu cau.
 *  - CLAMP: kep delta toi da (maxFrameTime) de tranh "nhay coc" sau khi lag
 *           hoac sau khi tab bi an mot luc lau.
 *
 * Quy tac vang: KHONG mot he thong nao duoc tu goi Gdx.graphics.getDeltaTime().
 * Tat ca deu nhan delta qua tham so update(float delta). GameClock la nguon
 * duy nhat sinh ra con so do.
 *
 * Class nay co tinh "thuan" (khong phu thuoc libGDX) nen de test va tai su dung.
 */
public final class GameClock {

    /** Gioi han delta moi frame (giay). Vi du 0.05 = toi da 50ms ~ 20fps logic. */
    public static final float MAX_FRAME_TIME = 0.05f;

    /** Toc do choi 1x. */
    public static final float SPEED_1X = 1.0f;
    /** Toc do choi 2x (mo khoa o level 4). */
    public static final float SPEED_2X = 2.0f;

    private boolean paused = false;
    private float timeScale = SPEED_1X;

    /** Tong thoi gian world da troi qua (giay, da scale). Huu ich cho wave/progress. */
    private float worldTime = 0f;

    /**
     * Goi 1 lan moi frame voi delta tho tu engine (Gdx.graphics.getDeltaTime()).
     * Tra ve gameDelta da xu ly (clamp + pause + speed) cho WORLD.
     */
    public float tick(float rawDelta) {
        float clamped = clamp(rawDelta);
        if (paused) {
            return 0f;
        }
        float gameDelta = clamped * timeScale;
        worldTime += gameDelta;
        return gameDelta;
    }

    /**
     * Tra ve realDelta cho UI/menu: chi clamp, KHONG bi pause, KHONG bi scale.
     */
    public float realDelta(float rawDelta) {
        return clamp(rawDelta);
    }

    private float clamp(float delta) {
        return Math.min(delta, MAX_FRAME_TIME);
    }

    // ----- Pause -----
    public void pause()  { paused = true; }
    public void resume() { paused = false; }
    public void togglePause() { paused = !paused; }
    public boolean isPaused() { return paused; }

    // ----- Speed -----
    public void setSpeed2x(boolean on) { timeScale = on ? SPEED_2X : SPEED_1X; }
    public boolean isSpeed2x() { return timeScale == SPEED_2X; }
    public float getTimeScale() { return timeScale; }

    // ----- World time -----
    public float getWorldTime() { return worldTime; }
    public void resetWorldTime() { worldTime = 0f; }

    /** Reset toan bo ve trang thai dau khi vao 1 level moi. */
    public void resetForNewLevel() {
        paused = false;
        timeScale = SPEED_1X;
        worldTime = 0f;
    }
}
