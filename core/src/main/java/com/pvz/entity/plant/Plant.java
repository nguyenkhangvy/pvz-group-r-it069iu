package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.util.DebugDraw;
import com.pvz.data.PlantData;
import com.pvz.entity.AnimationComponent;
import com.pvz.entity.Entity;

/**
 * Plant: lop CO SO TRUU TUONG cho moi cay. Gan voi 1 o (row, col).
 *
 * Theo Single Responsibility: lop nay CHI lo nhung gi MOI cay deu co:
 *   - vi tri o (row, col), hp (tu Entity)
 *   - animation + draw (co anh thi ve anh, khong thi khoi mau)
 *   - vong doi update / updateWithContext
 *
 * No KHONG chua logic tan cong hay san xuat sun. Nhung thu do thuoc ve cac
 * lop con chuyen biet:
 *   - ShooterPlant   : cay ban (Peashooter, SnowPea, Repeater)
 *   - SunflowerPlant : cay san sun (Sunflower)
 *   - DefensivePlant : cay thu thuan (Wall-nut)
 *   - CherryBomb / PotatoMine / Chomper : hanh vi dac biet
 *
 * Nho vay Peashooter khong con mang sunTimer vo nghia, va Sunflower khong mang
 * attackTimer vo nghia.
 */
public abstract class Plant extends Entity {

    protected final PlantData data;
    protected final int row;
    protected final int col;

    protected final AnimationComponent anim = new AnimationComponent();

    /** Khi >0: dang phat animation tam thoi (shooting/special), het thi ve idle. */
    protected float actionAnimTimer = 0f;
    protected static final float ACTION_ANIM_TIME = 0.3f;

    protected Plant(PlantData data, int row, int col, float centerX, float centerY,
                    float width, float height) {
        super(centerX, centerY, width, height, data.hp);
        this.data = data;
        this.row = row;
        this.col = col;
        loadAnimations();
    }

    /** Nap cac trang thai animation tu JSON (co anh thi dung, khong thi fallback). */
    protected void loadAnimations() {
        if (data.animations == null) return;
        anim.addState("idle", data.animations.idle);
        anim.addState("shooting", data.animations.shooting);
        anim.addState("eating", data.animations.eating);
        anim.addState("special", data.animations.special);
        anim.setState("idle");
    }

    @Override
    public void update(float delta) {
        if (actionAnimTimer > 0f) {
            actionAnimTimer -= delta;
            if (actionAnimTimer <= 0f) anim.setState("idle");
        }
        anim.update(delta);
    }

    /**
     * Hanh vi can tac dong len the gioi (no, an zombie...). Mac dinh chi update noi bo.
     * Lop con (CherryBomb, PotatoMine, Chomper) override khi can ctx.
     */
    public void updateWithContext(float delta, PlantContext ctx) {
        update(delta);
    }

    /** Phat animation "shooting" trong giay lat roi tu ve idle. */
    public void playShootAnim() {
        anim.setState("shooting");
        actionAnimTimer = ACTION_ANIM_TIME;
    }

    /** Phat animation "special" trong giay lat roi tu ve idle. */
    public void playSpecialAnim() {
        anim.setState("special");
        actionAnimTimer = ACTION_ANIM_TIME;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion frame = anim.getFrame();
        if (frame != null) {
            batch.setColor(Color.WHITE);
            batch.draw(frame, x - width / 2f, y - height / 2f, width, height);
        } else {
            drawDebug(batch);
        }
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height, Color.FOREST);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public PlantData getData() { return data; }

    /**
     * Zombie co DUNG LAI an cay nay khong? Mac dinh = true (zombie an moi cay
     * no gap). Ngoai le: cay sap no/bien mat (Cherry, Potato da arm) override = false.
     */
    public boolean isEatable() {
        return true;
    }
}
