import java.util.Random;

public class Plant extends Entity {
    Random rand = new Random();

    public Plant(int x, int y) {
        super(x, y, 5);
    }

    @Override
    public void act(Grid grid) {

        if (rand.nextDouble() < grid.plantGrowthRate) {

            for (int i = 0; i < 3; i++) {
                int nx = x + rand.nextInt(5) - 2;
                int ny = y + rand.nextInt(5) - 2;

                if (grid.isValid(nx, ny) && grid.isEmpty(nx, ny)) {
                    grid.addEntity(new Plant(nx, ny));
                    return;
                }
            }
        }
    }

    public String toString() {
        return "🌱";
    }
}