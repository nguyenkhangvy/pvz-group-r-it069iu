package com.pvz.entity.plant;

import com.badlogic.gdx.utils.Array;
import com.pvz.entity.zombie.Zombie;

/**
 * PlantContext: "cau noi" giua cay va the gioi game (GameScreen cung cap).
 *
 * Ly do thiet ke (SOLID - Dependency Inversion):
 *  - Cac cay dac biet (CherryBomb, PotatoMine, Chomper) can tac dong len zombie
 *    xung quanh, nhung KHONG nen biet truc tiep ve GameScreen.
 *  - Thay vao do chung phu thuoc vao interface nay. GameScreen implement no.
 *  - De test va de thay doi: co the thay GameScreen bang lop khac ma cay khong doi.
 */
public interface PlantContext {

    /** Lay tat ca zombie con song trong 1 o (row, col). */
    Array<Zombie> zombiesInCell(int row, int col);

    /** Lay tat ca zombie con song trong vung vuong tam (row,col) ban kinh `radius` o. */
    Array<Zombie> zombiesInArea(int row, int col, int radius);

    /** Gay sat thuong cho tat ca zombie trong vung (dung cho Cherry Bomb / Potato Mine). */
    void damageArea(int row, int col, int radius, float damage);

    /** Bao GameScreen go cay khoi luoi (vd Potato Mine sau khi no, Chomper... neu can). */
    void removePlant(Plant plant);
}
