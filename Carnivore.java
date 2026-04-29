import java.util.Random;

public class Carnivore extends Entity {
    Random rand = new Random();

    public Carnivore(int x, int y) {
        super(x, y, 20);
    }

    @Override
    public void act(Grid grid) {

        energy--; // 🔋 loss

        // 🍖 HUNT (radius 2)
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {

                int nx = x + dx;
                int ny = y + dy;

                if (grid.isValid(nx, ny)) {
                    Entity e = grid.getEntity(nx, ny);

                    if (e instanceof Herbivore) {
                        if (rand.nextDouble() < grid.huntingRate) {
                            grid.removeEntity(e);
                            grid.moveEntity(this, nx, ny);
                            energy += 18;
                            return;
                        }
                    }
                }
            }
        }

        // 🧬 REPRODUCTION (need ≥2 nearby)
        if (energy > grid.carnReproduceThreshold &&
                grid.countNearby(this, Carnivore.class) >= 2) {

            for (int i = 0; i < 5; i++) {
                int rx = x + rand.nextInt(5) - 2;
                int ry = y + rand.nextInt(5) - 2;

                if (grid.isValid(rx, ry) && grid.isEmpty(rx, ry)) {
                    grid.addEntity(new Carnivore(rx, ry));
                    energy -= 10;
                    return;
                }
            }
        }

        // 🚶 MOVE
        int nx = x + rand.nextInt(5) - 2;
        int ny = y + rand.nextInt(5) - 2;

        if (grid.isValid(nx, ny) && grid.isEmpty(nx, ny)) {
            grid.moveEntity(this, nx, ny);
        }

        // 💀 DEATH
        if (isDead(grid.carnDeathThreshold)) {
            grid.removeEntity(this);
        }
    }

    public String toString() {
        return "🐺";
    }
}