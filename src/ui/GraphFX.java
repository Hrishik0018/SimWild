package ui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.LinkedList;

public class GraphFX {

    private XYChart.Series<Number, Number> plant = new XYChart.Series<>();
    private XYChart.Series<Number, Number> herb = new XYChart.Series<>();
    private XYChart.Series<Number, Number> carn = new XYChart.Series<>();
    private XYChart.Series<Number, Number> energySeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> predatorPrey = new XYChart.Series<>();

    private LinkedList<Integer> plantBuffer = new LinkedList<>();
    private LinkedList<Integer> herbBuffer = new LinkedList<>();
    private LinkedList<Integer> carnBuffer = new LinkedList<>();

    private final int WINDOW = 4;

    private LineChart<Number, Number> popChart;

    public GraphFX() {
        plant.setName("🌱 Plants");
        herb.setName("🐄 Herbivores");
        carn.setName("🐺 Carnivores");
        energySeries.setName("⚡ Avg Energy");
        predatorPrey.setName("🐄 vs 🐺");
    }

    public void show() {

        popChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        popChart.setTitle("Population Trends");
        popChart.setCreateSymbols(true);
        popChart.getData().addAll(plant, herb, carn);

        LineChart<Number, Number> energyChart =
                new LineChart<>(new NumberAxis(), new NumberAxis());
        energyChart.setTitle("Average Energy");
        energyChart.setCreateSymbols(true);
        energyChart.getData().add(energySeries);

        LineChart<Number, Number> ppChart =
                new LineChart<>(new NumberAxis(), new NumberAxis());
        ppChart.setTitle("Predator–Prey Balance");
        ppChart.setCreateSymbols(true);
        ppChart.getData().add(predatorPrey);

        VBox root = new VBox(popChart, energyChart, ppChart);

        Stage stage = new Stage();
        stage.setScene(new Scene(root, 650, 900));
        stage.setTitle("Ecosystem Graph");
        stage.show();

        // ✅ COLORS + LEGEND FIX
        Platform.runLater(() -> {
            plant.getNode().setStyle("-fx-stroke: green;");
            herb.getNode().setStyle("-fx-stroke: orange;");
            carn.getNode().setStyle("-fx-stroke: red;");
            energySeries.getNode().setStyle("-fx-stroke: blue;");
            predatorPrey.getNode().setStyle("-fx-stroke: purple;");

            for (Node n : popChart.lookupAll(".chart-legend-item-symbol")) {
                if (n.getStyleClass().contains("default-color0"))
                    n.setStyle("-fx-background-color: green;");
                else if (n.getStyleClass().contains("default-color1"))
                    n.setStyle("-fx-background-color: orange;");
                else if (n.getStyleClass().contains("default-color2"))
                    n.setStyle("-fx-background-color: red;");
            }
        });
    }

    private double smooth(LinkedList<Integer> buffer, int val) {
        buffer.add(val);
        if (buffer.size() > WINDOW) buffer.removeFirst();
        return buffer.stream().mapToDouble(i -> i).average().orElse(val);
    }

    public void update(int p, int h, int c, double avgEnergy, int step) {

        // 👉 extinction fix
        if (p == 0) plantBuffer.clear();
        if (h == 0) herbBuffer.clear();
        if (c == 0) carnBuffer.clear();

        double sp = (p == 0) ? 0 : Math.round(smooth(plantBuffer, p));
        double sh = (h == 0) ? 0 : Math.round(smooth(herbBuffer, h));
        double sc = (c == 0) ? 0 : Math.round(smooth(carnBuffer, c));

        plant.getData().add(new XYChart.Data<>(step, sp));
        herb.getData().add(new XYChart.Data<>(step, sh));
        carn.getData().add(new XYChart.Data<>(step, sc));

        energySeries.getData().add(new XYChart.Data<>(step, avgEnergy));
        predatorPrey.getData().add(new XYChart.Data<>(step, (double) h / (c + 1)));

        // memory limit
        if (plant.getData().size() > 150) {
            plant.getData().remove(0);
            herb.getData().remove(0);
            carn.getData().remove(0);
            energySeries.getData().remove(0);
            predatorPrey.getData().remove(0);
        }
    }

    public void reset() {
        plant.getData().clear();
        herb.getData().clear();
        carn.getData().clear();
        energySeries.getData().clear();
        predatorPrey.getData().clear();

        plantBuffer.clear();
        herbBuffer.clear();
        carnBuffer.clear();
    }
}