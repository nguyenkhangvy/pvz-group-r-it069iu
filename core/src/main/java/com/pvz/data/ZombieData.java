package com.pvz.data;

/**
 * ZombieData: anh xa 1-1 voi zombies/<ten>.json.
 * Khong can thuoc tinh "armor" rieng; bucket/conehead chi don gian la hp lon hon.
 */
public class ZombieData {
    public String id;              // vd "basic", "conehead", "buckethead", "flag", "polevault"
    public String displayName;
    public float hp;
    public float speed;            // pixel/giay di chuyen sang trai
    public float damage;           // sat thuong moi don can
    public float eatInterval;      // bao lau can 1 lan (giay)

    // Kha nang dac biet
    public String ability;         // null | "polevault" | "flag" ...
    public float vaultSpeedBonus;  // polevault: toc do khi nhay (neu can)
    public float speedAfterVault;  // polevault: toc do (pixel/giay) SAU khi da nhay qua cay

    public AnimationStates animations;

    public static class AnimationStates {
        public String[] walking;
        public String[] eating;
        public String[] dying;
        public String[] special;   // vault/flag-wave...
    }
}
