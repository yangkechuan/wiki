package example;


public class TVOnCommand implements Command{

    TV tv;

    public TVOnCommand(TV tv) {
        this.tv = tv;
    }

    public void execute() {
        this.tv.on();
    }
}
