package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import app.mutex.DistributedMutex;
import app.mutex.SuzukiMutex;
import app.mutex.SuzukiToken;
import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

public class ServentInitializer implements Runnable {
	private int getSomeServentPort() {
		int bsPort = AppConfig.BOOTSTRAP_PORT;
		
		int retVal = -2;
		
		try {
			Socket bsSocket = new Socket("localhost", bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Hail\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.flush();
			
			Scanner bsScanner = new Scanner(bsSocket.getInputStream());
			retVal = bsScanner.nextInt();
			
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	@Override
	public void run() {
		int someServentPort = getSomeServentPort();
		
		if (someServentPort == -2) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (someServentPort == -1) { //bootstrap gave us -1 -> we are first
			AppConfig.timestampedStandardPrint("First node in Chord system. I have token.");
			SuzukiToken token = new SuzukiToken();
			AppConfig.mutexState.setSuzukiToken(token);
		} else { //bootstrap gave us something else - let that node tell our successor that we are here
			AppConfig.timestampedStandardPrint(someServentPort + " will add me.");
			NewNodeMessage nnm = new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(), someServentPort);
			MessageUtil.sendMessage(nnm);
		}
	}

}
