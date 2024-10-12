package cli.command.files;

import app.AppConfig;
import app.mutex.MutexState;
import cli.command.CLICommand;

public class RemoveFileCommand implements CLICommand {
    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        String fullFilePath=AppConfig.directory.concat(args);
        AppConfig.chordState.removeFileWithPath(fullFilePath);
    }
}
