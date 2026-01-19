package Model.units;

import Model.gamefield.Cell;
import Model.gamefield.CellPosition;
import Model.gamefield.Direction;
import Model.gamefield.GameField;
import Model.ownership.Unit;
import Model.updatableunit.UpdatableUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Iceberg extends UpdatableUnit {
    private boolean _isSolid;

    private Map<Cell, Double> _originalTemperatures = new HashMap<>();

    private List<Cell> _cooledCells = new ArrayList<>();

    public Iceberg() {
        _isSolid = true;
    }

    public void applyCoolingEffect(Cell centerCell) {
        if (centerCell == null) return;

        CellPosition centerPos = centerCell.getPosition();
        int centerRow = centerPos.getRow();
        int centerCol = centerPos.getColumn();

        GameField field = centerCell.getOwner();
        for (int row = centerRow - 2; row <= centerRow + 2; row++) {
            for (int col = centerCol - 2; col <= centerCol + 2; col++) {
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

    public void restoreOriginalTemperatures() {
        for (Map.Entry<Cell, Double> entry : _originalTemperatures.entrySet()) {
            Cell cell = entry.getKey();
            Double originalTemp = entry.getValue();

            if (cell != null) {
                cell.setCharacteristic("temperature", originalTemp);
            }
        }

        _originalTemperatures.clear();
        _cooledCells.clear();
    }

    public boolean breakIceberg(Cell currentCell) {
        if (!_isSolid || currentCell == null) {
            return false;
        }

        Cell adjacentEmptyCell = findAdjacentEmptyCell(currentCell);

        if (adjacentEmptyCell == null) {
            return false;
        }

        IceShard shard1 = new IceShard();
        IceShard shard2 = new IceShard();

        Unit currentUnit = currentCell.extractUnit();
        if (currentUnit != this) {
            currentCell.putUnit(currentUnit);
            return false;
        }

        restoreOriginalTemperatures();

        currentCell.putUnit(shard1);
        shard1.applyCoolingEffect(currentCell);

        adjacentEmptyCell.putUnit(shard2);
        shard2.applyCoolingEffect(adjacentEmptyCell);

        _isSolid = false;
        fireStateChanged();

        return true;
    }

    private Cell findAdjacentEmptyCell(Cell cell) {
        for (Direction dir : new Direction[]{Direction.north(), Direction.south(), Direction.east(), Direction.west()}) {
            Cell neighbor = cell.getNeighbor(dir);
            if (neighbor != null && neighbor.isEmpty()) {
                return neighbor;
            }
        }
        return null;
    }

    public boolean isSolid() {
        return _isSolid;
    }

    @Override
    public String toString() {
        return "I(" + (_isSolid ? "s" : "b") + ")";
    }
}
