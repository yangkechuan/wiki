package example;

public class Test {

    public static void main(String[] args) {
        BuyHouse buyHouse = new BuyHouseImpl();
        buyHouse.buyHouse();

        BuyHouseProxy proxy = new BuyHouseProxy(buyHouse);
        proxy.buyHouse();
    }
}
