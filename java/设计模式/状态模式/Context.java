package example;

/**
 * 定义当前状态
 */
public class Context {

    State state;

    public Context(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        System.out.println("当前状态为：" + state);
    }

    public void request(){
        state.handle(this);
    }
}
