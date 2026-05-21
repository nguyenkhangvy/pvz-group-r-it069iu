package com.pvz.entity.plant;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.util.DebugDraw;
import com.pvz.data.PlantData;
import com.pvz.entity.Entity;

/**
 * Plant: lop co so cho moi cay. Gan voi 1 o (row, col).
 *
 * Cay co ban (Peashooter, Sunflower, Repeater, Wall-nut) dung truc tiep lop nay
 * + cau hinh JSON. Cay co hanh vi dac biet (CherryBomb, PotatoMine, Chomper, SnowPea
 * neu can) ke thua va override.
 *
 * VONG DOI:
 *  - update(float delta): dem thoi gian noi bo (van khoa thoi gian).
 *  - updateWithContext(delta, ctx): hanh vi can tac dong len the gioi (no, an zombie...).
 *    GameScreen goi ham nay moi frame. Cay co ban khong can ctx nen mac dinh rong.
 */
public class Plant extends Entity {

    protected final PlantData data;
    protected final int row;
    protected final int col;

    protected float attackTimer = 0f;   // dem nguoc toi luot tan cong
    protected float sunTimer = 0f;       // cho cay san xuat sun

    public Plant(PlantData data, int row, int col, float centerX, float centerY,
                 float width, float height) {
        super(centerX, centerY, width, height, data.hp);
        this.data = data;
        this.row = row;
        this.col = col;
    }

    @Override
    public void update(float delta) {
        attackTimer += delta;
        sunTimer += delta;
    }

    /**
     * Hanh vi can tac dong len the gioi. Cay co ban khong lam gi.
     * Lop con (CherryBomb, PotatoMine, Chomper) override.
     */
    public void updateWithContext(float delta, PlantContext ctx) {
        update(delta);
    }

    public boolean canAttack() {
        if (data.attackInterval <= 0) return false;
        if (attackTimer >= data.attackInterval) {
            attackTimer = 0f;
            return true;
        }
        return false;
    }

    public boolean canProduceSun() {
        if (data.sunInterval <= 0) return false;
        if (sunTimer >= data.sunInterval) {
            sunTimer = 0f;
            return true;
        }
        return false;
    }

    /** Cay co ban CO ban dan tu xa khong? */
    public boolean isShooter() {
        return data.projectileType != null && data.attackInterval > 0;
    }

    @Override
    public void draw(SpriteBatch batch) {
        // TODO: ve animation tu atlas khi co asset
    }

    @Override
    public void drawDebug(SpriteBatch batch) {
        DebugDraw.get().rectCentered(batch, x, y, width, height, Color.FOREST);
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public PlantData getData() { return data; }
    public boolean blocksZombie() { return data.blocksZombie; }

    /**
     * Zombie co DUNG LAI an cay nay khong?
     *
     * QUAN TRONG: trong PvZ, zombie dung lai an MOI cay no gap (Peashooter,
     * Sunflower, Wall-nut...), khong chi cay "chan". Vi vay mac dinh = true.
     *
     * Ngoai le (zombie di XUYEN qua, khong dung): cay sap tu bien mat/no
     * nhu Cherry Bomb, hoac Potato Mine da arm (se no ngay khi cham).
     * Cac lop con override ham nay.
     */
    public boolean isEatable() {
        return true;
    }
}
