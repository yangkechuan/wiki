package example;


public class Test {

    public static void main(String[] args) throws CloneNotSupportedException {
        Prototype r1 = new Prototype();
        Prototype r2 = (Prototype) r1.clone();

        System.out.println(r1 == r2);
    }
}




