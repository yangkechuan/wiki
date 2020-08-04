package example;

public class Prototype implements Cloneable{

    /**
     * 用一个已经创建的实例作为原型
     * 通过复制该原型对象来创建一个和原型相同或相似的新对象
     */
    public Prototype() {
        System.out.println("原型创建成功");
    }

    /**
     * 原型模式的克隆分为浅克隆和深克隆
     * 这里采用浅克隆
     * @return object
     * @throws CloneNotSupportedException
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        System.out.println("原型复制成功");
        return super.clone();
    }
}
