package example;

/**
 * 定义实体类
 */
public class BatterCake extends AbstractBatterCake{

    public BatterCake() {
    }

    @Override
    public String getDesc() {
        return "煎饼";
    }

    @Override
    public int cost() {
        // 基础金额
        return 8;
    }
}
