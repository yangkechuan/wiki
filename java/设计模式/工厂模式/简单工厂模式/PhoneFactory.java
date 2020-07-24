package example;

/**
 * 手机工厂类
 */
public class PhoneFactory {

    public Phone makePhone(String phoneType){
        if (phoneType.equals("MiPhone")){
            return new MiPhone();
        }else if (phoneType.equals("IPhone")){
            return new IPhone();
        }
        return null;
    }
}
