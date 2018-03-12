package back;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

class ThreadServer extends Thread {

	public static final String GET_FILE = "GetFile";
	public static final String FILE_LIST = "FileList";
	public static final String CLOSE_SESSION = "CloseSession";

	public static final String SEPARADOR_COMANDOS = ";;;";
	public static final String SEPARADOR_ARCHIVOS = ":::";

	/**
	 * estado de la sesion
	 */
	private boolean estado;

	/**
	 * Socket asignado al thread
	 */
	private Socket connectionSocket;

	/**
	 * Directorio de archivos
	 */
	private File directory;

	/**
	 * Lista de archivos del directorio
	 */
	private ArrayList<File> directoryFiles;

	/**
	 * Lector del Socket
	 */
	private BufferedReader bufferedReader;

	/**
	 * Escritor del Socket
	 */
	private PrintWriter printWriter;

	/**
	 * Otro output para el socket
	 */
	private OutputStream outputStream;

	/**
	 * 
	 * @param socket
	 * @param c
	 * @param directory
	 */
	public ThreadServer(Socket socket, File directory) throws Exception {

		this.estado = true;
		this.connectionSocket = socket;
		this.directory = directory;
		this.directoryFiles = new ArrayList<>();

		this.bufferedReader = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));

		this.printWriter = new PrintWriter(this.connectionSocket.getOutputStream(), true);

		this.outputStream = connectionSocket.getOutputStream();
	}

	public void listDirectoryFiles() {

		System.out.println("[Thread Server] Listing directory files: ");
		StringBuilder listOfFiles = new StringBuilder();
		listOfFiles.append(FILE_LIST);

		if (!directoryFiles.isEmpty()) {
			directoryFiles.clear();
		}

		File[] directoryContents = directory.listFiles();

		for (File file : directoryContents) {
			if (file.isFile()) {
				System.out.println("[Thread Server] File name: \t" + file.getName());

				listOfFiles.append(SEPARADOR_ARCHIVOS);
				listOfFiles.append(file.getName());
				directoryFiles.add(file);
			}
		}

		printWriter.println(listOfFiles.toString());
	}

	public void uploadFile(String fileName) throws Exception {
		File fileToUpload = null;

		for (File file : directoryFiles) {
			if (file.getName().equals(fileName)) {
				fileToUpload = file;
				break;
			}
		}

		if (fileToUpload != null) {
			byte[] byteArray = new byte[(int) fileToUpload.length()];
			FileInputStream fileInputStream = new FileInputStream(fileToUpload);

			Float size = (float) (byteArray.length / (1024 * 1024));
			DecimalFormat formatter = new DecimalFormat("#.00");

			System.out.println("[Thread Server] uploadind : " + fileToUpload.getName() + " - Size: "
					+ formatter.format(size) + " MB");

			BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
			bufferedInputStream.read(byteArray, 0, byteArray.length);

			this.outputStream.write(byteArray, 0, byteArray.length);
			this.outputStream.flush();

			bufferedInputStream.close();
		} 
		else {
			System.out.println("[Thread Server] File not found in directory!");
			throw new Exception("[Thread Server] File not found in directory!");
		}
	}

	public void run() {
		try {
			while (this.estado) {
				String[] comando = bufferedReader.readLine().split(SEPARADOR_COMANDOS);

				if (comando[0].equals(FILE_LIST)) {
					listDirectoryFiles();
				} 
				else if (comando[0].equals(GET_FILE)) {
					uploadFile(comando[1]);
				} 
				else if (comando[0].equals(CLOSE_SESSION)) {
					this.estado = false;
					printWriter.println("[Thread Server] Connection Concluded!");
					this.bufferedReader.close();
					this.printWriter.close();
					this.outputStream.close();
				}

			}
		} 
		catch (Exception e) {
			System.out.println("[Thread Server] Exception Caught during Thread Execution!");
			e.printStackTrace();
		}
		finally {
			try {
				this.estado = false;
				printWriter.println("[Thread Server] Connection Concluded!");
				this.bufferedReader.close();
				this.printWriter.close();
				this.outputStream.close();
			} catch (IOException e2) {
				System.out.println("[Thread Server] Exception Caught during Thread Clousure!");
				e2.printStackTrace();
			}
		}
	}

}