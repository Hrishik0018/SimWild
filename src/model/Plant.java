package model;

import core.Grid;

public class Plant extends Entity {

    // UPDATED CONSTRUCTOR
    public Plant(int x, int y, int energy) {
        super(x, y, energy);
    }

    @Override
    public void act(Grid grid) {
        // plant does nothing
    }
}