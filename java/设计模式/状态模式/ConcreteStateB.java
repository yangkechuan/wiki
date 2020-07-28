package example;


public class ConcreteStateB extends State{

    private static final String FLAG = "ConcreteStateB";

    @Override
    public void handle(Context context) {
        // 设置下一个节点
        context.setState(new ConcreteStateA());
    }

    @Override
    public String toString() {
        return FLAG;
    }
}
