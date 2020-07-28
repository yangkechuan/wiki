package example;

/**
 * 抽象状态类
 */
public abstract class State {

    /**
     * 设置节点
     * @param context context
     */
    public abstract void handle(Context context);
}
