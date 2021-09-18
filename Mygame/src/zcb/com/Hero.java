package zcb.com;

import java.awt.event.KeyAdapter;
import java.awt.image.BufferedImage;

/**
 * 本机类
 */
public class Hero extends FlyingObject {
    private int life;//生命值
    private int doubleFire;//火力值
    private BufferedImage[] images;
    private int index = 0; //负责图片切换

    public Hero(){
        image = run_game.hero0;
        width = image.getWidth();
        height = image.getHeight();
        x = 150;
        y = 400;
        life = 3;
        doubleFire = 0;
        images = new BufferedImage[]{run_game.hero0,run_game.hero1};
        index = 0;
    }

    //因为英雄机随鼠标移动，所以要实时改变对象的x和y
    public void chenge(int x,int y){
        this.x = x-width/2;
        this.y = y-height/2;
    }

    //这个是英雄机实现图片切换
    @Override
    public void step() {
        if (index==0){
            index = 1;
        }else {
            index = 0;
        }
        this.image=images[index];
    }

    //创建产生子弹的方法
    public Bullet[] pellet(){
        int a = width/4;
        int b = 10;
        if (doubleFire>0){
            Bullet[] bs =new Bullet[2];
            bs[0] = new Bullet(this.x+a-4,this.y-10);
            bs[1] = new Bullet(this.x+3*a-4,this.y-10);
            return bs;
        }
        else {
            Bullet[] bs =new Bullet[1];
            bs[0]= new Bullet(this.x+2*a-4,this.y-10);
            return bs;
        }
    }

    public void addLife(){
        life++;
    }  //加生命
    public void addDoubleFire(){
        doubleFire++;
    }//加火力
    public void reduceLife(){life--;}//
    public void cleanDoubleFire(){doubleFire=0;}
    public int Life_print(){
        return life;
    }
    public void change_speed(int level){}
}
