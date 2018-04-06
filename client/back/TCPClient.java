package back;

import java.io.*;
import java.net.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class TCPClient 
{
	
	public static final String GET_FILE = "GetFile";
	public static final String FILE_LIST = "FileList";
	public static final String CLOSE_SESSION = "CloseSession";
	public static final String FINISHED = "Finished";
	
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
    	inicializarConexion();    	    
	}
    
    public void inicializarConexion( )
    {
    	try {
    		 String archivoPropiedades = "./data/clientFiles/client.properties";
    		 FileInputStream fis = new FileInputStream( archivoPropiedades );
    	     Properties config = new Properties( );
    	     config.load( fis );
    	     fis.close( );
    	     
    	     
			 canal = new Socket( config.getProperty( "servidor.dirIp" ) , Integer.parseInt(config.getProperty( "servidor.puerto" )) );
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
    	
    	int size = 16384;
    	byte[] data = new byte[size];
    	
    	try {
    		File f = new File("./data/clientFiles", nombre);
    		FileOutputStream fileOut = new FileOutputStream(f);
    	
    		int paquete = 0;
    		boolean completado = false;
    		byte[] end = FINISHED.getBytes();
    		
    		long t1 = System.currentTimeMillis();
    		int i = this.inFromServer.read(data);
    		while(i > 0)
    		{
    			fileOut.write(data, 0, i);
    			paquete++;
    			System.out.println("Paquete numero " + paquete + " de tamanio " + i + " bytes");    			
    			i = this.inFromServer.read(data);    		
    		}    		
    		long t2 = System.currentTimeMillis();
			long t = t2-t1;
			System.out.println("[Cliente] Download finished!");
			System.out.println("Time: " + t*0.001 + " segundos");   	
    		
    		fileOut.close();
    		
    		out.close();
			in.close();
			inFromServer.close();
	    	canal.close();
	    	
	    	inicializarConexion();
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
