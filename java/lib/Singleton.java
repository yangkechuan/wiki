package com.lib;

/**
 * Created by IntelliJ IDEA.
 * User    : yangkechuan
 * Date    : 2018/7/20
 * Time    : 11:58
 */
public class Singleton {

    private Singleton(){}

    /** volatile 内存可见性 **/
    private static volatile Singleton instance = null;

    /** 双重校验锁 **/
    private static Singleton getInstance(){
        if (instance == null){
            synchronized (Singleton.class){
                if (instance == null){
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
    }
}
