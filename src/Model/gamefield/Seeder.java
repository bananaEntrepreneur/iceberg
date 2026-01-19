package Model.gamefield;

import java.awt.geom.Dimension2D;

// Абстрактный заполнитель поля
abstract public class Seeder {

    public void run(GameField field) {
        seedRobot(field);
        seedCharacteristics(field);
        seedUnits(field);
    }

    abstract public Dimension2D getFieldSize();

    abstract protected void seedRobot(GameField field);

    abstract protected void seedCharacteristics(GameField field);

    abstract protected void seedUnits(GameField field);
}
