package example;

/**
 * 鸡蛋装饰类
 */
public class EggDecorator extends AbstractDecorator{
    public EggDecorator(AbstractBatterCake abstractBatterCake) {
        super(abstractBatterCake);
    }

    @Override
    public String getDesc() {
        return super.getDesc() + " 加一个鸡蛋";
    }

    @Override
    public int cost() {
        return super.cost() + 1;
    }
}
