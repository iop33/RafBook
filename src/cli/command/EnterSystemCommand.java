package cli.command;

import app.ServentInitializer;

public class EnterSystemCommand implements CLICommand{
    @Override
    public String commandName() {
        return "enter";
    }

    @Override
    public void execute(String args) {
        ServentInitializer serventInitializer = new ServentInitializer();
        Thread initializerThread = new Thread(serventInitializer);
        initializerThread.start();
    }
}
