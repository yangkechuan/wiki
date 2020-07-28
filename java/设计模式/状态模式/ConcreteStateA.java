package example;


public class ConcreteStateA extends State{

    private static final String FLAG = "ConcreteStateA";

    @Override
    public void handle(Context context) {
        // 设置下一个节点
        context.setState(new ConcreteStateB());
    }

    @Override
    public String toString() {
        return FLAG;
    }
}
