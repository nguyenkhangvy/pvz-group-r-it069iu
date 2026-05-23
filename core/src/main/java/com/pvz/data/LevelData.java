package com.pvz.data;

/**
 * LevelData: anh xa 1-1 voi levels/level_1_X.json.
 * Chua wave timeline, huge wave, sun interval, max plants, unlock...
 */
public class LevelData {
    public String id;                  // "1-1"
    public int maxPlants;              // so cay toi da chon duoc o man nay
    public float sunFallInterval;      // bao lau roi 1 sun tu troi (giay)
    public int sunFallAmount;          // moi sun troi roi bao nhieu
    public float progressDuration;     // tong thoi gian du kien (giay) cho thanh progress

    public String unlockPlant;         // cay mo khoa khi THANG level nay (null neu khong)
    public String unlockZombie;        // zombie moi xuat hien (chi de hien o win screen)
    public String unlockFeature;       // "shovel" | "speed" | null

    /**
     * Cac hang (lane) HOAT DONG trong level nay. Index hang tu 0..4 (0 = duoi cung).
     * Hang giua = 2. Vi du:
     *   [2]        -> chi 1 hang giua
     *   [1,2,3]    -> 3 hang giua
     *   [0,1,2,3,4]-> ca 5 hang
     * Neu null/empty -> mac dinh dung TAT CA 5 hang.
     * Chi hang active moi co co, moi trong duoc cay, va moi co zombie.
     */
    public int[] activeRows;

    public Wave[] waves;               // timeline cac dot zombie
    public float[] hugeWaveTimes;      // cac moc (giay) la huge wave (mot lv co the nhieu)

    /** Mot dot spawn: tai thoi diem `time`, sinh `count` con zombie loai `zombieId`. */
    public static class Wave {
        public float time;             // giay ke tu dau man
        public String zombieId;
        public int count;
        public int[] rows;             // hang nao (null/empty = ngau nhien)
        public boolean huge;           // co phai huge wave khong
    }
}
