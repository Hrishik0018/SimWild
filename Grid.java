import java.util.*;

public class Grid {
public int plantEnergy = 5;
public double herbConsumeRate = 1.0;

public int herbReproduceThreshold = 20;
public int carnReproduceThreshold = 28;

public int herbDeathThreshold = 0;
public int carnDeathThreshold = 0;

    private Entity[][] grid;
    private int rows, cols;
    private List<Entity> entities;

    public double plantGrowthRate;
    public double huntingRate;

    // stats
    public int plantBirth = 0, plantDeath = 0;
    public int herbBirth = 0, herbDeath = 0;
    public int carnBirth = 0, carnDeath = 0;

    public int prevP = 0, prevH = 0, prevC = 0;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        grid = new Entity[rows][cols];
        entities = new ArrayList<>();
    }

    public void addEntity(Entity e) {
        if (isEmpty(e.getX(), e.getY())) {
            grid[e.getX()][e.getY()] = e;
            entities.add(e);

            if (e instanceof Plant) plantBirth++;
            else if (e instanceof Herbivore) herbBirth++;
            else if (e instanceof Carnivore) carnBirth++;
        }
    }

    public void removeEntity(Entity e) {
        grid[e.getX()][e.getY()] = null;
        entities.remove(e);

        if (e instanceof Plant) plantDeath++;
        else if (e instanceof Herbivore) herbDeath++;
        else if (e instanceof Carnivore) carnDeath++;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x < rows && y < cols;
    }

    public boolean isEmpty(int x, int y) {
        return grid[x][y] == null;
    }

    public Entity getEntity(int x, int y) {
        return grid[x][y];
    }

    public void moveEntity(Entity e, int newX, int newY) {
        grid[e.getX()][e.getY()] = null;
        e.setPosition(newX, newY);
        grid[newX][newY] = e;
    }

    public int countNearby(Entity e, Class<?> type) {
        int count = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {

                int nx = e.getX() + dx;
                int ny = e.getY() + dy;

                if (isValid(nx, ny)) {
                    Entity other = getEntity(nx, ny);
                    if (other != null && other != e && type.isInstance(other)) count++;
                }
            }
        }
        return count;
    }
 
    public int count(Class<?> type) {
        int c = 0;
        for (Entity e : entities) {
            if (type.isInstance(e)) c++;
        }
        return c;
    }

    public void simulateStep() {
        List<Entity> copy = new ArrayList<>(entities);
        Collections.shuffle(copy);

        for (Entity e : copy) {
            if (entities.contains(e)) {
                e.act(this);
            }
        }
    }

    public void printGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == null)
                    System.out.print("◻️ ");
                else
                    System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printStats() {
        int p = count(Plant.class);
        int h = count(Herbivore.class);
        int c = count(Carnivore.class);

        System.out.println("🌱:" + p + " 🐑:" + h + " 🐺:" + c);

        System.out.println("Δ Change -> 🌱:" + (p - prevP) +
                " 🐑:" + (h - prevH) +
                " 🐺:" + (c - prevC));

        System.out.println("Births -> 🌱:" + plantBirth +
                " 🐑:" + herbBirth +
                " 🐺:" + carnBirth);

        System.out.println("Deaths -> 🌱:" + plantDeath +
                " 🐑:" + herbDeath +
                " 🐺:" + carnDeath);

        prevP = p;
        prevH = h;
        prevC = c;

        // reset per step
        plantBirth = plantDeath = 0;
        herbBirth = herbDeath = 0;
        carnBirth = carnDeath = 0;
    }
}