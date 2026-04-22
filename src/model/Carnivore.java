package model;

import core.Grid;

public class Carnivore extends Entity {

    public Carnivore(int x,int y,int energy){
        super(x,y,energy);
    }

    @Override
    public void act(Grid grid){

        move(grid);

        outer:
        for(int dx=-1; dx<=1; dx++){
            for(int dy=-1; dy<=1; dy++){

                int nx = x + dx;
                int ny = y + dy;

                if(nx>=0 && ny>=0 && nx<grid.getRows() && ny<grid.getCols()){

                    Entity e = grid.getEntity(nx, ny);

                    if(e instanceof Herbivore){
                        energy += grid.carnConsumeRate;
                        grid.removeEntity(e);
                        break outer;
                    }
                }
            }
        }

        if(energy >= grid.carnReproduceThreshold){
            reproduce(grid);
        }

        energy--;

        if(energy <= 0){
            grid.removeEntity(this);
        }
    }

    private void reproduce(Grid grid){

        if(Math.random() > 0.3) return;

        int nx = x + (int)(Math.random()*3)-1;
        int ny = y + (int)(Math.random()*3)-1;

        if(nx>=0 && ny>=0 && nx<grid.getRows() && ny<grid.getCols()){
            if(grid.isEmpty(nx,ny)){
                grid.addEntity(new Carnivore(nx,ny,energy/2));
                energy/=2;
            }
        }
    }
}