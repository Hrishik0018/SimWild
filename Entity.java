public abstract class Entity {
    protected int x, y, energy, id;

    private static int counter = 1;

    public Entity(int x, int y, int energy) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.id = counter++;
    }

    public abstract void act(Grid grid);

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isDead(int threshold) {
        return energy <= threshold;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}