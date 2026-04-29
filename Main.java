import java.io.PrintStream;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        Scanner sc = new Scanner(System.in);

        // 🎛️ USER INPUT
        System.out.print("Steps: ");
        int steps = sc.nextInt();

        System.out.print("Plant growth rate (0-1): ");
        double plantRate = sc.nextDouble();

        System.out.print("Hunting rate (0-1): ");
        double huntRate = sc.nextDouble();

        System.out.print("🌱 Plant life span: ");
        int plantEnergy = sc.nextInt();

        System.out.print("🐑 Herbivore consumption rate (1-3): ");
        double herbConsume = sc.nextDouble();

        // 🧠 GRID SETUP
        Grid grid = new Grid(10, 10);
        grid.plantGrowthRate = plantRate;
        grid.huntingRate = huntRate;

        // ⚙️ NEW CONFIGS
        grid.plantEnergy = plantEnergy;
        grid.herbConsumeRate = herbConsume;

        Random rand = new Random();

        // 📊 GRAPH DATA STORAGE
        List<Integer> pData = new ArrayList<>();
        List<Integer> hData = new ArrayList<>();
        List<Integer> cData = new ArrayList<>();

        // ✅ INITIALIZATION (NO OVERLAP)

        int plants = 18;
        while (plants > 0) {
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if (grid.isEmpty(x, y)) {
                grid.addEntity(new Plant(x, y));
                plants--;
            }
        }

        int herb = 10;
        while (herb > 0) {
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if (grid.isEmpty(x, y)) {
                grid.addEntity(new Herbivore(x, y));
                herb--;
            }
        }

        int carn = 5;
        while (carn > 0) {
            int x = rand.nextInt(10);
            int y = rand.nextInt(10);

            if (grid.isEmpty(x, y)) {
                grid.addEntity(new Carnivore(x, y));
                carn--;
            }
        }

        // 🚀 SIMULATION LOOP
        for (int i = 1; i <= steps; i++) {

            System.out.println("\n===== STEP " + i + " =====");

            grid.printGrid();   // 🧱 grid
            grid.printStats();  // 📊 stats

            int p = grid.count(Plant.class);
            int h = grid.count(Herbivore.class);
            int c = grid.count(Carnivore.class);

            // 📊 STORE DATA FOR GRAPH
            pData.add(p);
            hData.add(h);
            cData.add(c);

            // ❗ COLLAPSE CHECK
            if (p == 0 || h == 0 || c == 0) {
                System.out.println("⚠️ ECOSYSTEM COLLAPSED!");
                break;
            }

            grid.simulateStep();

            Thread.sleep(300); // ⏳ delay
        }

        // 📊 SHOW GRAPH AFTER SIMULATION
        System.out.println("\n📊 Showing Graph...");
        Graph.show(pData, hData, cData);

        sc.close();
    }
}