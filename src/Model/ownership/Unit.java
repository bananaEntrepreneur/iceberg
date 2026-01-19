package Model.ownership;

import Model.gamefield.Cell;

import static Model.ownership.RelativeUnitLocation.*;

// Игровой объект
public abstract class Unit {

    // -------------------------- Владелец ----------------------------

    private CanOwnUnit _owner;

    boolean setOwner(CanOwnUnit owner) {
        boolean ok =  (getOwner() == null) && canBelongTo(owner); // юнит никому не принадлежит и готов присоединиться к указанному владельцу
        if(ok) {
            _owner = owner;
        }
        return ok;
    }

    // !!! Поведение по умолчанию
    protected boolean canBelongTo(CanOwnUnit owner) {
        return true;
    }

    void removeOwner() {
        _owner = null;
    }

    public CanOwnUnit getOwner() {
        return _owner;
    }

    public <T> T typedOwner() {
        return (T)_owner;
    }

    public boolean belongTo(CanOwnUnit ob) {
        return getOwner() == ob;
    }

    public boolean belongTo(Class cl) {
        return getOwner().getClass() == cl;
    }


    // -------------------------- Позиция ----------------------------

    public Cell getPosition() {
        if(_owner == null) return null;

        if( getOwner() instanceof Cell) {          // !!! Знаем, что юнит верхнего уровня всегда должен принадлежать ячейке
            return (Cell)_owner;
        } else if( _owner instanceof Unit ) {
            return ((Unit)_owner).getPosition();  // будет работать, даже при многократной вложенности (как в матрешке)
        } else {
            throw new RuntimeException();
        }
    }

    // ------------------------- Соседи ------------------------------

    public boolean isNeighbor(Unit other) {
        boolean ok =  getPosition() != null && other.getPosition() != null;
        return ok && getPosition().isNeighbor( other.getPosition() );
    }

    public RelativeUnitLocation onSameField(Unit other) {
        if( getPosition() != null ) {
            if(  other.getPosition() != null ) {
                if( getPosition().getOwner() == other.getPosition().getOwner() ) {
                    return SAME_FIELD;
                } else {
                    return DIFFERENT_FIELDS;
                }
            }  else {
                return ONLY_THIS_UINT_OUT_FIELD;
            }
        } else {
            if(  other.getPosition() != null ) {
                return ONlY_OTHER_UNIT_OUT_FIELD;
            } else {
                return OUT_FIELDS;
            }
        }
    }
}
