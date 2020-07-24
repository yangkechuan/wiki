package example;


public class Test {

    public static void main(String[] args) {
        AbstractFactory miFactory = new XiaoMiFactory();
        AbstractFactory appleFactory = new AppleFactory();
        miFactory.makePhone();
        appleFactory.makePhone();
    }
}
