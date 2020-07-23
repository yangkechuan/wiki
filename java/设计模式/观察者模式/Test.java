package example;


public class Test {

    public static void main(String[] args) {
        SubjectUser subjectUser = new SubjectUser();

        ObserverUser observerUser = new ObserverUser(subjectUser);
        subjectUser.setSubjectUser("test", 10);
        subjectUser.setSubjectUser("test1", 30);
    }
}
