package com.pvz.data;

/**
 * LevelData: anh xa 1-1 voi levels/level_1_X.json.
 * Chua spawn timeline, sun interval, max plants, progress duration.
 */
public class LevelData {
    public String id;                  // "1-1"
    public int maxPlants;              // so cay toi da chon duoc o man nay
    public float sunFallInterval;      // bao lau roi 1 sun tu troi (giay)
    public int sunFallAmount;          // moi sun troi roi bao nhieu
    public float progressDuration;     // tong thoi gian du kien (giay) cho thanh progress
    public int[] activeRows;            // cac hang hoat dong, vd [1,2,3] hoac [0,1,2,3,4]

    public Wave[] waves;               // timeline spawn zombie theo thoi gian

    /** Tai thoi diem `time`, sinh `count` con zombie loai `zombieId`. */
    public static class Wave {
        public float time;             // giay ke tu dau tran
        public String zombieId;
        public int count;
        public int[] rows;             // hang nao (null/empty = ngau nhien)
    }
}
