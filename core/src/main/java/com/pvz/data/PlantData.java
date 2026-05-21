package com.pvz.data;

/**
 * PlantData: anh xa 1-1 voi file plants/<ten>.json.
 * libGDX Json se tu dien field theo TEN. Vi vay ten bien o day = ten key trong JSON.
 * Ban se ghi de file JSON sau, chi can giu DUNG ten field nay.
 */
public class PlantData {
    public String id;              // vd "peashooter"
    public String displayName;     // ten hien thi tren the bai
    public float hp;
    public int cost;               // gia sun
    public float cooldown;         // hoi chieu the bai (giay)
    public float damage;           // sat thuong moi don (neu co ban)
    public float attackInterval;   // khoang cach giua 2 lan tan cong (giay)
    public float range;            // tam ban (pixel) - 0 = vo han / ca hang

    // Sun production (cho Sunflower)
    public float sunInterval;      // bao lau san xuat 1 sun (giay), 0 = khong
    public int sunAmount;          // moi lan ra bao nhieu sun

    // Hanh vi dac biet
    public String projectileType;  // vd "pea", "snow_pea"; null neu khong ban
    public int projectilePerShot;  // Repeater = 2
    public float armTime;          // Potato Mine: thoi gian "arm" truoc khi co the no
    public float explodeDelay;     // Cherry Bomb: 1.2s sau khi dat thi no
    public int explodeRadius;      // ban kinh no theo o (Cherry = 1 -> 3x3)
    public float explodeDamage;    // sat thuong vung no (Cherry Bomb, Potato Mine)
    public float chewTime;         // Chomper: thoi gian nhai
    public float chompDamage;      // Chomper: sat thuong khi an nguyen con (instant kill thuong)
    public boolean blocksZombie;   // co chan zombie khong (Wall-nut, Chomper khi nhai...)

    // Animation: ten cac region trong atlas theo tung trang thai
    public AnimationStates animations;

    public static class AnimationStates {
        public String[] idle;
        public String[] shooting;
        public String[] eating;     // khi bi an (cay bi zombie an)
        public String[] special;    // arm/chew/explode tuy cay
    }
}
