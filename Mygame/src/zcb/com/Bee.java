package zcb.com;

import java.util.Random;

/**
 * 蜜蜂类
 */
public class Bee extends FlyingObject implements Award{
    private int Bee_xspeed = 1;
    private int Bee_yspeed = 1;
    private int awardType;// 获取类型
    public Bee(){
        image = run_game.bee;
        width = image.getWidth();
        height = image.getHeight();
        Random r =new Random();
        x = r.nextInt( run_game.WIDTH-this.width);
        y = -this.height;
        awardType =r.nextInt(2);
    }
    @Override
    public int getType() {
        return awardType;
    }

    @Override
    public void step() {
        if (x>=run_game.WIDTH-this.width) {
            Bee_xspeed=-Bee_xspeed;
        }
        if (x<=0){
            Bee_xspeed=-Bee_xspeed;
        }
        y+=Bee_yspeed;
        x+=Bee_xspeed;
    }

    public void change_speed(int level){
        if (Bee_xspeed>0){
            Bee_xspeed=level;
        }else {
            Bee_xspeed=-level;
        }
        Bee_yspeed=level;
    }
}
