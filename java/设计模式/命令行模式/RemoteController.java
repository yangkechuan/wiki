package example;

/**
 * 遥控器
 */
public class RemoteController {
    Command[] onCommands;
    Command[] offCommands;

    public RemoteController(int commandSize) {
        this.onCommands = new Command[commandSize];
        this.offCommands = new Command[commandSize];
    }

    public void setCommand(int i, Command onCommand, Command offCommand){
        onCommands[i] = onCommand;
        offCommands[i] = offCommand;
    }

    /**
     * 按下开按钮
     */
    public void onButtonPressed(int i){
        onCommands[i].execute();
    }
    /**
     * 按下关按钮
     */
    public void offButtonPressed(int i){
        offCommands[i].execute();
    }
}
