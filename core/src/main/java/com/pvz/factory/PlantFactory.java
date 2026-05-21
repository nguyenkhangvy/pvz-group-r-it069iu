package com.pvz.factory;

import com.pvz.core.GameConfig;
import com.pvz.data.PlantData;
import com.pvz.entity.plant.CherryBomb;
import com.pvz.entity.plant.Chomper;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.plant.PotatoMine;
import com.pvz.manager.DataManager;
import com.pvz.system.GridSystem;

/**
 * PlantFactory (FACTORY METHOD pattern).
 * Tao dung lop con theo id. Cay co ban (peashooter, sunflower, wallnut, snowpea,
 * repeater) dung Plant base + data. Cay co hanh vi rieng dung lop con.
 */
public final class PlantFactory {

    private final DataManager data = DataManager.get();
    private final GridSystem grid;

    public PlantFactory(GridSystem grid) { this.grid = grid; }

    public Plant create(String plantId, int row, int col) {
        PlantData pd = data.plant(plantId);
        if (pd == null) return null; // chua co data -> khong tao

        float cx = grid.colToPixelX(col);
        float cy = grid.rowToPixelY(row);
        float w = GameConfig.CELL_WIDTH * 0.8f;
        float h = GameConfig.CELL_HEIGHT * 0.8f;

        switch (plantId) {
            case "cherrybomb":
                return new CherryBomb(pd, row, col, cx, cy, w, h);
            case "potatomine":
                return new PotatoMine(pd, row, col, cx, cy, w, h);
            case "chomper":
                return new Chomper(pd, row, col, cx, cy, w, h);
            // peashooter, sunflower, wallnut, snowpea, repeater dung Plant base:
            default:
                return new Plant(pd, row, col, cx, cy, w, h);
        }
    }
}
