package Model.gamefield;

import java.util.HashMap;
import java.util.Iterator;

import Model.units.Robot;

// Прямоугольное поле, состоящее из ячеек
// Навигация по полю возможна следующими способами:
// - последовательный обход ячеек
// - получение ячейки по позиции на плоскости
// - переход от ячейки к соседней ячейке
public class GameField implements Iterable<Cell> {

    // ---------------------- Размеры -----------------------------

    private final int _width;
    private final int _height;

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public CellularRegion getRegion() {
        return new CellularRegion(new CellPosition(0, 0), _width, _height);
    }

    // --------------------------- Ячейки ----------------------

    private final HashMap<CellPosition, Cell> _cells = new HashMap<>();

    public Cell getCell(CellPosition pos) {
        return _cells.get( pos );
    }

    public Cell getCell(int row, int col) {
        return getCell(new CellPosition(row, col));
    }

    @Override
    public Iterator<Cell> iterator() {
        return iterator( getRegion() );
    }

    public Iterator<Cell> iterator(CellularRegion region) {
        return new FieldIterator(this, region);
    }

    // ---------------------------- Порождение ---------------------

    public GameField( Seeder seeder) {

        _width = (int) seeder.getFieldSize().getWidth();
        _height = (int) seeder.getFieldSize().getHeight();
        buildField();

        seeder.run(this);
    }

    private void buildField() {

        // Создаем ячейки
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                CellPosition pos = new CellPosition(row, col);
                _cells.put(pos, new Cell(this, pos));
            }
        }

        // Связываем ячейки
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {

                Cell cell = getCell(row, col);

                if (getHeight() > 1 && row < getHeight() - 1) {
                    cell.setNeighbor(Direction.south(), getCell(row + 1, col));
                }
                if (row > 0) {
                    cell.setNeighbor(Direction.north(), getCell(row - 1, col));
                }
                if (getWidth() > 1 && col < getWidth() - 1) {
                    cell.setNeighbor(Direction.east(), getCell(row, col + 1));
                }
                if (col > 0) {
                    cell.setNeighbor(Direction.west(), getCell(row, col - 1));
                }
            }
        }
    }

    // ---------------------- Робот ---------------------------

    private final Robot _user_robot = new Robot();

    public Robot getUserRobot() {
        return _user_robot;
    }


    // --------------- Итератор по ячейкам ----------------

    private static class FieldIterator implements Iterator<Cell> {

        private Cell _cell = null;
        private final GameField _field;
        private final Iterator<CellPosition> _region_iter;

        public FieldIterator(GameField field, CellularRegion region) {
            _field = field;
            CellularRegion intesect_region = field.getRegion().intersect( region );
            _region_iter = intesect_region.iterator();
        }

        @Override
        public boolean hasNext() {
            return _region_iter.hasNext();
        }

        @Override
        public Cell next() {
            _cell = nextCell(_cell);
            return _cell;
        }

        private Cell nextCell(Cell cell) {
            CellPosition next_pos = _region_iter.next();
            Cell next_cell = null;

            if( next_pos != null ) {
                next_cell = _field.getCell( next_pos );
            }

            return next_cell;
        }
    }
}