package telran.chat.server.controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import telran.chat.server.tasks.ChatServerReceiver;
import telran.chat.server.tasks.ChatServerSender;
import telran.mediation.BlkQueue;
import telran.mediation.IBlkQueue;

public class ChatServerAppl {

	public static void main(String[] args) throws InterruptedException {
		int port = 9000;
		IBlkQueue<String> messageBox = new BlkQueue<>(10);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		ChatServerSender sender = new ChatServerSender(messageBox);
		Thread senderThread = new Thread(sender);
		senderThread.setDaemon(true);
		senderThread.start();
		try (ServerSocket serverSocket = new ServerSocket(port);) {
			while (true) {
				System.out.println("Server wait...");
				Socket socket = serverSocket.accept();
				System.out.println("Connection established");
				System.out.println("Client host: " + socket.getInetAddress() + ":" + socket.getPort());
				sender.addClient(socket);
				ChatServerReceiver receiver = new ChatServerReceiver(socket, messageBox);
				executorService.execute(receiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executorService.shutdown();
			executorService.awaitTermination(1, TimeUnit.MINUTES);
		}
	}

}
