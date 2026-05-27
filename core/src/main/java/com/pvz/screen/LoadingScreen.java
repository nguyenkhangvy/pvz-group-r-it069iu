package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.pvz.core.GameConfig;
import com.pvz.manager.AudioManager;
import com.pvz.manager.ScreenManager;
import com.pvz.util.DebugDraw;

/**
 * LoadingScreen: man hinh nap tai nguyen luc khoi dong.
 *
 * Vi sao can?
 *  - Game co ~69 file am thanh (13MB). Neu nap LAZY (lan dau phat moi nap tu dia)
 *    thi se GIAT khi lan dau co tieng moi trong tran. Nap truoc o day -> muot.
 *  - Co thanh tien do THAT dua tren AssetManager.getProgress().
 *
 * Cach hoat dong:
 *  - Liet ke toan bo file trong assets/audio/ va xep hang nap qua AssetManager
 *    (theme.wav nap dang Music vi dai; con lai nap dang Sound).
 *  - Moi frame goi assetManager.update() de nap dan; ve thanh tien do.
 *  - Khi xong: ban giao AssetManager cho AudioManager, roi sang StartupScreen.
 *
 * Luu y: dung BitmapFont mac dinh + DebugDraw (giong cac man khac), khong phu
 * thuoc Scene2D.
 */
public class LoadingScreen extends BaseScreen {

    private static final String AUDIO_DIR = "audio/";
    private static final String IMAGE_DIR = "images/";
    private static final String THEME_NAME = "theme"; // ten file nhac nen (bo qua duoi: .mp3/.ogg/.wav)

    private final AssetManager assets = new AssetManager();

    private boolean queued = false;
    private boolean done = false;
    // tien do hien thi (noi suy muot toi tien do that)
    private float shownProgress = 0f;

    public LoadingScreen() {
        queueAssets();
    }

    /** Xep hang tat ca file am thanh de nap. */
    /**
     * Xep hang toan bo file can nap (am thanh + hinh anh).
     *
     * Luu y: dung FileHandle.list() de quet thu muc. Cach nay chay tot khi chay
     * bang `./gradlew lwjgl3:run` (asset nam tren o dia that). Neu sau nay dong
     * goi thanh JAR, list() tren internal co the khong liet ke duoc -> luc do
     * can liet ke ten file thu cong hoac dung file manifest. Hien tai (chay dev)
     * thi quet tu dong nhu duoi day la du.
     */
    private void queueAssets() {
        // --- am thanh ---
        FileHandle audioDir = Gdx.files.internal(AUDIO_DIR);
        if (audioDir.exists()) {
            for (FileHandle f : audioDir.list()) {
                String name = f.name();
                if (name.endsWith(".wav") || name.endsWith(".ogg") || name.endsWith(".mp3")) {
                    if (f.nameWithoutExtension().equals(THEME_NAME)) {
                        assets.load(AUDIO_DIR + name, Music.class); // nhac nen -> Music (bat ke .mp3/.ogg/.wav)
                    } else {
                        assets.load(AUDIO_DIR + name, Sound.class);
                    }
                }
            }
        }

        // --- hinh anh ---
        FileHandle imageDir = Gdx.files.internal(IMAGE_DIR);
        if (imageDir.exists()) {
            for (FileHandle f : imageDir.list()) {
                String name = f.name();
                if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                    assets.load(IMAGE_DIR + name, com.badlogic.gdx.graphics.Texture.class);
                }
            }
        }

        queued = true;
    }

    @Override
    protected void update(float delta) {
        if (!queued) return;

        // nap dan: update() tra ve true khi da nap xong het
        boolean finished = assets.update();

        // noi suy tien do hien thi cho muot mat
        float target = assets.getProgress();
        shownProgress += (target - shownProgress) * Math.min(1f, delta * 8f);

        if (finished && !done) {
            done = true;
            shownProgress = 1f;
            // ban giao cho AudioManager dung tai nguyen da nap san
            AudioManager.get().attachAssetManager(assets, AUDIO_DIR);
            // ban giao cho AssetProvider dung texture da nap san
            com.pvz.manager.AssetProvider.get().attachAssetManager(assets);
            // bat nhac nen ngay (chay xuyen suot moi man)
            AudioManager.get().playTheme(AudioManager.THEME);
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    @Override
    protected void draw() {
        DebugDraw dd = DebugDraw.get();
        float W = GameConfig.WORLD_WIDTH;
        float cx = W / 2f;

        batch.begin();

        // --- NEN (anh "loading_bg") ---
        drawBackground("loading_bg");
        // --- THANH TIEN DO ---
        float barW = 620f, barH = 46f;
        float bx = cx - barW / 2f, by = 100f;
        // vien nau
        dd.rect(batch, bx - 6, by - 6, barW + 12, barH + 12, new Color(0.38f, 0.26f, 0.12f, 1f));
        // nen long mau kem dam
        dd.rect(batch, bx, by, barW, barH, new Color(0.86f, 0.80f, 0.62f, 1f));
        // phan tien do mau xanh la (2 sac de tao cam giac soc)
        float fillW = barW * shownProgress;
        dd.rect(batch, bx, by, fillW, barH, new Color(0.42f, 0.78f, 0.28f, 1f));
        dd.rect(batch, bx, by + barH * 0.5f, fillW, barH * 0.5f, new Color(0.52f, 0.86f, 0.34f, 1f));

        // --- ICON CHAY tren dau thanh (anh "loading_runner" neu co; khong thi cuc tron) ---
        float runnerSize = barH + 80f;
        float runnerX = bx + fillW - runnerSize / 2f;
        float runnerY = by + barH / 2f - runnerSize / 2f;
        runnerX = Math.max(bx - runnerSize / 4f, Math.min(runnerX, bx + barW - runnerSize * 0.75f));
        com.badlogic.gdx.graphics.g2d.TextureRegion runner =
            com.pvz.manager.AssetProvider.get().region("loading_runner");
        batch.setColor(Color.WHITE);
        batch.draw(runner, runnerX, runnerY, runnerSize, runnerSize);
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        // KHONG dispose font (dung chung qua FontProvider) va KHONG dispose 'assets'
        // (da ban giao cho AudioManager/AssetProvider dung tiep).
    }
}
