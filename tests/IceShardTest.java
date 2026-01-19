import Model.gamefield.*;
import Model.ownership.Unit;
import Model.seeders.SimpleSeeder;
import Model.units.IceShard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IceShardTest {

    private GameField field;
    private Cell centerCell;

    @BeforeEach
    void setUp() {
        field = new GameField(new SimpleSeeder());
        centerCell = field.getCell(0, 1);
    }

    @Test
    void testIceShardCreation() {
        IceShard iceShard = new IceShard();
        
        assertNull(iceShard.getOwner(), "New ice shard should not have an owner initially");
    }

    @Test
    void testApplyCoolingEffect() {
        if (!centerCell.isEmpty()) {
            centerCell.extractUnit();
        }

        IceShard iceShard = new IceShard();
        centerCell.putUnit(iceShard);

        double originalCenterTemp = centerCell.getCharacteristic("temperature");

        iceShard.applyCoolingEffect(centerCell);

        assertEquals(originalCenterTemp - 1.0, centerCell.getCharacteristic("temperature"), 0.001,
                "Center cell temperature should decrease by 1");

        for (int row = Math.max(0, centerCell.getPosition().getRow() - 1);
             row <= Math.min(field.getHeight() - 1, centerCell.getPosition().getRow() + 1);
             row++) {
            for (int col = Math.max(0, centerCell.getPosition().getColumn() - 1);
                 col <= Math.min(field.getWidth() - 1, centerCell.getPosition().getColumn() + 1);
                 col++) {
                Cell testCell = field.getCell(row, col);
                if (testCell != null) {
                    assertEquals(originalCenterTemp - 1.0, testCell.getCharacteristic("temperature"), 0.001,
                            "Cell at (" + row + "," + col + ") should be cooled");
                }
            }
        }
    }

    @Test
    void testApplyCoolingEffectAfterRemoval() {
        if (!centerCell.isEmpty()) {
            centerCell.extractUnit();
        }

        IceShard iceShard = new IceShard();
        centerCell.putUnit(iceShard);

        double originalCenterTemp = centerCell.getCharacteristic("temperature");

        iceShard.applyCoolingEffect(centerCell);

        assertNotEquals(originalCenterTemp, centerCell.getCharacteristic("temperature"),
                "Temperature should change after applying cooling effect");

        centerCell.extractUnit();

        assertNotEquals(originalCenterTemp, centerCell.getCharacteristic("temperature"),
                "Temperature should remain changed after ice shard removal");
    }

    @Test
    void testApplyCoolingEffectOnEdgeOfField() {
        Cell edgeCell = field.getCell(0, 3);

        if (!edgeCell.isEmpty()) {
            edgeCell.extractUnit();
        }

        IceShard iceShard = new IceShard();
        edgeCell.putUnit(iceShard);

        double originalEdgeTemp = edgeCell.getCharacteristic("temperature");

        iceShard.applyCoolingEffect(edgeCell);

        assertEquals(originalEdgeTemp - 1.0, edgeCell.getCharacteristic("temperature"), 0.001,
                "Edge cell temperature should decrease by 1");

        int cooledCellsCount = 0;
        for (int row = Math.max(0, edgeCell.getPosition().getRow() - 1);
             row <= Math.min(field.getHeight() - 1, edgeCell.getPosition().getRow() + 1);
             row++) {
            for (int col = Math.max(0, edgeCell.getPosition().getColumn() - 1);
                 col <= Math.min(field.getWidth() - 1, edgeCell.getPosition().getColumn() + 1);
                 col++) {
                Cell testCell = field.getCell(row, col);
                if (testCell != null) {
                    assertEquals(originalEdgeTemp - 1.0, testCell.getCharacteristic("temperature"), 0.001,
                            "Cell at (" + row + "," + col + ") should be cooled");
                    cooledCellsCount++;
                }
            }
        }

        assertTrue(cooledCellsCount >= 1, "At least one cell should be cooled");
    }
}