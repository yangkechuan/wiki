package example;

/**
 * 定义炒菜模板
 */
public abstract class AbstractCookVegetable {
    protected void wash() {
        System.out.println("洗菜");
    }

    protected void pourOil() {
        System.out.println("热油下锅");
    }

    protected void fry() {
        System.out.println("下菜翻炒");
    }

    /**
     * 具体调料由菜决定
     */
    protected abstract void pourSauce();

    /**
     * 具体炒菜流程
     */
    public final void cook() {
        this.wash();
        this.pourOil();
        this.fry();
        this.pourSauce();
        System.out.println("起锅吃菜");
    }
}
