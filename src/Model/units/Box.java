package Model.units;

import Model.updatableunit.UpdatableUnit;

// Коробка, которая может открываться ключом
public class Box extends UpdatableUnit {

    // ------------------- Открывание коробки ---------------------------

    private boolean _isOpen = false;
    
    public boolean isOpen() {
        return _isOpen;
    }
    
    public boolean open(Key key) {
        if( key.belongTo(Robot.class) && key.isNeighbor(this) ) {
            _isOpen = true;

            fireStateChanged();
            return true;
        }

        return false;
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {

        String msg = "B";

        msg += "(";
        if( isOpen() ) {
            msg += "o";
        } else {
            msg += "c";
        }
        msg += ")";

        return msg;
    }
}
