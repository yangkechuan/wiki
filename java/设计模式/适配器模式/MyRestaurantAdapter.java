package example;


/**
 * 酒吧
 */
public class MyRestaurantAdapter extends RestaurantImpl implements Bar{
    public void haveSong() {
        System.out.println("在餐厅同样提供酒吧的驻唱服务");
    }
}
