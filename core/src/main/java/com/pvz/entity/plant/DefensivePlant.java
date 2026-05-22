package com.pvz.entity.plant;

import com.pvz.data.PlantData;

/**
 * DefensivePlant: cay THU THUAN, chi dung chan zombie (Wall-nut).
 *
 * Khong ban, khong san sun. Suc manh nam o hp cao (chan zombie lau nho mau trau).
 * Khong co timer nao -> dung tinh than Single Responsibility: cay nay khong
 * "lam" gi ngoai viec ton tai va chiu don.
 */
public class DefensivePlant extends Plant {

    public DefensivePlant(PlantData data, int row, int col, float centerX, float centerY,
                          float width, float height) {
        super(data, row, col, centerX, centerY, width, height);
    }
}
