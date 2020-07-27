package example;

/**
 * 单例模式
 */
public class Singleton {

    private static volatile Singleton instange = null;

    private Singleton(){}

    private static Singleton getInstance(){
        if (instange == null){
            synchronized (Singleton.class){
                if (instange == null){
                    instange = new Singleton();
                }
            }
        }
        return instange;
    }

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
    }
}
