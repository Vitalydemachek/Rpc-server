package com.rpc;

import com.rpc.structure.ClientWorker;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RpcServerApp {
	private static List<ClientWorker> clientList = new ArrayList<>();
	private final Properties properties = new Properties();

	public static void main(String[] args) throws IOException {
		RpcServerApp server = new RpcServerApp();
		server.start(Integer.valueOf(args[0]));
	}

	private void start(int port) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties")) {
			properties.load(fileInputStream);
		}
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				try {
					clientList.add(new ClientWorker(clientSocket, properties));
				} catch (IOException e) {
					clientSocket.close();
				}
			}
		}
	}
}
