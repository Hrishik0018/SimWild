package ui;

import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

public class GraphFX {

    private XYChart.Series<Number, Number> plant = new XYChart.Series<>();
    private XYChart.Series<Number, Number> herb = new XYChart.Series<>();
    private XYChart.Series<Number, Number> carn = new XYChart.Series<>();

    private int time = 0;

    public GraphFX() {
        plant.setName("Plants");
        herb.setName("Herbivores");
        carn.setName("Carnivores");
    }

    public void show() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();

        x.setLabel("Time");
        y.setLabel("Population");

        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.getData().addAll(plant, herb, carn);

        Stage stage = new Stage();
        stage.setScene(new Scene(chart, 600, 400));
        stage.setTitle("Live Graph");
        stage.show();
    }

    public void update(int p, int h, int c) {

        plant.getData().add(new XYChart.Data<>(time, p));
        herb.getData().add(new XYChart.Data<>(time, h));
        carn.getData().add(new XYChart.Data<>(time, c));

        time++;

        // memory control
        if(plant.getData().size() > 100){
            plant.getData().remove(0);
            herb.getData().remove(0);
            carn.getData().remove(0);
        }
    }

    public void reset() {
        plant.getData().clear();
        herb.getData().clear();
        carn.getData().clear();
        time = 0;
    }
}