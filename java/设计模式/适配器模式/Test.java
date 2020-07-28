package example;

public class Test {

    public static void main(String[] args) {
        MyRestaurantAdapter adapter = new MyRestaurantAdapter();
        adapter.haveFood();
        adapter.haveDrink();
        adapter.haveSong();

        // 第二种方式
        MyRestaurantAdapter2 adapter2 = new MyRestaurantAdapter2(new RestaurantImpl());
        adapter2.haveSong();
    }
}
