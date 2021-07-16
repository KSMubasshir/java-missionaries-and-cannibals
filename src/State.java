import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class State implements Iterable<State> {

    private static final int MIN_TOTAL = 1;

    private static final int MIN_BOAT_CAPACITY = 1;

    public enum BoatLocation {

        SOURCE_BANK,
        
        TARGET_BANK
    }

    private final int missionaries;

    private final int cannibals;

    private final int totalMissionaries;

    private final int totalCannibals;

    private final int boatCapacity;

    private final BoatLocation boatLocation;

    public State(int missionaries,
            int cannibals,
            int totalMissionaries,
            int totalCannibals,
            int boatCapacity,
            BoatLocation boatLocation) {
        Objects.requireNonNull(boatLocation, "Boat location is null.");
        checkTotalMissionaries(totalMissionaries);
        checkTotalCannibals(totalCannibals);
        checkMissionaryCount(missionaries, totalMissionaries);
        checkCannibalCount(cannibals, totalCannibals);
        checkBoatCapacity(boatCapacity);

        this.missionaries = missionaries;
        this.cannibals = cannibals;
        this.totalMissionaries = totalMissionaries;
        this.totalCannibals = totalCannibals;
        this.boatCapacity = boatCapacity;
        this.boatLocation = boatLocation;
    }

    public static State getInitialStateNode(int totalMissionaries,
            int totalCannibals,
            int boatCapacity) {
        return new State(totalMissionaries,
                totalCannibals,
                totalMissionaries,
                totalCannibals,
                boatCapacity,
                BoatLocation.SOURCE_BANK);
    }

    public boolean isSolutionState() {
        return boatLocation == BoatLocation.TARGET_BANK
                && missionaries == 0
                && cannibals == 0;
    }

    public boolean isTerminalState() {
        if (missionaries > 0 && missionaries < cannibals) {
            return true;
        }

        int missionariesAtTargetBank = totalMissionaries - missionaries;
        int cannibalsAtTargetBank = totalCannibals - cannibals;

        if (missionariesAtTargetBank > 0
                && missionariesAtTargetBank < cannibalsAtTargetBank) {
            return true;
        }

        return false;
    }

    @Override
    public Iterator<State> iterator() {
        return new NeighborStateIterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int missionaryFieldLength = ("" + totalMissionaries).length();
        int cannibalFieldLength = ("" + totalCannibals).length();

        sb.append(String.format("[m: %" + missionaryFieldLength + "d",
                missionaries));
        sb.append(String.format(", c: %" + cannibalFieldLength + "d]",
                cannibals));
        switch (boatLocation) {
            case SOURCE_BANK: {
                sb.append("v ~~~  ");
                break;
            }

            case TARGET_BANK: {
                sb.append("  ~~~ v");
                break;
            }
        }

        sb.append(String.format("[m: %" + missionaryFieldLength + "d",
                totalMissionaries - missionaries));
        sb.append(String.format(", c: %" + cannibalFieldLength + "d]",
                totalCannibals - cannibals));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof State)) {
            return false;
        }

        State other = (State) o;
        return missionaries == other.missionaries
                && cannibals == other.cannibals
                && totalMissionaries == other.totalMissionaries
                && totalCannibals == other.totalCannibals
                && boatLocation == other.boatLocation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.missionaries;
        hash = 31 * hash + this.cannibals;
        hash = 31 * hash + this.totalMissionaries;
        hash = 31 * hash + this.totalCannibals;
        hash = 31 * hash + Objects.hashCode(this.boatLocation);
        return hash;
    }
    private class NeighborStateIterator implements Iterator<State> {

        private final Iterator<State> iterator;

        public NeighborStateIterator() {
            this.iterator = generateNeighbors();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public State next() {
            return iterator.next();
        }

        private Iterator<State> generateNeighbors() {
            if (isTerminalState()) {
                return Collections.<State>emptyIterator();
            }

            List<State> list = new ArrayList<>();

            switch (State.this.boatLocation) {
                case SOURCE_BANK: {
                    trySendFromSourceBank(list);
                    break;
                }

                case TARGET_BANK: {
                    trySendFromTargetBank(list);
                    break;
                }
            }

            return list.iterator();
        }

        private void trySendFromSourceBank(List<State> list) {
            int availableMissionaries = Math.min(missionaries, boatCapacity);
            int availableCannibals = Math.min(cannibals, boatCapacity);

            for (int capacity = 1; capacity <= boatCapacity; ++capacity) {
                for (int m = 0; m <= availableMissionaries; ++m) {
                    for (int c = 0; c <= availableCannibals; ++c) {
                        if (0 < c + m && c + m <= capacity) {
                            list.add(new State(missionaries - m,
                                    cannibals - c,
                                    totalMissionaries,
                                    totalCannibals,
                                    boatCapacity,
                                    BoatLocation.TARGET_BANK));
                        }
                    }
                }
            }
        }

        private void trySendFromTargetBank(List<State> list) {
            int availableMissionaries
                    = Math.min(totalMissionaries - missionaries, boatCapacity);
            int availableCannibals
                    = Math.min(totalCannibals - cannibals, boatCapacity);

            for (int capacity = 1; capacity <= boatCapacity; ++capacity) {
                for (int m = 0; m <= availableMissionaries; ++m) {
                    for (int c = 0; c <= availableCannibals; ++c) {
                        if (0 < c + m && c + m <= capacity) {
                            list.add(new State(missionaries + m,
                                    cannibals + c,
                                    totalMissionaries,
                                    totalCannibals,
                                    boatCapacity,
                                    BoatLocation.SOURCE_BANK));
                        }
                    }
                }
            }
        }
    }

    private static void checkTotalMissionaries(int totalMissionaries) {
        checkIntNotLess(totalMissionaries,
                MIN_TOTAL,
                "The total amount of missionaries is too small: "
                + totalMissionaries + ". Should be at least "
                + MIN_TOTAL);
    }

    private static void checkTotalCannibals(int totalCannibals) {
        checkIntNotLess(totalCannibals,
                MIN_TOTAL,
                "The total amount of cannibals is too small: "
                + totalCannibals + ". Should be at least "
                + MIN_TOTAL);
    }

    private static void checkMissionaryCount(int missionaries,
            int totalMissionaries) {
        checkNotNegative(missionaries,
                "Negative amount of missionaries: " + missionaries);
        checkIntNotLess(totalMissionaries,
                missionaries,
                "Missionaries at a bank (" + missionaries + "), "
                + "missionaries in total (" + totalMissionaries + ").");
    }

    private static void checkCannibalCount(int cannibals,
            int totalCannibals) {
        checkNotNegative(cannibals,
                "Negative amount of cannibals: " + cannibals);
        checkIntNotLess(totalCannibals,
                cannibals,
                "Cannibals at a bank (" + cannibals + "), "
                + "cannibals in total (" + totalCannibals + ").");
    }

    private static void checkBoatCapacity(int boatCapacity) {
        checkIntNotLess(boatCapacity,
                MIN_BOAT_CAPACITY,
                "Boat capacity too small: " + boatCapacity + ", "
                + "must be at least " + MIN_BOAT_CAPACITY + ".");
    }
    private static void checkIntNotLess(int integer,
            int minimum,
            String errorMessage) {
        if (integer < minimum) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void checkNotNegative(int integer, String errorMessage) {
        checkIntNotLess(integer, 0, errorMessage);
    }
}
