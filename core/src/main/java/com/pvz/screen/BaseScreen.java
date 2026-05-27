package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pvz.core.GameConfig;

/**
 * BaseScreen: lop co so cho moi man hinh. Thiet lap camera/viewport theo
 * do phan giai target 1280x720 (FitViewport giu ti le khi resize cua so).
 *
 * Cung cap san batch + shapes de lop con ve. Lop con override drawWorld()
 * va update().
 */
public abstract class BaseScreen extends ScreenAdapter {

    protected final OrthographicCamera camera;
    protected final Viewport viewport;
    protected final SpriteBatch batch;
    private boolean switched = false;

    protected BaseScreen() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera);
        viewport.apply(true);
        // SpriteBatch mac dinh chi chua 1000 sprite giua begin()/end().
        // GameScreen ve nhieu: luoi + entity + chu HUD (moi ky tu = 1 sprite) +
        // menu pause. Khi choi lau de vuot 1000 -> tran buffer native -> CRASH
        // (EXCEPTION_ACCESS_VIOLATION tai BufferUtils.copyJni). Tang len 4000.
        batch = new SpriteBatch(4000);
    }

    @Override
    public final void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.15f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        // Neu update() da chuyen sang man hinh khac (vd lose/win) thi screen nay
        // da bi dispose -> KHONG duoc goi draw() voi batch da dispose nua.
        if (switched) return;

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        draw();
    }

    /** Lop con goi khi no chu dong chuyen sang man hinh khac giua update(). */
    protected void markSwitched() { switched = true; }

    /** Lop con cap nhat logic. delta o day la delta tho tu engine. */
    protected abstract void update(float delta);

    /** Lop con ve noi dung. */
    protected abstract void draw();

    /**
     * Tien ich: ve anh nen full man hinh. Cac screen khong can lap lai doan
     * AssetProvider.get().region(name) + null check + batch.draw nua.
     */
    protected void drawBackground(String regionName) {
        com.badlogic.gdx.graphics.g2d.TextureRegion bg =
            com.pvz.manager.AssetProvider.get().region(regionName);
        if (bg != null) {
            batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
            batch.draw(bg, 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
