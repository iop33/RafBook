package cli.command;

import app.AppConfig;
import app.backup.Backuper;
import app.backup.Buddy;
import cli.CLIParser;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private Backuper backuper;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener, Backuper backuper) {
		this.parser = parser;
		this.listener = listener;
		this.backuper = backuper;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		Buddy.getInstance().stop();
		backuper.stop();
	}
}
