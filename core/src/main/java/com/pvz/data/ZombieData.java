package com.pvz.data;

/**
 * ZombieData: anh xa voi file zombies/<ten>.json.
 *
 * Thiet ke theo NHOM giong PlantData: moi zombie chi khai bao thu no can.
 *  - Field CHUNG (moi zombie deu co): hp, speed, damage, eatInterval, animations.
 *  - Nhom TUY CHON: vault (chi PoleVault dung). Zombie khac bo han nhom nay khoi JSON.
 *
 * Khong can thuoc tinh "armor": Conehead/Buckethead chi don gian la hp lon hon.
 */
public class ZombieData {
    // (Khong can "id"/"displayName": id = TEN FILE json.)
    public float hp;
    public float speed;            // pixel/giay di chuyen sang trai
    public float damage;           // sat thuong moi don can
    public float eatInterval;      // giay giua 2 lan can

    // ----- NHOM TUY CHON (null neu khong dung) -----
    public VaultStats vault;       // chi PoleVault Zombie

    public AnimationStates animations;

    public static class VaultStats {
        public float speedAfterVault;  // toc do (pixel/giay) SAU khi nhay qua cay dau
    }

    public static class AnimationStates {
        public String[] walking;
        public String[] eating;
        public String[] dying;
        public String[] special;   // vault / flag-wave...
    }
}
