package example;

/**
 * 茄子
 */
public class CookEggplant extends AbstractCookVegetable{
    @Override
    protected void wash() {
        System.out.println("去除头尾，然后用水洗下");
    }

    @Override
    protected void pourOil() {
        System.out.println("热锅多油");
    }

    @Override
    protected void pourSauce() {
        System.out.println("加盐和鸡精");
    }
}
