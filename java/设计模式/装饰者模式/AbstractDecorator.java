package example;

/**
 * 装饰类
 */
public class AbstractDecorator extends AbstractBatterCake{

    private AbstractBatterCake abstractBatterCake;

    public AbstractDecorator(AbstractBatterCake abstractBatterCake) {
        this.abstractBatterCake = abstractBatterCake;
    }

    @Override
    public String getDesc() {
        return this.abstractBatterCake.getDesc();
    }

    @Override
    public int cost() {
        return this.abstractBatterCake.cost();
    }
}
