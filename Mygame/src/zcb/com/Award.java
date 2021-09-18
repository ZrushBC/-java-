package zcb.com;
//封装小蜜蜂奖励类型的接口
public interface Award {
    public int DOOUBLE_Fire=0;
    public int LIFE=1;
    //获取奖励类型
    public int getType();
}