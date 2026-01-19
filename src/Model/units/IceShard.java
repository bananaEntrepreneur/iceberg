package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.CellPosition;
import Model.gamefield.GameField;
import Model.updatableunit.UpdatableUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IceShard extends UpdatableUnit {
    private Map<Cell, Double> _originalTemperatures = new HashMap<>();

    private List<Cell> _cooledCells = new ArrayList<>();
    
    public IceShard() { }

    public void applyCoolingEffect(Cell centerCell) {
        if (centerCell == null) return;
        
        CellPosition centerPos = centerCell.getPosition();
        int centerRow = centerPos.getRow();
        int centerCol = centerPos.getColumn();
        
        GameField field = centerCell.getOwner();
        for (int row = centerRow - 1; row <= centerRow + 1; row++) {
            for (int col = centerCol - 1; col <= centerCol + 1; col++) {
                if (row >= 0 && row < field.getHeight() && col >= 0 && col < field.getWidth()) {
                    Cell cell = field.getCell(row, col);

                    if (cell != null) {
                        double originalTemp = cell.getCharacteristic("temperature");
                        _originalTemperatures.put(cell, originalTemp);

                        cell.setCharacteristic("temperature", originalTemp - 1.0);

                        _cooledCells.add(cell);
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "IS";
    }
}