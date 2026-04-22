package model;

import core.Grid;

public abstract class Entity {
    protected int x, y;
    protected int energy;

    public Entity(int x, int y, int energy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
    }

    public abstract void act(Grid grid);

    public void move(Grid grid) {

        int newX, newY;

        do {
            newX = x + (int)(Math.random()*3) - 1;
            newY = y + (int)(Math.random()*3) - 1;

            newX = Math.max(0, Math.min(newX, grid.getRows()-1));
            newY = Math.max(0, Math.min(newY, grid.getCols()-1));

        } while (!grid.isEmpty(newX, newY));

        x = newX;
        y = newY;
    }

    public int getX(){ return x; }
    public int getY(){ return y; }
}