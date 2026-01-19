package Model.ownership;

import Model.gamefield.Cell;

// Свойства обладания юнитом
public class OwnershipProperty implements CanOwnUnit {

    // ---------------------- Владелец свойства ---------------------
    private final CanOwnUnit _owner;

    public OwnershipProperty(CanOwnUnit owner) {
        if(owner == null) {
            throw new NullPointerException();
        }
        _owner = owner;
    }

    // ---------------------- Юнит, которым владеем ----------------
    private Unit _unit = null;

    private static boolean noCycleDetected(Unit unit, CanOwnUnit owner) {
        CanOwnUnit current = owner; // начинаем со следующего владельца в цепи

        while ( current != null && !(current instanceof Cell) ) { // идём вверх по цепи владений

            if (current == unit) { // если текущий владелец - это сам unit → ЦИКЛ!
                return false;
            }

            if (current instanceof Unit) {
                // Переходим выше в цепи
                Unit unitOwner = (Unit) current;
                current = unitOwner.getOwner();
            } else {
                // Неизвестный тип владельца
                throw new RuntimeException();
            }
        }

        return true;  // ✓ ЦИКЛА НЕ НАЙДЕНО - безопасно вложить unit
    }

    public boolean putUnit(Unit unit) {
        if(unit == null || !isEmpty()) { // имеется реальный юнит и слот у владельца не занят
            return false;
        }

        if ( !noCycleDetected(unit, _owner) ) { // цикл вложенности обнаружен - запретить вложение
            return false;
        }

        boolean ok = unit.setOwner(_owner);
        if( ok ) {
            _unit = unit;
        }

        return ok;
    }

    public Unit extractUnit(){
        if( !isEmpty() ) {
            _unit.removeOwner();
        }

        Unit removedUnit = _unit;
        _unit = null;

        return removedUnit;
    }

    public Unit getUnit() {
        return _unit;
    }

    public boolean isEmpty() {
        return _unit == null;
    }
}
