package com.pvz.factory;

import com.pvz.core.GameConfig;
import com.pvz.data.PlantData;
import com.pvz.entity.plant.CherryBomb;
import com.pvz.entity.plant.Chomper;
import com.pvz.entity.plant.DefensivePlant;
import com.pvz.entity.plant.Plant;
import com.pvz.entity.plant.PotatoMine;
import com.pvz.entity.plant.ShooterPlant;
import com.pvz.entity.plant.SunflowerPlant;
import com.pvz.manager.DataManager;
import com.pvz.system.GridSystem;

/**
 * PlantFactory (FACTORY METHOD pattern).
 *
 * Map id -> dung lop chuyen biet:
 *   peashooter, snowpea, repeater -> ShooterPlant   (cay ban)
 *   sunflower                     -> SunflowerPlant (cay san sun)
 *   wallnut                       -> DefensivePlant (cay thu)
 *   cherrybomb / potatomine / chomper -> lop hanh vi dac biet
 *
 * Nho tach lop, moi cay chi mang dung trach nhiem cua no (Single Responsibility):
 * Peashooter khong con sunTimer, Sunflower khong con attackTimer.
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
            case "sunflower":
                return new SunflowerPlant(pd, row, col, cx, cy, w, h);
            case "wallnut":
                return new DefensivePlant(pd, row, col, cx, cy, w, h);
            case "peashooter":
            case "snowpea":
            case "repeater":
                return new ShooterPlant(pd, row, col, cx, cy, w, h);
            default:
                // cay moi chua phan loai: doan theo nhom data co mat.
                if (pd.shoot != null) return new ShooterPlant(pd, row, col, cx, cy, w, h);
                if (pd.sun != null)   return new SunflowerPlant(pd, row, col, cx, cy, w, h);
                return new DefensivePlant(pd, row, col, cx, cy, w, h);
        }
    }
}
