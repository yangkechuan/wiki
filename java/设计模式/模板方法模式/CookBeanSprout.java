package example;

/**
 * 豆芽
 */
public class CookBeanSprout extends AbstractCookVegetable{
    @Override
    protected void pourOil() {
        System.out.println("热锅少油");
    }

    @Override
    protected void fry() {
        System.out.println("快速翻炒");
    }

    @Override
    protected void pourSauce() {
        System.out.println("加盐和少量生抽");
    }
}
