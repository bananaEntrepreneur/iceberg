package Model.seeders;

import Model.gamefield.Cell;
import Model.gamefield.Direction;
import Model.gamefield.GameField;
import Model.gamefield.Seeder;
import Model.units.Box;
import Model.units.Iceberg;

import java.awt.*;
import java.awt.geom.Dimension2D;

public class SimpleSeeder extends Seeder {

    @Override
    public Dimension2D getFieldSize(){
        return new Dimension(4, 3);
    }

    @Override
    protected void seedRobot(GameField field) {
        field.getCell(0, 0).putUnit( field.getUserRobot() );
    }

    @Override
    protected void seedCharacteristics(GameField field) {
        for (Cell c: field ) {
            c.setCharacteristic("temperature", -1);
            c.setCharacteristic("radiation", 1.2);
        }
    }

    @Override
    protected void seedUnits(GameField field) {

        Cell underRobotCell = field.getUserRobot().typedOwner();
        underRobotCell = underRobotCell.getNeighbor( Direction.south()) ;

        underRobotCell.putUnit( new Box() );

        Cell icebergCell = field.getCell(2, 1);
        if (icebergCell.isEmpty()) {
            Iceberg iceberg = new Iceberg();
            icebergCell.putUnit(iceberg);
            iceberg.applyCoolingEffect(icebergCell);
        }
    }
}
