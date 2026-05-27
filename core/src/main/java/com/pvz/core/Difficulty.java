package com.pvz.core;

/**
 * Difficulty: 3 muc do kho. Moi muc co he so nhan vao chi so zombie.
 *
 * He so (hpMul / damageMul / speedMul) hien dat MAC DINH = 1.0 cho moi muc;
 * ban se DIEN LAI sau theo y muon (vi du HARD hp x1.5...). Chi can sua so o day,
 * KHONG phai dung den logic spawn/entity.
 *
 * ZombieFactory se nhan cac he so nay khi tao zombie (nhan vao hp/damage/speed).
 */
public enum Difficulty {
    EASY(1.0f, 1.0f, 1.0f),
    NORMAL(1.2f, 1.2f, 1.1f),
    HARD(1.4f, 1.4f, 1.2f);

    public final float hpMul;
    public final float damageMul;
    public final float speedMul;

    Difficulty(float hpMul, float damageMul, float speedMul) {
        this.hpMul = hpMul;
        this.damageMul = damageMul;
        this.speedMul = speedMul;
    }

    /** Doc an toan tu ten luu trong save (mac dinh EASY neu sai). */
    public static Difficulty fromName(String name) {
        if (name == null) return EASY;
        try { return Difficulty.valueOf(name); }
        catch (Exception e) { return EASY; }
    }

    /** Muc tiep theo (de kiem tra khoa/mo). null neu da la cao nhat. */
    public Difficulty next() {
        switch (this) {
            case EASY: return NORMAL;
            case NORMAL: return HARD;
            default: return null;
        }
    }
}
