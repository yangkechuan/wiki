package com.lib;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User    : yangkechuan
 * Date    : 2018/7/20
 * Time    : 11:36
 */
class User implements Serializable {

    public static final Long serialVersionUID = 1L;

    private Integer age;
    private String name;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}

class Test{
    public static void main(String[] args)  throws IOException, ClassNotFoundException{
        User user = new User();
        user.setAge(10);
        user.setName("test");

        //序列化
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File("user.txt")));
        os.writeObject(user);
        os.close();

        //反序列化
        ObjectInputStream oi = new ObjectInputStream(new FileInputStream(new File("user.txt")));
        User newUser = (User) oi.readObject();
        System.out.println(newUser);
    }
}