package cli.command;

import app.AppConfig;
import app.ServentInfo;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.RemoveNodeMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener) {
		this.parser = parser;
		this.listener = listener;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		try (Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT)) {
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Stop\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.flush();
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Error notifying bootstrap about stop.");
		}

		if(AppConfig.chordState.getSuccessorTable() == null) {
			AppConfig.timestampedStandardPrint("No successor table found. Stopping immediately.");
			parser.stop();
			listener.stop();
			return;
		}

		int successorPort = AppConfig.chordState.getNextNodePort();
		AppConfig.timestampedStandardPrint("Transferring responsibilities to successor at port " + successorPort);

		RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(AppConfig.myServentInfo.getListenerPort(), successorPort, AppConfig.chordState.getValueMap(), AppConfig.chordState.getPredecessor().getListenerPort());
		MessageUtil.sendMessage(removeNodeMessage);

		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		AppConfig.timestampedStandardPrint("Node at port " + AppConfig.myServentInfo.getListenerPort() + " has stopped.");
	}

}
