package example;


public class Test {

    public static void main(String[] args) {
        PhoneFactory factory = new PhoneFactory();
        Phone miPhone = factory.makePhone("MiPhone");
        Phone iPhone = factory.makePhone("IPhone");

    }
}
