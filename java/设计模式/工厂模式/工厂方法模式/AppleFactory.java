package example;


public class AppleFactory implements AbstractFactory{
    public Phone makePhone() {
        return new IPhone();
    }
}
