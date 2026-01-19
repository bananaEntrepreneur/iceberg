package Model.gamefield;

import java.util.Iterator;

public class CellularRegion implements Iterable<CellPosition> {
    private final CellPosition _leftTop;
    private final int _width;
    private final int _height;

    private CellPosition _currentPos;

    public CellularRegion(CellPosition leftTop, int width, int height) {
        _leftTop = leftTop;
        _width = width;
        _height = height;
    }

    public CellularRegion(CellPosition center, int deltaUp, int deltaDown, int deltaLeft, int deltaRight) {
        _leftTop = center.shift(-deltaUp, -deltaDown);
        _width = deltaLeft + deltaRight + 1;
        _height = deltaUp + deltaDown + 1;
    }

    public CellPosition getLeftTop() {
        return _leftTop;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getLeft() {
        return _leftTop.getColumn();
    }

    public int getRight() {
        return getLeft() + _width - 1;
    }

    public int getTop() {
        return _leftTop.getRow();
    }

    public int getBottom() {
        return getTop() + _height - 1;
    }

    public boolean isEmpty() {
        return _width == 0 || _height == 0;
    }

    @Override
    public Iterator<CellPosition> iterator() {
        return new CellularRegionIterator(this);
    }


    // ------------------ Принадлежность позиции ----------------

    public boolean contains(CellPosition pos) {
        // Проверяем принадлежность к вертикальным границам
        boolean isWithinVertical = pos.getRow() >= this.getTop() && pos.getRow() <= this.getBottom();

        // Проверяем принадлежность к горизонтальным границам
        boolean isWithinHorizontal = pos.getColumn() >= this.getLeft() && pos.getColumn() <= this.getRight();

        return isWithinVertical && isWithinHorizontal;
    }

    // ------------------ Сравнение регионов ----------------
    @Override
    public boolean equals(Object other) {

        if (other == null) {
            return false;
        }

        if (!(other instanceof CellularRegion)) {
            return false;
        }

        // Типы совместимы, можно провести преобразование
        CellularRegion other_region = (CellularRegion) other;
        // Возвращаем результат сравнения
        return getLeftTop().equals(other_region.getLeftTop()) &&
                getWidth() == other_region.getWidth() &&
                getHeight() == other_region.getHeight();
    }

    public CellularRegion intersect(CellularRegion other) {
        // Вычисляем границы пересечения
        int intersectLeft = Math.max(this.getLeft(), other.getLeft());
        int intersectRight = Math.min(this.getRight(), other.getRight());
        int intersectTop = Math.max(this.getTop(), other.getTop());
        int intersectBottom = Math.min(this.getBottom(), other.getBottom());

        // Проверяем, существует ли пересечение
        if (intersectLeft > intersectRight || intersectTop > intersectBottom) {
            // Пересечение пусто — возвращаем регион с нулевой площадью
            return new CellularRegion(new CellPosition(0, 0), 0, 0);
        } else {
            // Вычисляем ширину и высоту пересечения
            int intersectWidth = intersectRight - intersectLeft + 1;
            int intersectHeight = intersectBottom - intersectTop + 1;

            return new CellularRegion(new CellPosition(intersectTop, intersectLeft),
                    intersectWidth, intersectHeight);
        }
    }

    /**
     * Объединение двух регионов.
     *
     * Возвращает МИНИМАЛЬНЫЙ ОХВАТЫВАЮЩИЙ РЕГИОН (Bounding Box), который
     * содержит оба входных региона.
     */
    public CellularRegion union(CellularRegion other) {
        // Вычисляем границы объединённого региона
        int unionLeft = Math.min(this.getLeft(), other.getLeft());
        int unionRight = Math.max(this.getRight(), other.getRight());
        int unionTop = Math.min(this.getTop(), other.getTop());
        int unionBottom = Math.max(this.getBottom(), other.getBottom());

        // Вычисляем ширину и высоту объединённого региона
        int unionWidth = unionRight - unionLeft + 1;
        int unionHeight = unionBottom - unionTop + 1;

        // Создаём и возвращаем новый регион
        return new CellularRegion(
                new CellPosition(unionTop, unionLeft),
                unionWidth,
                unionHeight
        );
    }

    // ----------------------------------------------------------------------

    private static class CellularRegionIterator implements Iterator<CellPosition> {

        private final CellularRegion _region;
        private CellPosition _pos = null;

        public CellularRegionIterator(CellularRegion region) {
            _region = region;
        }

        @Override
        public boolean hasNext() {

            return nextPos( _pos ) != null;
        }

        @Override
        public CellPosition next() {
            _pos = nextPos(_pos);
            return _pos;
        }

        private CellPosition nextPos(CellPosition pos) {
            CellPosition next_pos = null;

            if(pos == null) {
                next_pos = _region.getLeftTop(); // в начале обхода начинаем с левого-верхнего угла региона
            } else {
                // Определяем позицию следующей ячейки из региона
                if(pos.getColumn() < _region.getRight()) {               // не достигли правой границы региона
                    next_pos = pos.shift(0, 1);
                } else if(pos.getRow() < _region.getBottom()) {          // не достигли нижней границы региона
                    next_pos = new CellPosition(pos.getRow() + 1, _region.getLeft());
                }
            }

            return next_pos;
        }
    }
}
