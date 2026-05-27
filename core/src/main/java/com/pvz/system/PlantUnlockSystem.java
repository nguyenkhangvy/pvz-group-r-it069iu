package com.pvz.system;

import com.badlogic.gdx.utils.Array;
import com.pvz.core.GameConfig;

/**
 * PlantUnlockSystem: tinh danh sach cay nguoi choi DA CO khi vao 1 level.
 *
 * Theo dung thiet ke cua ban:
 *  - Peashooter co tu DAU (level 1).
 *  - Cay unlock SAU KHI THANG level do (hardcode trong PlantUnlockSystem).
 *  - Khi vao choosing plant cua level N, nguoi choi co: peashooter + tat ca cay
 *    unlock tu cac level TRUOC N (1..N-1).
 *
 * Bang mapping (theo yeu cau):
 *   1-1 thang -> Sunflower
 *   1-2 thang -> Cherry Bomb
 *   1-3 thang -> Wall-nut   (va mo tinh nang shovel)
 *   1-4 thang -> Potato Mine (va mo tinh nang speed 2x)
 *   1-5 thang -> (khong cay moi)
 *   1-6 thang -> Snow Pea
 *   1-7 thang -> Chomper
 *   1-8 thang -> Repeater
 *
 * Vi du: vao choosing plant cua 1-5, da thang 1-1..1-4 => co:
 *   peashooter + sunflower + cherrybomb + wallnut + potatomine.
 *
 * LUU Y: ten id o day phai KHOP voi ten file JSON trong assets/data/plants/.
 * Neu ban muon dua hoan toan vao JSON, co the thay bang doc DataManager sau.
 */
public final class PlantUnlockSystem {

    private PlantUnlockSystem() {}

    /** Cay mac dinh co tu dau. */
    private static final String DEFAULT_PLANT = "peashooter";

    /** unlockOfLevel[n] = cay nhan duoc khi THANG level n (null = khong co). */
    private static String unlockOfLevel(int level) {
        switch (level) {
            case 1: return "sunflower";
            case 2: return "cherrybomb";
            case 3: return "wallnut";
            case 4: return null;          // 1-4: khong co cay moi (chi mo speed)
            case 5: return "potatomine";  // 1-5: thuong Potato Mine (theo data docx)
            case 6: return "snowpea";
            case 7: return "chomper";
            case 8: return "repeater";
            default: return null;
        }
    }

    /**
     * Tra ve danh sach cay co the chon khi vao choosing plant cua `currentLevel`.
     * Gom peashooter + cac cay unlock tu level 1..(currentLevel-1).
     */
    public static Array<String> getUnlockedPlants(int currentLevel) {
        Array<String> result = new Array<>();
        result.add(DEFAULT_PLANT);
        for (int lv = GameConfig.FIRST_LEVEL; lv < currentLevel; lv++) {
            String p = unlockOfLevel(lv);
            if (p != null && !result.contains(p, false)) result.add(p);
        }
        return result;
    }
}
