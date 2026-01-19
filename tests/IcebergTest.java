import Model.gamefield.*;
import Model.ownership.Unit;
import Model.units.Box;
import Model.units.IceShard;
import Model.units.Iceberg;
import Model.seeders.SimpleSeeder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IcebergTest {

    private GameField field;
    private Cell centerCell;
    private Cell adjacentCell;

    @BeforeEach
    void setUp() {
        field = new GameField(new SimpleSeeder());
        centerCell = field.getCell(0, 1);
        adjacentCell = centerCell.getNeighbor(Direction.east());
    }

    @Test
    void testIcebergCreation() {
        Iceberg iceberg = new Iceberg();
        
        assertTrue(iceberg.isSolid(), "New iceberg should be solid");
        assertNull(iceberg.getOwner(), "New iceberg should not have an owner initially");
    }

    @Test
    void testApplyCoolingEffect() {
        Iceberg iceberg = new Iceberg();
        centerCell.putUnit(iceberg);

        double originalCenterTemp = centerCell.getCharacteristic("temperature");

        iceberg.applyCoolingEffect(centerCell);

        assertEquals(originalCenterTemp - 1.0, centerCell.getCharacteristic("temperature"), 0.001,
                "Center cell temperature should decrease by 1");

        int cooledCellsCount = 0;
        for (int row = Math.max(0, centerCell.getPosition().getRow() - 2);
             row <= Math.min(field.getHeight() - 1, centerCell.getPosition().getRow() + 2);
             row++) {
            for (int col = Math.max(0, centerCell.getPosition().getColumn() - 2);
                 col <= Math.min(field.getWidth() - 1, centerCell.getPosition().getColumn() + 2);
                 col++) {
                Cell testCell = field.getCell(row, col);
                if (testCell != null) {
                    assertEquals(originalCenterTemp - 1.0, testCell.getCharacteristic("temperature"), 0.001,
                            "Cell at (" + row + "," + col + ") should be cooled");
                    cooledCellsCount++;
                }
            }
        }

        assertTrue(cooledCellsCount > 0, "At least the center cell should be cooled");
    }

    @Test
    void testRestoreOriginalTemperatures() {
        Iceberg iceberg = new Iceberg();
        centerCell.putUnit(iceberg);

        double originalCenterTemp = centerCell.getCharacteristic("temperature");

        iceberg.applyCoolingEffect(centerCell);

        assertNotEquals(originalCenterTemp, centerCell.getCharacteristic("temperature"),
                "Temperature should change after applying cooling effect");

        iceberg.restoreOriginalTemperatures();

        assertEquals(originalCenterTemp, centerCell.getCharacteristic("temperature"), 0.001,
                "Center cell temperature should be restored");

        for (int row = Math.max(0, centerCell.getPosition().getRow() - 2);
             row <= Math.min(field.getHeight() - 1, centerCell.getPosition().getRow() + 2);
             row++) {
            for (int col = Math.max(0, centerCell.getPosition().getColumn() - 2);
                 col <= Math.min(field.getWidth() - 1, centerCell.getPosition().getColumn() + 2);
                 col++) {
                Cell testCell = field.getCell(row, col);
                if (testCell != null) {
                    assertEquals(originalCenterTemp, testCell.getCharacteristic("temperature"), 0.001,
                            "Cell at (" + row + "," + col + ") should have original temperature restored");
                }
            }
        }
    }

    @Test
    void testBreakIcebergCreatesTwoShards() {
        if (!centerCell.isEmpty()) {
            centerCell.extractUnit();
        }

        Iceberg iceberg = new Iceberg();
        centerCell.putUnit(iceberg);

        iceberg.applyCoolingEffect(centerCell);

        Cell emptyAdjacentCell = null;
        for (Direction dir : new Direction[]{Direction.north(), Direction.south(), Direction.east(), Direction.west()}) {
            Cell neighbor = centerCell.getNeighbor(dir);
            if (neighbor != null && neighbor.isEmpty()) {
                emptyAdjacentCell = neighbor;
                break;
            }
        }

        if (emptyAdjacentCell != null) {
            double originalCenterTemp = centerCell.getCharacteristic("temperature");

            boolean broken = iceberg.breakIceberg(centerCell);

            assertTrue(broken, "Iceberg should break successfully");
            assertFalse(iceberg.isSolid(), "Iceberg should no longer be solid after breaking");

            Unit unitAtCenter = centerCell.getUnit();
            Unit unitAtEmptyAdjacent = emptyAdjacentCell.getUnit();

            assertNotNull(unitAtCenter, "There should be a unit at the center position");
            assertNotNull(unitAtEmptyAdjacent, "There should be a unit at the adjacent position");

            assertTrue(unitAtCenter instanceof IceShard, "Center should have an IceShard");
            assertTrue(unitAtEmptyAdjacent instanceof IceShard, "Adjacent cell should have an IceShard");

            assertEquals(originalCenterTemp - 1.0, centerCell.getCharacteristic("temperature"), 0.001,
                    "Center cell should be cooled by the center shard");
        } else {
            boolean broken = iceberg.breakIceberg(centerCell);
            assertFalse(broken, "Iceberg should not break if no adjacent empty space exists");
        }
    }
}