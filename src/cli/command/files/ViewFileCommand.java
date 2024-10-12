package cli.command.files;

import app.AppConfig;
import app.ChordState;
import app.backup.DistributedFile;
import cli.command.CLICommand;

public class ViewFileCommand implements CLICommand {

	@Override
	public String commandName() {
		return "view_file";
	}

	@Override
	public void execute(String args) {
		try {
			String fullFilePath=AppConfig.directory.concat(args);
			// Calculate the key for the file using Chord hash function
			int key = ChordState.chordHash(fullFilePath);
			AppConfig.timestampedStandardPrint("Looking for file with key: " + key);
			DistributedFile val = AppConfig.chordState.getValue(key); // Get the file from the Chord network

			if (val == null) {
				AppConfig.timestampedStandardPrint("No such key: " + key + ", for file name: " + args);
			} else if (val.getFilePath().equalsIgnoreCase("WAIT")) {
				AppConfig.timestampedStandardPrint("Please wait...");
			}	else if (val.getFilePath().equalsIgnoreCase("NOT_FRIEND")) {
				AppConfig.timestampedStandardPrint("You can't view this file, file is private and you are not friend with owner of it");
			}
			else {
				AppConfig.timestampedStandardPrint(key + ", [File Content]:\n" + val.getContent()); // Display the content of the file
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Invalid argument for dht_get: " + args + ". Should be key, which is an int.");
		}
	}
}
