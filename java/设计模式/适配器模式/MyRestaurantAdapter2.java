package example;

public class MyRestaurantAdapter2 implements Bar{

    private Restaurant restaurant;

    public MyRestaurantAdapter2(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurant.haveFood();
        this.restaurant.haveDrink();
    }

    public void haveSong() {
        System.out.println("餐厅同样可以有酒吧的相关驻唱服务");
    }
}
