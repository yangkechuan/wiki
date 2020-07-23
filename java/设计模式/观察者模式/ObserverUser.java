package example;

import java.util.Observable;
import java.util.Observer;


public class ObserverUser implements Observer {

    /**
     * 观察者信息
     */
    private String name;
    private Integer age;
    private Observable observable;

    public ObserverUser(Observable observable) {
        // 实例化时，加入被观察者
        this.observable = observable;
        observable.addObserver(this);
    }

    public void display(){
        System.out.println("user: " + this.name + " age: " + this.age);
    }
    public void update(Observable o, Object arg) {
        if (o instanceof SubjectUser){
            SubjectUser user = (SubjectUser) o;
            this.name = user.getName();
            this.age = user.getAge();
            this.display();
        }
    }
}
