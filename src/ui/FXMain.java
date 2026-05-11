package ui;

import core.Grid;
import javafx.geometry.Pos;
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
    private GraphFX graph = new GraphFX();
    private int timeSteps = 0;
    private boolean isRunning = false;
    private TextArea logArea = new TextArea();

    @Override
    public void start(Stage stage){
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1e1e1e;");
        GridPane gp = new GridPane();

        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                Label l = new Label();
                l.setMinSize(35,35);
                l.setStyle("-fx-border-color:#5c4033; -fx-background-color:#8b5a2b; -fx-alignment:center;");
                cells[i][j]=l;
                gp.add(l,j,i);
            }
        }

        logArea.setEditable(false);
        logArea.setPrefHeight(120);
        logArea.setStyle("-fx-control-inner-background:#000; -fx-text-fill:#00ffcc;");
        root.setBottom(logArea);

        Label stats = new Label();
        stats.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        HBox top = new HBox(stats);
        top.setAlignment(Pos.CENTER_RIGHT);
        top.setStyle("-fx-padding:10;");
        root.setTop(top);

        Slider speed = new Slider(200,1500,800);
        Slider plantGrowth = new Slider(0.0,1.0,0.6);
        Slider herbEat = new Slider(1,6,4);
        Slider carnEat = new Slider(1,8,5);
        Slider herbRepro = new Slider(8,25,14);
        Slider carnRepro = new Slider(12,35,20);

        // INITIAL POPULATION SLIDERS
        Slider plantInit = new Slider(0,50,25);
        Slider herbInit = new Slider(0,30,15);
        Slider carnInit = new Slider(0,20,6);

        Label speedVal = new Label("800");
        Label plantVal = new Label("0");
        Label herbVal = new Label("4");
        Label carnVal = new Label("5");
        Label herbRepVal = new Label("14");
        Label carnRepVal = new Label("20");

        // LABELS
        Label plantInitVal = new Label("25");
        Label herbInitVal = new Label("15");
        Label carnInitVal = new Label("6");

        speed.valueProperty().addListener((o,a,b)-> speedVal.setText(""+b.intValue()));
        plantGrowth.valueProperty().addListener((o,a,b)-> plantVal.setText(""+(int)(b.doubleValue()*10)));
        herbEat.valueProperty().addListener((o,a,b)-> herbVal.setText(""+b.intValue()));
        carnEat.valueProperty().addListener((o,a,b)-> carnVal.setText(""+b.intValue()));
        herbRepro.valueProperty().addListener((o,a,b)-> herbRepVal.setText(""+b.intValue()));
        carnRepro.valueProperty().addListener((o,a,b)-> carnRepVal.setText(""+b.intValue()));


        plantInit.valueProperty().addListener((o,a,b)-> plantInitVal.setText(""+b.intValue()));
        herbInit.valueProperty().addListener((o,a,b)-> herbInitVal.setText(""+b.intValue()));
        carnInit.valueProperty().addListener((o,a,b)-> carnInitVal.setText(""+b.intValue()));

        Button sMinus = new Button("-");
        Button sPlus = new Button("+");
        sMinus.setOnAction(e -> speed.setValue(speed.getValue()-100));
        sPlus.setOnAction(e -> speed.setValue(speed.getValue()+100));

        Button hMinus = new Button("-");
        Button hPlus = new Button("+");
        hMinus.setOnAction(e -> herbEat.setValue(herbEat.getValue()-1));
        hPlus.setOnAction(e -> herbEat.setValue(herbEat.getValue()+1));

        Button cMinus = new Button("-");
        Button cPlus = new Button("+");
        cMinus.setOnAction(e -> carnEat.setValue(carnEat.getValue()-1));
        cPlus.setOnAction(e -> carnEat.setValue(carnEat.getValue()+1));

        Button addPlant = new Button("+ Plant");
        Button addHerb = new Button("+ Herbivore");
        Button addCarn = new Button("+ Carnivore");

        addPlant.setOnAction(e -> spawn(new Plant(rand(),rand(),grid.plantEnergy)));
        addHerb.setOnAction(e -> spawn(new Herbivore(rand(),rand(),12)));
        addCarn.setOnAction(e -> spawn(new Carnivore(rand(),rand(),18)));

        Button start = new Button("▶ Start");
        Button pause = new Button("⏸ Pause");
        Button resume = new Button("⏵ Resume");
        Button reset = new Button("🔄 Reset");

        start.setOnAction(e -> startSim(speed, plantGrowth, herbEat, carnEat, herbRepro, carnRepro,
                plantInit, herbInit, carnInit));

        pause.setOnAction(e -> { if(timeline!=null) timeline.pause(); });
        resume.setOnAction(e -> { if(timeline!=null) timeline.play(); });
        reset.setOnAction(e -> resetSim());

        VBox controls = new VBox(10,
                new Label("Speed of Simulation"), new HBox(5,sMinus,speed,sPlus,speedVal),
                new Label("Plant Growth Rate"), plantGrowth, plantVal,
                new Label("Herbivore Eating Rate"), new HBox(5,hMinus,herbEat,hPlus,herbVal),
                new Label("Carnivore Eating Rate"), new HBox(5,cMinus,carnEat,cPlus,carnVal),
                new Label("Herbivore Reproduction Threshold"), herbRepro, herbRepVal,
                new Label("Carnivore Reproduction Threshold"), carnRepro, carnRepVal,


                new Separator(),
                new Label("Initial Plants"), plantInit, plantInitVal,
                new Label("Initial Herbivores"), herbInit, herbInitVal,
                new Label("Initial Carnivores"), carnInit, carnInitVal,

                new Separator(),
                addPlant, addHerb, addCarn,
                new Separator(),
                start, pause, resume, reset
        );

        controls.setStyle("-fx-padding:10; -fx-background-color:#ffffff;");

        root.setCenter(gp);
        ScrollPane scroll = new ScrollPane(controls);
        scroll.setFitToWidth(true);

        root.setRight(scroll);
        scroll.setPrefWidth(280);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        graph.show();

        stage.setScene(new Scene(root,1000,700));
        stage.setTitle("Ecosystem Simulation PRO");
        stage.show();

        Timeline statsUpdater = new Timeline(new KeyFrame(Duration.millis(300), e -> {
            stats.setText(
                    "🌱 " + grid.countPlants() +
                            " | 🐄 " + grid.countHerb() +
                            " | 🐺 " + grid.countCarn() +
                            " | Steps: " + timeSteps
            );
        }));
        statsUpdater.setCycleCount(Animation.INDEFINITE);
        statsUpdater.play();
    }

    private void startSim(Slider speed, Slider plantGrowth, Slider herbEat, Slider carnEat,
                          Slider herbRepro, Slider carnRepro,
                          Slider plantInit, Slider herbInit, Slider carnInit){

        grid.getEntities().clear();
        graph.reset();
        timeSteps = 0;

        int pCount = (int) plantInit.getValue();
        int hCount = (int) herbInit.getValue();
        int cCount = (int) carnInit.getValue();

        if(pCount + hCount + cCount > 225){
            showAlert("Too many entities! Max = 225");
            return;
        }

        for(int i=0;i<hCount;i++)
            grid.addEntity(new Herbivore(rand(),rand(),12));

        for(int i=0;i<cCount;i++)
            grid.addEntity(new Carnivore(rand(),rand(),18));

        for(int i=0;i<pCount;i++)
            grid.addEntity(new Plant(rand(),rand(),grid.plantEnergy));

        updateUI();

        timeline = new Timeline(new KeyFrame(Duration.millis(speed.getValue()), e -> {

            grid.plantGrowthRate = plantGrowth.getValue();
            grid.herbConsumeRate = herbEat.getValue();
            grid.carnConsumeRate = carnEat.getValue();
            grid.herbReproduceThreshold = (int)herbRepro.getValue();
            grid.carnReproduceThreshold = (int)carnRepro.getValue();

            grid.update();
            timeSteps++;

            updateUI();

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
                    timeSteps
            );

            log("Step " + timeSteps + " running...");
            log("Plants: "+grid.countPlants()+" Herb: "+grid.countHerb()+" Carn: "+grid.countCarn());

            if(grid.countHerb()==0 || grid.countCarn()==0){
                timeline.stop();
                log("⚠ Ecosystem collapsed!");
                Platform.runLater(() -> showAlert(
                        "⚠ Ecosystem Collapsed!\n\n" +
                                "Plants: " + grid.countPlants() + "\n" +
                                "Herbivores: " + grid.countHerb() + "\n" +
                                "Carnivores: " + grid.countCarn()
                ));
            }

        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void log(String msg){
        Platform.runLater(() -> logArea.appendText(msg+"\n"));
    }

    private void spawn(Entity e){
        if(grid.isEmpty(e.getX(), e.getY())){
            grid.addEntity(e);
            updateUI();
        }
    }

    private int rand(){
        return (int)(Math.random()*15);
    }

    private void resetSim(){
        if(timeline!=null) timeline.stop();
        grid.getEntities().clear();
        graph.reset();
        timeSteps = 0;
        updateUI();
    }

    private void updateUI(){
        for(int i=0;i<15;i++){
            for(int j=0;j<15;j++){
                cells[i][j].setText("");
                cells[i][j].setStyle("-fx-border-color:#5c4033; -fx-background-color:#8b5a2b;");
            }
        }

        for(Entity e: grid.getEntities()){
            Label cell = cells[e.getX()][e.getY()];

            if(e instanceof Plant){
                cell.setText("🌱");
                cell.setStyle("-fx-background-color:#4CAF50;");
            }
            else if(e instanceof Herbivore){
                cell.setText("🐄");
                cell.setStyle("-fx-background-color:#FFC107;");
            }
            else if(e instanceof Carnivore){
                cell.setText("🐺");
                cell.setStyle("-fx-background-color:#F44336;");
            }
        }
    }

    private void showAlert(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simulation Alert");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args){
        launch();
    }
}