package com.pvz.data;

/**
 * PlantData: anh xa voi file plants/<ten>.json.
 *
 * Thiet ke theo NHOM (nested groups) de moi cay CHI khai bao thu no can:
 *  - Field CHUNG (moi cay deu co): hp, cost, cooldown, animations.
 *  - Cac NHOM TUY CHON: shoot / sun / explode / arm / chew. Cay khong dung nhom
 *    nao thi BO HAN nhom do khoi JSON -> libGDX de null. Khong con field thua.
 *
 * Vi du:
 *  - peashooter.json: chi co "shoot".
 *  - sunflower.json:  chi co "sun".
 *  - wallnut.json:    chi co hp cao (cay thu thuan).
 *  - cherrybomb.json: chi co "explode".
 *  - potatomine.json: co "arm" + "explode".
 *  - chomper.json:    co "chew".
 */
public class PlantData {

    // ----- CHUNG cho moi cay -----
    // (Khong can field "id"/"displayName": id = TEN FILE json, DataManager map bang ten file.)
    public float hp;
    public int cost;               // gia sun
    public float cooldown;         // hoi chieu the bai (giay)

    // ----- CAC NHOM TUY CHON (null neu cay khong dung) -----
    public ShootStats shoot;       // cay ban (Peashooter, SnowPea, Repeater)
    public SunStats sun;           // cay san sun (Sunflower)
    public ExplodeStats explode;   // cay no (CherryBomb, PotatoMine)
    public ArmStats arm;           // cay can "arm" truoc (PotatoMine)
    public ChewStats chew;         // cay nhai (Chomper)

    public AnimationStates animations;

    // ===== Cac nhom =====
    public static class ShootStats {
        public float attackInterval;   // giay giua 2 lan ban
        public String projectileType;  // "pea", "snow_pea"
        public int projectilePerShot;  // Repeater = 2
        // (Khong co "damage"/"range": sat thuong lay tu projectile; logic ban trung
        //  zombie dau tien cung hang nen khong dung range.)
    }

    public static class SunStats {
        public float interval;         // giay giua 2 lan nha sun
        public int amount;             // moi lan bao nhieu sun
    }

    public static class ExplodeStats {
        public float delay;            // CherryBomb: 1.2s sau khi dat
        public int radius;             // ban kinh theo o (1 -> 3x3)
        public float damage;           // sat thuong vung no
    }

    public static class ArmStats {
        public float armTime;          // PotatoMine: giay truoc khi san sang no
    }

    public static class ChewStats {
        public float chewTime;         // Chomper: giay nhai
        public float chompDamage;      // sat thuong khi an (thuong instant-kill)
    }

    public static class AnimationStates {
        public String[] idle;
        public String[] shooting;
        public String[] eating;
        public String[] special;
    }
}
