package Model.units;

import Model.gamefield.Direction;
import Model.gamefield.Cell;
import Model.updatableunit.UpdatableUnit;

// Робот, который может перемещаться по полю и взаимодействовать с другими юнитами
public class Robot extends UpdatableUnit {

    // ------------------------------- Заряд ---------------------------------
    private int _charge = 25;

    private static final int REQUIRED_CHARGE_FOR_MOVE = 1;
    private static final int REQUIRED_CHARGE_FOR_BREAKING = 5;

    public int charge() {
        return _charge;
    }

    protected boolean isAvailableCharge(int chargeQuery) {
        return chargeQuery <= _charge;
    }

    protected int reduceCharge(int chargeQuery) {
        int retrievedCharge = Math.min(_charge, chargeQuery);
        _charge -= retrievedCharge;
        return retrievedCharge;
    }

    // --------------------------- Перемещение ------------------------------------
    public boolean canMoveTo(Cell to) {
        return to.isEmpty();
    }

    public boolean move(Direction direct) {

        Cell pos = this.getPosition();

        if(!isAvailableCharge(REQUIRED_CHARGE_FOR_MOVE)) {
            return false;
        }

        Cell newPos = pos.getNeighbor(direct);
        if(newPos == null) {
            return false;
        }

        if (newPos.getUnit() instanceof Iceberg) {
            Iceberg iceberg = (Iceberg) newPos.getUnit();
            if (iceberg.isSolid() && isAvailableCharge(REQUIRED_CHARGE_FOR_BREAKING)) {

                boolean broken = iceberg.breakIceberg(newPos);
                if (broken) {
                    boolean robotPlaced = newPos.putUnit(this);
                    if (!robotPlaced) {
                        pos.putUnit(this);
                        return false;
                    }

                    pos.extractUnit();

                    reduceCharge(REQUIRED_CHARGE_FOR_BREAKING);
                    fireStateChanged();
                    return true;
                }
            }
            return false;
        }

        if(!canMoveTo(newPos)) {
            return false;
        }

        pos.extractUnit();
        newPos.putUnit(this);
        reduceCharge(REQUIRED_CHARGE_FOR_MOVE);

        fireStateChanged();
        return true;
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {

        String msg;
        msg = "R(" + charge() + ")";

        return msg;
    }
}
