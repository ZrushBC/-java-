package zcb.com;

import sun.plugin2.message.MarkTaintedMessage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class run_game extends JPanel{
    //游戏界面宽高
    public static final int WIDTH = 400;
    public static final int HEIGHT = 700;
    //状态设置
    public static final int START=0; //开始界面
    public static final int RUNING=1; //运行界面
    public static final int PAUSE=2; //暂停界面
    public static final int GAME_OVER=3; //游戏结束界面
    public static final int RANK=4; //历史记录
    public static final int READY=5;//游戏准备
    private int Mark=0;//状态初始值
    //游戏图片素材
    public static BufferedImage background;
    public static BufferedImage rank_background;
    public static BufferedImage start;
    public static BufferedImage pause;
    public static BufferedImage gameover;
    public static BufferedImage airplane;
    public static BufferedImage bee;
    public static BufferedImage bullet;
    public static BufferedImage hero0;
    public static BufferedImage hero1;
    //new一些对象
    private Hero hero = new Hero();
    private FlyingObject[] flys = {};
    private Bullet[] bullets = {};
    private int Score = 0;  //描述分数
    private int old_s = 0; //旧分数
    private int level=1; //难度等级
    private int time_mark=40;//敌人生成时间标记
    private int ready_key=0;//弹窗显示标记
    private String Name="不知名玩家";

    //静态块，给静态变量复制
    static{
        try{
            background = ImageIO.read(run_game.class.getResource("background.png"));
            rank_background = ImageIO.read(run_game.class.getResource("rank_background.png"));
            airplane = ImageIO.read(run_game.class.getResource("airplane.png"));
            background = ImageIO.read(run_game.class.getResource("background.png"));
            bee = ImageIO.read(run_game.class.getResource("bee.png"));
            bullet = ImageIO.read(run_game.class.getResource("bullet.png"));
            gameover = ImageIO.read(run_game.class.getResource("gameover.png"));
            hero0 = ImageIO.read(run_game.class.getResource("hero0.png"));
            hero1 = ImageIO.read(run_game.class.getResource("hero1.png"));
            pause = ImageIO.read(run_game.class.getResource("pause.png"));
            start = ImageIO.read(run_game.class.getResource("start1.png"));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("图片路径错误");
        }
    }

    //将子弹群放入bullets数组
    int bullets_index = 0;
    public void enterAction_bullets(){
        bullets_index++;
        if(bullets_index%30==0){
            Bullet[] bs = hero.pellet();
            bullets = Arrays.copyOf(bullets,bullets.length+bs.length);
            if (bs.length>1){
                bullets[bullets.length-1] = bs[0];
                bullets[bullets.length-2] = bs[1];
            }
            else {
                bullets[bullets.length-1] = bs[0];
            }
            bullets_index = 0;
        }
    }

    //创建一个生成大量敌机和小蜜蜂对象的方法
    public FlyingObject enemy(){
        Random r = new Random();
        int i = r.nextInt(20);
        if (i == 10){
            return new Bee();
        }else {
            return new Airplane();
        }
    }

    //将生成的敌机和小蜜蜂对象放入flys数组中
    int flys_index = 0;
    public void enterAction_flys(){
        flys_index++;
        if (flys_index % time_mark ==0){ //每隔400毫秒添加一个对象
            flys = Arrays.copyOf(flys,flys.length+1);
            flys[flys.length-1] = enemy();
            flys_index = 0;
        }
        for (int i=0;i<flys.length;i++){
            flys[i].change_speed(level);
        }
    }

    //封装一个各个对象行动的方法
    public void step_action(){
        hero.step();
        for (int i=0;i<bullets.length;i++){
            bullets[i].step();
        }
        for (int i=0;i<flys.length;i++){
            flys[i].step();
        }
    }

    //创建一个删除越界的飞行物的方法
    public void Object_delet(){
        FlyingObject[] flys_live = new FlyingObject[flys.length];
        int flysLive_index=0;
        //遍历flys数组
        for(int i=0;i<flys.length;i++){
            if(flys[i].y<=HEIGHT){
                flys_live[flysLive_index] = flys[i];
                flysLive_index++;
            }
        }
        flys = Arrays.copyOf(flys_live,flysLive_index);
        Bullet[] bullets_live = new Bullet[bullets.length];
        int bulletsLive_index = 0;
        //遍历子弹
        for (int i=0;i<bullets.length;i++){
            if(bullets[i].y>=0){
                bullets_live[bulletsLive_index] = bullets[i];
                bulletsLive_index++;
            }
        }
        bullets =Arrays.copyOf(bullets_live,bulletsLive_index);
    }

    //创建一个子弹碰撞消失以及奖励的方法
    public void bullet_hit_delet() {
        for (int i = 0; i < bullets.length; i++) {
            for (int j = 0; j < flys.length; j++) {
                if (flys[j].check_hit(bullets[i])) {
                    //碰撞奖励
                    if (flys[j].image == airplane) {
                        Score+=((Enemy) flys[j]).getScore();
                    }
                    if (flys[j].image==bee){
                        int type = ((Award) flys[j]).getType();
                        switch (type){
                            case 1:hero.addLife();break;
                            case 0:hero.addDoubleFire();break;
                        }
                    }
                    //写删除对象
                    flys[j] = flys[flys.length - 1];
                    bullets[i] = bullets[bullets.length - 1];
                    flys = Arrays.copyOf(flys, flys.length - 1);
                    bullets = Arrays.copyOf(bullets, bullets.length - 1);
                    break;
                }
            }
        }
    }

    //创建一个检查结束游戏的方法
    public void check_gimeover(){
        if (hero_hit_punish()){
            Mark = GAME_OVER;
        }
    }

    //创建英雄机和敌机碰撞以及惩罚的方法
    public boolean hero_hit_punish(){
        for (int i=0;i<flys.length;i++){
            if (flys[i].checkHero_hit(hero)){
                //碰撞惩罚
                //System.out.println(hero.Life_print());
                hero.reduceLife();
                hero.cleanDoubleFire();
                //删除碰撞对象
                flys[i]=flys[flys.length-1];
                flys = Arrays.copyOf(flys, flys.length - 1);
            }
        }
        return hero.Life_print()<=0;
    }

    //检查分数并增加难度
    public void check_score(){
        if(Score-old_s>100){
            old_s=Score;
            level*=2;
            if (time_mark>20){
                time_mark-=5;
            }
            for (int i=0;i<flys.length;i++){
                flys[i].change_speed(level);
            }
        }
    }

    //通过鼠标点击位置来判断开始游戏或者历史记录
    public int check_start(int x,int y){
        if (x>85&&x<305){
            if (y>315&&y<395){
                return READY;
            }else if (y>395&&y<485){
                return RANK;
            }
        }
        return START;
    }

    //排名输出
    public void file_print(){
        try{
            OutputStream t = new FileOutputStream("E:/Myjava/Mygame/src/zcb/com/rank.txt",true);
            OutputStreamWriter writer = new OutputStreamWriter(t);
            BufferedWriter buffwriter = new BufferedWriter(writer);
            buffwriter.write(Name+"    "+Score+"\n");
            buffwriter.close();
        }catch (Exception e){
            System.out.println("文件打开失败");
        }
    }

    //添加定时器，并编写定时器任务（让大量生成的敌人对象动起来）
    public void action(){
        //鼠标事件
        MouseAdapter l =new MouseAdapter() {
            //鼠标监控事件
            public void mouseMoved(MouseEvent e){
                if (Mark==RUNING){
                    int x = e.getX();
                    int y = e.getY();
                    hero.chenge(x,y);
                }
            } //获取鼠标坐标
            //添加鼠标单击事件
            public void mouseClicked(MouseEvent e) {
                int y=e.getY();
                int x=e.getX();
                switch (Mark){
                    case START: Mark=check_start(x,y);break;
                    case READY: Mark=RUNING;break;
                    case RANK: Mark=START;break;
                    case RUNING: Mark=PAUSE;break;
                    case PAUSE: Mark=RUNING;break;
                    case GAME_OVER:
                        file_print();
                        Score=0;
                        Name="不知名玩家";
                        ready_key=0;
                        level=1;
                        hero = new Hero();
                        flys = new FlyingObject[0];
                        bullets = new Bullet[0];
                        Mark=START;break;
                }
            }
            //鼠标移入
            public void mouseEntered(MouseEvent e) {
                if (Mark==PAUSE){
                    Mark = RUNING;
                }
            }
            //鼠标移出
            public void mouseExited(MouseEvent e) {
                if (Mark==RUNING){
                    Mark=PAUSE;
                }
            }

        };
        this.addMouseListener(l); //处理鼠标点击操作
        this.addMouseMotionListener(l); //处理鼠标滑动操作
        Timer timer = new Timer();//启动一个定时器
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Mark==RUNING){
                    enterAction_flys(); // 批量生产敌人对象
                    enterAction_bullets();//批量产生子弹对象
                    step_action();  //对象行动的方法
                    Object_delet();//删除越界对象
                    bullet_hit_delet();//删除子弹碰撞对象以及奖励
                    check_score();  //难度增加机制判断
                    check_gimeover();//删除英雄机碰撞对象以及惩罚
                }
                repaint(); //重新绘制
            }
        }, 10, 10);
    }

    //历史记录打印
    public void draw_rank(Graphics g){
        int x=10;
        int y=95;
        Font f = new Font(Font.DIALOG,Font.BOLD,40);
        g.setFont(f);
        g.drawString("历史记录",100,40);
        f = new Font(Font.DIALOG,Font.BOLD,25);
        g.setFont(f);
        g.drawString("姓名",10,85);
        g.drawString("分数",100,85);
        f = new Font(Font.DIALOG,Font.BOLD,15);
        g.setFont(f);
        try{
            InputStream t = new FileInputStream("E:/Myjava/Mygame/src/zcb/com/rank.txt");
            InputStreamReader reader = new InputStreamReader(t);
            BufferedReader buffReader = new BufferedReader(reader);
            String strTmp = "";
            while((strTmp = buffReader.readLine())!=null){
                y+=25;
                g.drawString(strTmp,x,y);
            }
            buffReader.close();
        }catch (Exception e){
            System.out.println("文件打开失败");
        }
    }

    //重写画板中的paint方法
    public void paint(Graphics g) {
        //画背景图
        g.drawImage(background,0,0,null);
        //画英雄机
        g.drawImage(hero.image,hero.x,hero.y,null);
        //画敌人
        for (int i=0;i<flys.length;i++){
            g.drawImage(flys[i].image,flys[i].x,flys[i].y,null);
        }
        //画子弹
        for (int i=0;i<bullets.length;i++){
            g.drawImage(bullets[i].image,bullets[i].x,bullets[i].y,null);
        }
        //画分数，画生命
        Font f = new Font(Font.DIALOG,Font.BOLD,25);
        g.setFont(f);
        g.drawString("分数："+Score,10,25);
        g.drawString("生命："+hero.Life_print(),10,60);
        //画状态
        switch(Mark){
            case START: g.drawImage(start,0,0,null);break;
            case PAUSE: g.drawImage(pause,0,0,null);break;
            case READY:
                g.drawImage(rank_background,0,0,null);
                g.drawString("请在弹窗输入姓名",10,150);
                g.drawString("如果不愿意",10,200);
                g.drawString("可直接点击确定",10,250);
                g.drawString("准备就绪，请点击屏幕开始",10,350);
                if (ready_key==0){
                    ready_key=1;
                    ready();
                }
                break;
            case GAME_OVER: g.drawImage(gameover,0,0,null);break;
            case RANK: g.drawImage(rank_background,0,0,null);draw_rank(g);break;
        }
    }

    //主函数
    public static void main(String[] args) {
        JFrame jf = new JFrame("fly");//创建了一个窗体
        run_game rg= new run_game();//创建面板
        //将面板加载到窗体上
        jf.add(rg);
        //设置窗体大小
        jf.setSize(WIDTH,HEIGHT);
        //设置窗体关闭按钮
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //设置窗体在当前系统显示的时候，总是在最上面
        jf.setAlwaysOnTop(true);
        //设置窗体初始位置
        jf.setLocationRelativeTo(null);
        //显示设置
        jf.setVisible(true);//去调用画板上的paint（）方法
        rg.action();
    }

    //玩家信息录入弹窗
    public void  ready() {

        // 创建 JFrame 实例
        JFrame frame = new JFrame("玩家信息");
        // 设置子窗口的大小
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);


        // 设置界面可见
        frame.setVisible(true);
        /*
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        //提示文字
        JLabel userLabel = new JLabel("玩家姓名:");
        //提示文字的设置，坐标,文字区域的宽高
        userLabel.setBounds(100,10,100,25);
        userLabel.setFont(new Font(null,Font.PLAIN,20));
        panel.add(userLabel);

        //创建文本域用于用户输入,指定可见列数为10列
        JTextField userText = new JTextField(10);
        //设置字体
        userText.setFont(new Font(null,Font.PLAIN,20));
        //设置按钮大小，坐标
        userText.setBounds(100,40,130,35);
        panel.add(userText);

        //创建一个按钮，点击后获取文本框的文本
        JButton btn =new JButton("确定");
        //设置字体
        btn.setFont(new Font(null,Font.PLAIN,20));
        //设置按钮大小，坐标
        btn.setBounds(100,90,130,40);
        panel.add(btn);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Name =userText.getText();
                if (Name.length()==0){
                    Name="不知名玩家";
                }
                frame.dispose();
            }
        });
    }

}
