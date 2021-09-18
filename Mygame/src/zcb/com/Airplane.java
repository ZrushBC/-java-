package zcb.com;

import java.util.Random;

/**
 * 敌机
 */
public class Airplane extends FlyingObject implements Enemy {
    private int Airplane_speed = 1;
    public Airplane(){
        image = run_game.airplane;
        width = image.getWidth();
        height = image.getHeight();
        Random r =new Random();
        x = r.nextInt( run_game.WIDTH-this.width);
        y = -this.height;
    }
    @Override
    public int getScore() {
        return 5;
    }

    @Override
    public void step() {
        y += Airplane_speed;
    }

    public void change_speed(int level){
        Airplane_speed =level;
    }
}
