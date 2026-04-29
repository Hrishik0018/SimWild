package ui;

import core.Grid;
import model.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;

import java.util.*;

public class FXMain extends Application {

    private Grid grid = new Grid(15,15);
    private Label[][] cells = new Label[15][15];
    private Timeline timeline;

    private Label stats = new Label();
    private GraphFX graph = new GraphFX();

    private int graphCounter = 0;
    private int timeSteps = 0;

    @Override
    public void start(Stage stage){

        BorderPane root = new BorderPane();

        // GRID
        GridPane gp = new GridPane();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                Label l = new Label();
                l.setMinSize(35,35);
                l.setStyle("-fx-border-color:gray; -fx-alignment:center;");
                cells[i][j]=l;
                gp.add(l,j,i);
            }
        }

        // SLIDERS
        Slider speed = new Slider(200,1500,800);
        Label speedVal = new Label("800");

        Slider plantGrowth = new Slider(0.0,1.0,0.6);
        Label plantVal = new Label("0.6");

        Slider herbEat = new Slider(1,6,4);
        Label herbVal = new Label("4");

        Slider carnEat = new Slider(1,8,5);
        Label carnVal = new Label("5");

        Slider herbRepro = new Slider(8,25,14);
        Label herbRepVal = new Label("14");

        Slider carnRepro = new Slider(12,35,20);
        Label carnRepVal = new Label("20");

        Slider herbCount = new Slider(0,50,15);
        Label herbCountVal = new Label("15");

        Slider carnCount = new Slider(0,30,6);
        Label carnCountVal = new Label("6");

        Slider plantCount = new Slider(0,100,25);
        Label plantCountVal = new Label("25");

        // LISTENERS
        speed.valueProperty().addListener((o,a,b)-> speedVal.setText(""+b.intValue()));
        plantGrowth.valueProperty().addListener((o,a,b)-> plantVal.setText(String.format("%.2f", b.doubleValue())));
        herbEat.valueProperty().addListener((o,a,b)-> herbVal.setText(""+b.intValue()));
        carnEat.valueProperty().addListener((o,a,b)-> carnVal.setText(""+b.intValue()));
        herbRepro.valueProperty().addListener((o,a,b)-> herbRepVal.setText(""+b.intValue()));
        carnRepro.valueProperty().addListener((o,a,b)-> carnRepVal.setText(""+b.intValue()));
        herbCount.valueProperty().addListener((o,a,b)-> herbCountVal.setText(""+b.intValue()));
        carnCount.valueProperty().addListener((o,a,b)-> carnCountVal.setText(""+b.intValue()));
        plantCount.valueProperty().addListener((o,a,b)-> plantCountVal.setText(""+b.intValue()));

        VBox sliders = new VBox(8,
                new Label("Speed"), speed, speedVal,
                new Label("Plant Growth"), plantGrowth, plantVal,
                new Label("Herbivore Eat Rate"), herbEat, herbVal,
                new Label("Carnivore Eat Rate"), carnEat, carnVal,
                new Label("Herbivore Reproduction Threshold"), herbRepro, herbRepVal,
                new Label("Carnivore Reproduction Threshold"), carnRepro, carnRepVal,
                new Separator(),
                new Label("Initial Herbivores"), herbCount, herbCountVal,
                new Label("Initial Carnivores"), carnCount, carnCountVal,
                new Label("Initial Plants"), plantCount, plantCountVal
        );

        sliders.setStyle("-fx-padding:10; -fx-background-color:#f4f4f4;");

        Button start = new Button("▶ Start");
        Button pause = new Button("⏸ Pause");
        Button reset = new Button("🔄 Reset");

        HBox buttons = new HBox(10,start,pause,reset);
        VBox rightPanel = new VBox(10, sliders, buttons, stats);

        root.setCenter(gp);
        root.setRight(rightPanel);

        start.setOnAction(e -> startSim(speed, plantGrowth, herbEat, carnEat,
                herbRepro, carnRepro, herbCount, carnCount, plantCount));

        pause.setOnAction(e -> { if(timeline!=null) timeline.pause(); });
        reset.setOnAction(e -> resetSim());

        graph.show();

        stage.setScene(new Scene(root,900,600));
        stage.setTitle("Ecosystem Simulation FINAL");
        stage.show();
    }

    // PERFECT SPAWN
    private List<int[]> getShuffledEmptyCells(){
        List<int[]> empty = new ArrayList<>();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                if(grid.isEmpty(i,j)){
                    empty.add(new int[]{i,j});
                }
            }
        }
        Collections.shuffle(empty);
        return empty;
    }

    private void startSim(Slider speed,
                          Slider plantGrowth,
                          Slider herbEat,
                          Slider carnEat,
                          Slider herbRepro,
                          Slider carnRepro,
                          Slider herbCount,
                          Slider carnCount,
                          Slider plantCount){

        grid.getEntities().clear();
        graph.reset();
        graphCounter = 0;
        timeSteps = 0;

        List<int[]> emptyCells = getShuffledEmptyCells();
        int index = 0;

        for(int i=0;i<(int)herbCount.getValue() && index < emptyCells.size();i++){
            int[] p = emptyCells.get(index++);
            grid.addEntity(new Herbivore(p[0],p[1],12));
        }

        for(int i=0;i<(int)carnCount.getValue() && index < emptyCells.size();i++){
            int[] p = emptyCells.get(index++);
            grid.addEntity(new Carnivore(p[0],p[1],18));
        }

        for(int i=0;i<(int)plantCount.getValue() && index < emptyCells.size();i++){
            int[] p = emptyCells.get(index++);
            grid.addEntity(new Plant(p[0],p[1],grid.plantEnergy));
        }

        // ✅ SHOW INITIAL STATE FIRST
        updateUI();
        updateStats();

        timeline = new Timeline(new KeyFrame(Duration.millis(speed.getValue()), e -> {

            timeline.setRate(800.0 / speed.getValue());

            grid.plantGrowthRate = plantGrowth.getValue();
            grid.herbConsumeRate = herbEat.getValue();
            grid.carnConsumeRate = carnEat.getValue();
            grid.herbReproduceThreshold = (int)herbRepro.getValue();
            grid.carnReproduceThreshold = (int)carnRepro.getValue();

            grid.update();
            updateUI();
            updateStats();

            graphCounter++;
            if(graphCounter % 3 == 0){
                double avgEnergy = grid.getEntities()
                        .stream()
                        .filter(ent -> !(ent instanceof Plant))
                        .mapToDouble(ent -> ent.energy)
                        .average()
                        .orElse(0);

                graph.update(
                        (int)grid.countPlants(),
                        (int)grid.countHerb(),
                        (int)grid.countCarn(),
                        avgEnergy,
                        timeSteps   // 🔥 THIS MUST CHANGE EVERY STEP
                );

                  // 🔥 AFTER update
                }

            timeSteps++;

            boolean herbDead = grid.countHerb() == 0;
            boolean carnDead = grid.countCarn() == 0;

            if(herbDead || carnDead){
                timeline.stop();
                Platform.runLater(() -> {
                    showAlert("⚠ Ecosystem Collapsed after " + timeSteps + " steps");
                });
            }

        }));

        // ✅ DELAY FIX
        timeline.setDelay(Duration.millis(speed.getValue()));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void resetSim(){
        if(timeline!=null) timeline.stop();
        grid.getEntities().clear();
        graph.reset();
        updateUI();
        updateStats();
    }

    private void updateUI(){

        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                cells[i][j].setText("");
                cells[i][j].setStyle("-fx-border-color:gray;");
            }
        }

        for(Entity e: grid.getEntities()){
            Label cell = cells[e.getX()][e.getY()];

            if(e instanceof Plant){
                cell.setText("🌱");
                cell.setStyle("-fx-border-color:gray; -fx-background-color: lightgreen;");
            }
            else if(e instanceof Herbivore){
                cell.setText("🐄");
                cell.setStyle("-fx-border-color:gray; -fx-background-color: lightyellow;");
            }
            else if(e instanceof Carnivore){
                cell.setText("🐺");
                cell.setStyle("-fx-border-color:gray; -fx-background-color: pink;");
            }
        }
    }

    private void updateStats(){
        stats.setText(
                "Plants: "+grid.countPlants()+
                        " | Herb: "+grid.countHerb()+
                        " | Carn: "+grid.countCarn()+
                        "\nTime Steps: "+timeSteps
        );
    }

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simulation Status");
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args){
        launch();
    }
}