package cli.command.files;

import app.AppConfig;
import app.ChordState;
import app.backup.DistributedFile;
import app.mutex.MutexState;
import cli.command.CLICommand;

public class AddFileCommand implements CLICommand {
    
    @Override
    public String commandName() {
        return "add_file";
    }
    
    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");
        
        if (splitArgs.length == 2) {
            try {
                String fulFilePath = AppConfig.directory.concat(splitArgs[0]);
                // Calculate the key for the file using Chord hash function
                int key = ChordState.chordHash(fulFilePath);
                AppConfig.timestampedStandardPrint("File chord key is: " + key);
                boolean privacy = splitArgs[1].equalsIgnoreCase(
                        "public"); // Determine the privacy of the file
                DistributedFile value = new DistributedFile(fulFilePath, privacy,
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.myServentInfo.getChordId());
                if (key < 0 || key >= ChordState.CHORD_SIZE) {
                    throw new NumberFormatException();
                }
                // Synchronized block to ensure thread safety
                synchronized (MutexState.lock) {
                    AppConfig.chordState.addFileToFilesIAdded(key);
                    AppConfig.chordState.putValue(key, value); // Put the file in the Chord network
                }
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint(
                        "Invalid key and value pair. Both should be ints. 0 <= key <= "
                                + ChordState.CHORD_SIZE
                                + ". 0 <= value.");
            }
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for put");
        }
    }
}
