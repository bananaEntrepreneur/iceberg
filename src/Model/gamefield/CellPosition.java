package Model.gamefield;

import java.util.ArrayList;
import java.util.List;

//  Позиция ячейки
public class CellPosition {

    // ----------------------- Свойства --------------------------
    private final int _row;
    private final int _column;

    public int getRow() { return _row; }

    public int getColumn() { return _column; }

    private void validate(int row, int col) {
        if(row < 0 || col < 0) {
            throw new IllegalArgumentException();
        }
    }

    // ----------------------- Порождение --------------------------
    public CellPosition(int row, int col) {

        validate(row, col);

        _row = row;
        _column = col;
    }

    // ------------------ "Соседние" позиции ----------------
    public CellPosition shift(int row_delta, int col_delta) {
        int new_row = getRow() + row_delta;
        int new_col = getColumn() + col_delta;

        validate(new_row, new_col);

        return new CellPosition(new_row, new_col);
    }

    public CellPosition shift(Direction direct, int delta) {

        if(delta <= 0) {
            throw new IllegalArgumentException();
        }

        int row_delta = 0, col_delta = 0;

        if(direct.equals( Direction.east() )) {
            row_delta = 0; col_delta += delta;
        } else if(direct.equals( Direction.west() )) {
            row_delta = 0; col_delta -= delta;
        } else if(direct.equals( Direction.north() )) {
            row_delta -= delta; col_delta = 0;
        }else if(direct.equals( Direction.south() )) {
            row_delta += delta; col_delta = 0;
        } else {
            throw new RuntimeException();
        }

        return shift(row_delta, col_delta);
    }

    // ------------------ Сравнение позиций ----------------
    @Override
    public boolean equals(Object other){

        if (other == null) {
            return false;
        }

        if (!(other instanceof CellPosition)) {
            return false;
        }

        // Типы совместимы, можно провести преобразование
        CellPosition otherPosition = (CellPosition)other;
        // Возвращаем результат сравнения углов
        return getRow() == otherPosition.getRow() && getColumn() == otherPosition.getColumn();
    }

    public boolean isNeighbor(CellPosition other) {

        int row_delta = Math.abs( getRow() - other.getRow() );
        int col_delta = Math.abs( getColumn() - other.getColumn() );

        return  (row_delta == 0 && col_delta == 1) || (row_delta == 1 && col_delta == 0);
    }

    /**
     * Определяет приблизительное направление к другой позиции.
     * Возвращает пустое множество, если позиции совпадают
     * Возвращает одно направление, если позиции располагаются на одной линии (вертикальной или горизонтальной)
     * Возвращает два направления, если позиции располагаются не на одной линии (первое направление с преобладающим смещением)
     */
    public List<Direction> approximateDirectionTo(CellPosition other) {
        // Вычисляем дельты
        int rowDelta = other.getRow() - this.getRow();
        int colDelta = other.getColumn() - this.getColumn();

        // Определяем направление по преобладающему смещению
        int absRowDelta = Math.abs(rowDelta);
        int absColDelta = Math.abs(colDelta);


        List<Direction> directions = new ArrayList<>();
        // Вертикальное смещение: добавляется если есть И оно >= горизонтального
        if (absRowDelta > 0 && absRowDelta >= absColDelta ) {
            if(rowDelta < 0) {
                directions.add( Direction.north() );
            } else if(rowDelta > 0) {
                directions.add( Direction.south() );
            }
        }

        // Горизонтальное смещение: добавляется если есть И оно >= вертикального
        if (absColDelta > 0 && absColDelta >= absRowDelta) {
            if(colDelta < 0) {
                directions.add( Direction.west() );
            } else if(colDelta > 0) {
                directions.add( Direction.east() );
            }
        }

        return directions;
    }

    // --------------------------------------------------
    @Override
    public int hashCode() {
        // Одинаковые объекты должны возвращать одинаковые значения
        return getRow() * 1000 + getColumn();
    }
}
