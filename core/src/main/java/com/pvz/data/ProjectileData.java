package com.pvz.data;

/**
 * ProjectileData: anh xa voi file projectiles/<ten>.json (pea, snow_pea...).
 *
 * Field CHUNG: speed, damage, animation. (id = TEN FILE json.)
 * Nhom TUY CHON: slow (chi snow_pea dung). Co nhom slow = lam cham; khong co = thuong.
 * (Bo co boolean "slows" thua: su ton tai cua nhom slow da noi len y nghia.)
 */
public class ProjectileData {

    public float speed;            // pixel/giay bay sang phai
    public float damage;
    public String[] animation;     // region atlas

    public SlowStats slow;         // null neu khong lam cham

    public static class SlowStats {
        public float factor;       // 0.5 = cham con 1 nua
        public float duration;     // giay
    }

    /** Tien ich: projectile nay co lam cham khong. */
    public boolean slows() { return slow != null; }
}
