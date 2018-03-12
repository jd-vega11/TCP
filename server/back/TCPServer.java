package back;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer {
	
	public static final int LISTENING_PORT = 3210;
	
	/**
	 * Directorio de archivos
	 */
	private File directory; 
	
	/**
	 * Socket del servidor
	 */
	private ServerSocket serverSocket;
	
	/**
	 * socket de comunicacion con el cliente
	 */
	private Socket connectionSocket;
	

	public TCPServer(String directoryPath) throws Exception {
		
		try {
			String exception = "";
			
			if(directoryPath == null || directoryPath.length() == 0){
				exception = "Directory path not included!";
				throw new Exception(exception);
			}
			
			this.directory = new File(directoryPath);

			if (!this.directory.exists() || !this.directory.isDirectory()) {
				
				exception = "Specified directory does not exist, or it is not a directory!";
				System.out.println(exception);
				throw new Exception(exception);
			}
			this.serverSocket = new ServerSocket(LISTENING_PORT);
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	private void listen(){
		try {

			System.out.println("[Server Socket] Listening on port " + LISTENING_PORT);
			
			while (true) {
				connectionSocket = serverSocket.accept();
				new ThreadServer(connectionSocket, directory);
			}
		} 
		catch (Exception e) {
			System.out.println("[Server Socket] Exception dring the listening face!");
			System.out.println("[Server Socket] Error description:  " + e.getMessage());
		}
	}


	public static void main(String[] args) {
		try {
			TCPServer tcpServer = new TCPServer(args[0]);
			tcpServer.listen();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
