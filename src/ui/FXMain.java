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

        // ================= GRID =================
        GridPane gp = new GridPane();
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                Label l = new Label();
                l.setMinSize(35,35);
                l.setStyle("-fx-border-color:gray; -fx-alignment:center; -fx-font-size:18;");
                cells[i][j]=l;
                gp.add(l,j,i);
            }
        }

        // ================= SLIDERS =================

        Slider speed = new Slider(200,1500,800);
        Label speedVal = new Label("800");

        speed.valueProperty().addListener((o,a,b)->{
            speedVal.setText(String.valueOf(b.intValue()));
            if(timeline!=null){
                timeline.setRate(800.0 / b.doubleValue());
            }
        });

        Slider plantGrowth = new Slider(0.0,1.0,0.6);
        Label plantVal = new Label("0.6");
        plantGrowth.valueProperty().addListener((o,a,b)->{
            plantVal.setText(String.format("%.2f", b.doubleValue()));
        });

        Slider herbEat = new Slider(1,6,4);
        Label herbVal = new Label("4");
        herbEat.valueProperty().addListener((o,a,b)->{
            herbVal.setText(String.valueOf(b.intValue()));
        });

        Slider carnEat = new Slider(1,8,5);
        Label carnVal = new Label("5");
        carnEat.valueProperty().addListener((o,a,b)->{
            carnVal.setText(String.valueOf(b.intValue()));
        });

        Slider herbRepro = new Slider(8,25,14);
        Label herbRepVal = new Label("14");
        herbRepro.valueProperty().addListener((o,a,b)->{
            herbRepVal.setText(String.valueOf(b.intValue()));
        });

        Slider carnRepro = new Slider(12,35,20);
        Label carnRepVal = new Label("20");
        carnRepro.valueProperty().addListener((o,a,b)->{
            carnRepVal.setText(String.valueOf(b.intValue()));
        });

        // 🔥 NEW: INITIAL POPULATION SLIDERS
        Slider herbCount = new Slider(0,50,15);
        Label herbCountVal = new Label("15");
        herbCount.valueProperty().addListener((o,a,b)->{
            herbCountVal.setText(String.valueOf(b.intValue()));
        });

        Slider carnCount = new Slider(0,30,6);
        Label carnCountVal = new Label("6");
        carnCount.valueProperty().addListener((o,a,b)->{
            carnCountVal.setText(String.valueOf(b.intValue()));
        });

        Slider plantCount = new Slider(0,100,25);
        Label plantCountVal = new Label("25");
        plantCount.valueProperty().addListener((o,a,b)->{
            plantCountVal.setText(String.valueOf(b.intValue()));
        });

        VBox sliders = new VBox(8,
                new Label("Speed"), speed, speedVal,
                new Label("Plant Growth"), plantGrowth, plantVal,
                new Label("Herb Eat Rate"), herbEat, herbVal,
                new Label("Carn Eat Rate"), carnEat, carnVal,
                new Label("Herb Reproduction"), herbRepro, herbRepVal,
                new Label("Carn Reproduction"), carnRepro, carnRepVal,
                new Separator(),
                new Label("Initial Herbivores"), herbCount, herbCountVal,
                new Label("Initial Carnivores"), carnCount, carnCountVal,
                new Label("Initial Plants"), plantCount, plantCountVal
        );

        sliders.setStyle("-fx-padding:10; -fx-background-color:#f4f4f4;");

        // ================= BUTTONS =================
        Button start = new Button("▶ Start");
        Button pause = new Button("⏸ Pause");
        Button reset = new Button("🔄 Reset");

        HBox buttons = new HBox(10,start,pause,reset);

        VBox rightPanel = new VBox(10, sliders, buttons, stats);

        root.setCenter(gp);
        root.setRight(rightPanel);

        // ================= ACTIONS =================

        start.setOnAction(e -> startSim(speed, plantGrowth, herbEat, carnEat,
                herbRepro, carnRepro, herbCount, carnCount, plantCount));

        pause.setOnAction(e -> {
            if(timeline!=null) timeline.pause();
        });

        reset.setOnAction(e -> resetSim());

        graph.show();

        stage.setScene(new Scene(root,900,600));
        stage.setTitle("Ecosystem Simulation FINAL");
        stage.show();
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

        // APPLY SETTINGS
        grid.plantGrowthRate = plantGrowth.getValue();
        grid.herbConsumeRate = herbEat.getValue();
        grid.carnConsumeRate = carnEat.getValue();
        grid.herbReproduceThreshold = (int)herbRepro.getValue();
        grid.carnReproduceThreshold = (int)carnRepro.getValue();

        // SPAWN ENTITIES
        for(int i=0;i<(int)herbCount.getValue();i++)
            grid.addEntity(new Herbivore(rand(),rand(),12));

        for(int i=0;i<(int)carnCount.getValue();i++)
            grid.addEntity(new Carnivore(rand(),rand(),18));

        for(int i=0;i<(int)plantCount.getValue();i++)
            grid.addEntity(new Plant(rand(),rand(),grid.plantEnergy));

        timeline = new Timeline(new KeyFrame(Duration.millis(speed.getValue()), e->{

            grid.update();
            updateUI();
            updateStats();

            graphCounter++;
            if(graphCounter % 3 == 0){
                graph.update(
                        (int)grid.countPlants(),
                        (int)grid.countHerb(),
                        (int)grid.countCarn()
                );
            }

            timeSteps++;

            // COLLAPSE CHECK
            if(grid.countHerb()==0 || grid.countCarn()==0){
                timeline.stop();
                Platform.runLater(() -> {
                    showAlert("⚠ Ecosystem Collapsed after " + timeSteps + " steps");
                });
            }

        }));

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

    private int rand(){
        return (int)(Math.random()*15);
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
                cell.setStyle("-fx-background-color: lightgreen;");
            }
            else if(e instanceof Herbivore){
                cell.setText("🐄");
                cell.setStyle("-fx-background-color: lightblue;");
            }
            else if(e instanceof Carnivore){
                cell.setText("🐺");
                cell.setStyle("-fx-background-color: pink;");
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