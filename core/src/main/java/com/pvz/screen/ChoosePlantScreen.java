package com.pvz.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pvz.data.LevelData;
import com.pvz.manager.AssetProvider;
import com.pvz.manager.AudioManager;
import com.pvz.manager.DataManager;
import com.pvz.manager.ScreenManager;
import com.pvz.system.PlantUnlockSystem;
import com.pvz.util.MenuButton;

/**
 * ChoosePlantScreen - phien ban don gian.
 *
 * Ban chi can them vao assets/images/ (folder nao cung duoc, ten khong trung):
 *   bg_choose            - anh nen full man: da co logo, chu "CHOOSE YOUR
 *                          CAPYBARA", panel trai/phai, o sun, khung slot... (KHUNG TRONG).
 *   card_<plantId>       - card cay (gia sun da in trong card). Vd card_peashooter.
 *   card_zombie_<id>     - card zombie. Vd card_zombie_basic.
 *   btn_start_battle     - nut START BATTLE.
 *   btn_back             - nut BACK.
 *
 * Logic:
 *   - Bam card trong seed bank -> them vao slot duoi (toi da maxPlants cua level).
 *   - Bam lai card do (trong bank) HOAC bam card trong slot -> bo chon.
 *   - Card da chon bi lam mo trong bank.
 *   - START BATTLE -> GameScreen voi danh sach da chon. BACK -> StartupScreen.
 *
 * KHONG ve text/so sun (da nam trong card / anh nen). Chinh toa do o dau file
 * cho khop khung cua ban.
 */
public class ChoosePlantScreen extends BaseScreen {

    private final int level;
    private final int maxPlants;
    private final Array<String> available;
    private final Array<String> chosen = new Array<>();
    private final Array<String> zombieTypes = new Array<>();

    private final MenuButton startBtn, backBtn;
    private final Vector3 tmp = new Vector3();

    // ---- TOA DO (chinh cho khop anh nen cua ban) ----
    // seed bank: hang card cay tren cung (trong panel trai)
    private static final float CARD_W = 128f, CARD_H = 136f, CARD_GAP = 7f;
    private static final float BANK_X = 85f, BANK_Y = 390f;

    // thanh slot duoi cung (card da chon hien o day)
    private static final float SLOT_X = 165f, SLOT_Y = 25f;
    private static final float SLOT_W = 70f, SLOT_H = 60f, SLOT_GAP = 5f;

    // luoi card zombie ben phai
    private static final float ZGRID_X = 847f, ZGRID_TOP = 520f;
    private static final float ZCARD_W = 104f, ZCARD_H = 100f, ZCARD_GAP = 10f;
    private static final int ZCOLS = 3;

    // 2 nut
    private static final float START_X = 840f, START_Y = 50f, START_W = 348f, START_H = 150f;
    private static final float BACK_X = 900f, BACK_Y = 25f, BACK_W = 264f, BACK_H = 60f;

    public ChoosePlantScreen(int level) {
        this.level = level;
        LevelData ld = DataManager.get().level(level);
        this.maxPlants = (ld != null && ld.maxPlants > 0) ? ld.maxPlants : 6;
        this.available = PlantUnlockSystem.getUnlockedPlants(level);

        if (ld != null && ld.waves != null) {
            for (LevelData.Wave wv : ld.waves) {
                if (wv.zombieId != null && !zombieTypes.contains(wv.zombieId, false)) {
                    zombieTypes.add(wv.zombieId);
                }
            }
        }

        startBtn = new MenuButton("START BATTLE", START_X, START_Y, START_W, START_H)
                       .setImage("btn_start_battle", false);
        backBtn  = new MenuButton("BACK", BACK_X, BACK_Y, BACK_W, BACK_H)
                       .setImage("btn_back", false);
    }

    private static final int BANK_COLS = 5;

    private float[] cardPos(int i) {
        int c = i % BANK_COLS;
        int r = i / BANK_COLS;
        float x = BANK_X + c * (CARD_W + CARD_GAP);
        float y = BANK_Y - r * (CARD_H + CARD_GAP);
        return new float[]{ x, y };
    }

    @Override
    protected void update(float delta) {
        startBtn.update(viewport);
        backBtn.update(viewport);
 
        if (Gdx.input.justTouched()) {
            tmp.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(tmp);

            // bam card trong seed bank -> them/bo
            for (int i = 0; i < available.size; i++) {
                float[] r = cardPos(i);
                if (tmp.x >= r[0] && tmp.x <= r[0] + CARD_W
                        && tmp.y >= r[1] && tmp.y <= r[1] + CARD_H) {
                    toggle(available.get(i));
                    return;
                }
            }
            // bam card trong slot duoi -> bo chon
            for (int i = 0; i < chosen.size; i++) {
                float sx = SLOT_X + i * (SLOT_W + SLOT_GAP);
                if (tmp.x >= sx && tmp.x <= sx + SLOT_W
                        && tmp.y >= SLOT_Y && tmp.y <= SLOT_Y + SLOT_H) {
                    chosen.removeIndex(i);
                    AudioManager.get().playClick();
                    return;
                }
            }
        }

        if (startBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new GameScreen(level, chosen));
        } else if (backBtn.pollClick(viewport)) {
            AudioManager.get().playClick();
            markSwitched();
            ScreenManager.get().setScreen(new StartupScreen());
        }
    }

    private void toggle(String plantId) {
        AudioManager.get().playClick();
        if (chosen.contains(plantId, false)) {
            chosen.removeValue(plantId, false);
        } else if (chosen.size < maxPlants) {
            chosen.add(plantId);
        }
    }

    @Override
    protected void draw() {
        AssetProvider ap = AssetProvider.get();
        batch.begin();
        batch.setColor(Color.WHITE);

        // 1) anh nen full man (khung trong: logo, chu, panel, o sun, khung slot)
        drawBackground("bg_choose");

        // 2) card cay trong seed bank
        for (int i = 0; i < available.size; i++) {
            String id = available.get(i);
            float[] r = cardPos(i);
            TextureRegion card = ap.region("card_" + id);
            if (card != null) {
                boolean picked = chosen.contains(id, false);
                batch.setColor(picked ? new Color(1, 1, 1, 0.4f) : Color.WHITE); // chon roi -> mo
                batch.draw(card, r[0], r[1], CARD_W, CARD_H);
                batch.setColor(Color.WHITE);
            }
        }

        // 3) card cay da chon -> hien o slot duoi (1 hang ngang)
        for (int i = 0; i < chosen.size; i++) {
            float sx = SLOT_X + i * (SLOT_W + SLOT_GAP);
            TextureRegion card = ap.region("card_" + chosen.get(i));
            if (card != null) {
                batch.draw(card, sx, SLOT_Y, SLOT_W, SLOT_H);
            }
        }

        // 4) card zombie ben phai (luoi 3 cot)
        for (int i = 0; i < zombieTypes.size; i++) {
            int col = i % ZCOLS, row = i / ZCOLS;
            float x = ZGRID_X + col * (ZCARD_W + ZCARD_GAP);
            float y = ZGRID_TOP - ZCARD_H - row * (ZCARD_H + ZCARD_GAP);
            TextureRegion zc = ap.region("card_zombie_" + zombieTypes.get(i));
            if (zc != null) {
                batch.draw(zc, x, y, ZCARD_W, ZCARD_H);
            }
        }

        // 5) 2 nut
        startBtn.draw(batch, null);
        backBtn.draw(batch, null);

        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}