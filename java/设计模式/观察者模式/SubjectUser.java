package example;

import java.util.Observable;


public class SubjectUser extends Observable {

    /**
     * 被观察者信息
     */
    private String name;
    private Integer age;

    public SubjectUser() {
    }

    public void subjectUserChanged(){
        // 设置为已修改，并通知所有观察者
        setChanged();
        notifyObservers();
    }
    public void setSubjectUser(String name, Integer age){
        this.name = name;
        this.age = age;
        subjectUserChanged();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
