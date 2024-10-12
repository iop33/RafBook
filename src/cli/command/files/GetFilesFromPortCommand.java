package cli.command.files;

import app.AppConfig;
import cli.command.CLICommand;
import servent.message.files.GetFilesFromPortMessage;
import servent.message.util.MessageUtil;

public class GetFilesFromPortCommand implements CLICommand {
    @Override
    public String commandName() {
        return "get_from_port";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(":");

        if (splitArgs.length == 2) {
            int port = Integer.parseInt(splitArgs[1]);
            AppConfig.timestampedStandardPrint("Getting files from port " + port);
            GetFilesFromPortMessage getFilesFromPortMessage = new GetFilesFromPortMessage(AppConfig.myServentInfo.getListenerPort(), port);
            MessageUtil.sendMessage(getFilesFromPortMessage);
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for get from port");
        }
    }
}
