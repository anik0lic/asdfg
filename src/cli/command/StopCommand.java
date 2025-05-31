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
		AppConfig.timestampedStandardPrint("Stopping...");

		// Notify Bootstrap about exit
		try (Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT)) {
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Stop\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.flush();
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Error notifying bootstrap about stop.");
		}

        // Inform successor to take over responsibilities if this node is part of the Chord ring
		ServentInfo successor = AppConfig.chordState.getSuccessorTable()[0];
		if (successor != null) {
			AppConfig.timestampedStandardPrint("Transferring responsibilities to successor at port " + successor.getListenerPort());

			// Send a RemoveNodeMessage to successor with the key-value pairs
			RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(
					AppConfig.myServentInfo.getListenerPort(),
					successor.getListenerPort(),
					AppConfig.myServentInfo,
					AppConfig.chordState.getValueMap()
			);
			MessageUtil.sendMessage(removeNodeMessage);
		} else {
			AppConfig.timestampedStandardPrint("No successor found. No keys to transfer.");
		}


		parser.stop();
		listener.stop();
		AppConfig.timestampedStandardPrint("Node at port " + AppConfig.myServentInfo.getListenerPort() + " has stopped.");
	}

}
