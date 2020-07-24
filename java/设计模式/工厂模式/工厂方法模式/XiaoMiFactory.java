package example;


public class XiaoMiFactory implements AbstractFactory{

    public Phone makePhone() {
        return new MiPhone();
    }
}
