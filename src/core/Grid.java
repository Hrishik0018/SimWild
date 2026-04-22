package core;

import model.*;
import java.util.*;

public class Grid {

    private int rows, cols;
    private List<Entity> entities = new ArrayList<>();

    public int plantEnergy = 6;
    public double plantGrowthRate = 0.6;

    public double herbConsumeRate = 4;
    public double carnConsumeRate = 5;

    public int herbReproduceThreshold = 14;
    public int carnReproduceThreshold = 20;

    public Grid(int r, int c){
        rows = r;
        cols = c;
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }

    public void addEntity(Entity e){ entities.add(e); }
    public void removeEntity(Entity e){ entities.remove(e); }

    public boolean isEmpty(int x,int y){
        for(Entity e:entities)
            if(e.getX()==x && e.getY()==y) return false;
        return true;
    }

    public Entity getEntity(int x,int y){
        for(Entity e:entities)
            if(e.getX()==x && e.getY()==y) return e;
        return null;
    }

    public List<Entity> getEntities(){ return entities; }

    public void update(){
        List<Entity> copy = new ArrayList<>(entities);

        for(Entity e: copy){
            e.act(this);
        }

        // plant growth
        if(Math.random() < plantGrowthRate){
            int x = (int)(Math.random()*rows);
            int y = (int)(Math.random()*cols);

            if(isEmpty(x,y)){
                addEntity(new Plant(x,y,plantEnergy));
            }
        }
    }

    public long countPlants(){ return entities.stream().filter(e->e instanceof Plant).count(); }
    public long countHerb(){ return entities.stream().filter(e->e instanceof Herbivore).count(); }
    public long countCarn(){ return entities.stream().filter(e->e instanceof Carnivore).count(); }
}