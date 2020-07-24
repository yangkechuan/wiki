package example;


public class Test {

    public static void main(String[] args) {
        AbstractBatterCake batterCake = new BatterCake();
        batterCake = new EggDecorator(batterCake);
        batterCake = new EggDecorator(batterCake);
        batterCake = new SausageDecorator(batterCake);

        System.out.println(batterCake.getDesc() + ", 售价：" + batterCake.cost());
    }
}
