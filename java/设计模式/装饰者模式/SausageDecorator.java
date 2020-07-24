package example;

/**
 * 香肠装饰类
 */
public class SausageDecorator extends AbstractDecorator{
    public SausageDecorator(AbstractBatterCake abstractBatterCake) {
        super(abstractBatterCake);
    }

    @Override
    public String getDesc() {
        return super.getDesc() + " 加一根香肠";
    }

    @Override
    public int cost() {
        return super.cost() + 2;
    }
}
