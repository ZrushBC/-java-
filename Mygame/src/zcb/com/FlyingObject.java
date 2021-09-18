package zcb.com;

import java.awt.image.BufferedImage;

/**
 * 飞行物类，一个父类
 */
public abstract class FlyingObject {
    protected BufferedImage image;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    //封装一个飞行物行动的方法
    public abstract void step();
    public abstract void change_speed(int level);

    //检查子弹碰撞
    public boolean check_hit(Bullet b){
        int x1=this.x;
        int x2=this.x+this.width;
        int y1=this.y;
        int y2=this.y+this.height;
        int x=b.x;
        int y=b.y;
        return x>x1 && x<x2 && y>y1 && y<y2;
    }

    //检查英雄机碰撞
    public boolean checkHero_hit(Hero b){
        int x1=this.x-b.width/2;
        int x2=this.x+this.width+b.width/2;
        int y1=this.y-b.height/2;
        int y2=this.y+this.height+b.width/2;
        int x=b.x+b.width/2;
        int y=b.y+b.height/2;
        return x>x1 && x<x2 && y>y1 && y<y2;
    }
}
