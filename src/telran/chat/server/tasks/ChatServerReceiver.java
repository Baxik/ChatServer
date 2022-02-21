package telran.chat.server.tasks;

import java.io.ObjectInputStream;
import java.net.Socket;

import telran.mediation.IBlkQueue;

public class ChatServerReceiver implements Runnable {
	Socket socket;
	IBlkQueue<String> messageBox;

	public ChatServerReceiver(Socket socket, IBlkQueue<String> messageBox) {
		this.socket = socket;
		this.messageBox = messageBox;
	}

	@Override
	public void run() {
		try (Socket socket = this.socket) {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			while(true) {
				String message = ois.readObject().toString();
				messageBox.push(message);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort() + " closed");
		}

	}

}
