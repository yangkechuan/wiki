package example;

public class Test {

    public static void main(String[] args) {
        // 豆芽
        System.out.println("准备炒豆芽");
        CookBeanSprout cookBeanSprout = new CookBeanSprout();
        cookBeanSprout.cook();

        System.out.println("-------------");
        // 茄子
        System.out.println("准备炒茄子");
        CookEggplant cookEggplant = new CookEggplant();
        cookEggplant.cook();
    }
}
