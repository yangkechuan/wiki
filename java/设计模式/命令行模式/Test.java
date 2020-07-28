package example;


public class Test {

    public static void main(String[] args) {
        Light light = new Light();
        Command lightOn = new LightOnCommand(light);
        Command ligOff = new LightOffCommand(light);

        TV tv = new TV();
        Command tvOn = new TVOnCommand(tv);
        Command tvOff = new TVOffCommand(tv);

        RemoteController remoteController = new RemoteController(2);
        remoteController.setCommand(0, lightOn, ligOff);
        remoteController.setCommand(1, tvOn, tvOff);

        remoteController.onButtonPressed(0);
        remoteController.offButtonPressed(0);

        remoteController.onButtonPressed(1);
        remoteController.offButtonPressed(1);

    }
}
