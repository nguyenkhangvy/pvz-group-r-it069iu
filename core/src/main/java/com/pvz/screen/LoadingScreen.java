package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private boolean queued = false;
    private boolean done = false;
    // tien do hien thi (noi suy muot toi tien do that)
    private float shownProgress = 0f;

    public LoadingScreen() {
        font.getData().setScale(2f);
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
        float cx = GameConfig.WORLD_WIDTH / 2f;
        float cy = GameConfig.WORLD_HEIGHT / 2f;

        batch.begin();

        // chu "LOADING"
        font.setColor(Color.WHITE);
        layout.setText(font, "LOADING");
        font.draw(batch, "LOADING", cx - layout.width / 2f, cy + 80f);

        // khung thanh tien do
        float barW = 600f, barH = 34f;
        float bx = cx - barW / 2f, by = cy - 10f;
        dd.rect(batch, bx - 3, by - 3, barW + 6, barH + 6, new Color(0.5f, 0.42f, 0.22f, 1f)); // vien
        dd.rect(batch, bx, by, barW, barH, new Color(0.12f, 0.12f, 0.12f, 1f));                 // nen
        dd.rect(batch, bx, by, barW * shownProgress, barH, new Color(0.35f, 0.75f, 0.35f, 1f)); // tien do

        // phan tram
        int pct = Math.round(shownProgress * 100f);
        font.getData().setScale(1.4f);
        layout.setText(font, pct + "%");
        font.draw(batch, pct + "%", cx - layout.width / 2f, by - 20f);
        font.getData().setScale(2f);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
        // KHONG dispose 'assets' o day: da ban giao cho AudioManager dung tiep.
    }
}
