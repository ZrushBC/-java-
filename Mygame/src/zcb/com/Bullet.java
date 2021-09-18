package zcb.com;

import java.util.Random;

/**
 * 子弹类
 */
public class Bullet extends FlyingObject {
    private int Bullet_speed = 6;
    public Bullet(int x,int y){
        image = run_game.bullet;
        width = image.getWidth();
        height = image.getHeight();
        this.x = x;
        this.y = y;
    }

    @Override
    public void step() {
        y-=Bullet_speed;
    }
    public void change_speed(int level){}
}
