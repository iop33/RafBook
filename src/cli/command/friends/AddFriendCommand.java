package cli.command.friends;

import app.AppConfig;
import cli.command.CLICommand;
import servent.message.friends.AddFriendMessage;
import servent.message.util.MessageUtil;

public class AddFriendCommand implements CLICommand {
    @Override
    public String commandName() {
        return "add_friend";
    }

    @Override
    public void execute(String args) {
        try{
            int friendPort = Integer.parseInt(args);
            AppConfig.chordState.addFriend(friendPort);
            AppConfig.timestampedStandardPrint("Added " + friendPort + " as friend!");
            AddFriendMessage addFriendMessage = new AddFriendMessage(AppConfig.myServentInfo.getListenerPort(), friendPort);
            MessageUtil.sendMessage(addFriendMessage);
        }catch (NumberFormatException e){
            AppConfig.timestampedErrorPrint("Invalid arguments for add_fiend command");
        }

    }
}
