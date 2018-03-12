package back;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;

public class TCPClient 
{
	public static final int SERVER_PORT = 3210;
	
	public static final String GET_FILE = "GetFile";
	public static final String FILE_LIST = "FileList";
	public static final String CLOSE_SESSION = "CloseSession";
	
	public static final String SEPARADOR_COMANDOS = ";;;";
	public static final String SEPARADOR_ARCHIVOS = ":::";
	
	 /**
     * Canal de comunicación con el servidor.
     */
    private Socket canal;

    /**
     * Flujo que lee los datos que llegan del servidor a través del socket.
     */
    private BufferedReader in;

    /**
     * Flujo que envía los datos al servidor a través del socket.
     */
    private PrintWriter out;
    
    private InputStream inFromServer;
    
    public TCPClient() 
    {
    	try {
    		 
			 canal = new Socket( "localhost", SERVER_PORT );
			 out = new PrintWriter( canal.getOutputStream( ), true );
	         in = new BufferedReader( new InputStreamReader( canal.getInputStream( ) ) );
	         inFromServer = canal.getInputStream();
		} 
    	catch (IOException e) {
			System.out.println("Problemas al conectarse con el servidor: " + e.getMessage());
		}    
	}
    
    public ArrayList<String> pedirListaArchivos( ) throws IOException
    {
    	ArrayList<String> listaArchivos = new ArrayList<>();
    	String msj = FILE_LIST + SEPARADOR_COMANDOS;
    	out.println(msj);
    	String rta = in.readLine();
    	String[] partes = rta.split(SEPARADOR_ARCHIVOS);
    	
    	if(partes[0].equals(FILE_LIST))
    	{
    		for(String s : partes)
    		{
    			listaArchivos.add(s);
    			System.out.println(s + "\n");
    		}
    	}
    	
    	
    	
    	return listaArchivos;    	
    }
    
    public void descargarArchivo(String nombre)
    {
    	String msj = GET_FILE + SEPARADOR_COMANDOS + nombre;
    	out.println(msj);
    	
    	boolean complete = true;
    	int c;
    	int i = 0;
    	int size = 9022386;
    	byte[] data = new byte[size];
    	
    	try {
    		File f = new File("./data/clientFiles", nombre);
    		FileOutputStream fileOut = new FileOutputStream(f);
    		DataOutputStream dataOut = new DataOutputStream(fileOut);
    		int tot = 0;
    		//empty file case
    		while (complete) {
    			c = inFromServer.read(data, 0, data.length);
    			tot += c;
    			System.out.println("Tamanio paquete " + i + ":" + c);
    			System.out.println("Paquete " + i + " " +data.toString());
    			if (tot >= data.length) {
    				complete = false;
    				System.out.println("Completed");			

    			} else {
    				dataOut.write(data, 0, c);
    				dataOut.flush();
    			}    			
    			i++;
    		}
  		
    		fileOut.close();
    		
    	}
    	catch(Exception e)
    	{    		
    		System.out.println("Problemas al descargar el archivo: " + e.getMessage());
    		
    	}

    }
    
    public void cerrarConexion()
    {
    	String msj = CLOSE_SESSION + SEPARADOR_COMANDOS;
    	out.println(msj);
    	
    	
    	try {
    		out.close();
			in.close();
			inFromServer.close();
	    	canal.close();
		} 
    	catch (IOException e) 
    	{
    		System.out.println("No fue posible cerrar la conexion: " + e.getMessage());
		}
    	
    }

}
