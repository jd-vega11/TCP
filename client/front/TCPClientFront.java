package front;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import back.TCPClient;

/**
 * Ventana principal del servidor del CupiEmail
 * @author JuanDavid
 */
public class TCPClientFront extends JFrame implements ActionListener
{
	
	
	
    private final static String LISTAR = "Listar archivos";

    private final static String DESCARGAR_1 = "Descargar arch 1";    

    private final static String DESCARGAR_2 = "Descargar arch 2";    

    private final static String DESCARGAR_3 = "Descargar arch 3";    

    private final static String DESCONECTAR = "Desconectar";
    

	// -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Clase principal del servidor
     */
	private TCPClient cliente;
	
	// -----------------------------------------------------------------
    // Atributos de la interfaz
   
    private JPanel panelBotones;
    
  
    private JToggleButton btnPedirListaArchivos;
    
    private JToggleButton btnDescargar1;
    
    private JToggleButton btnDescargar2;
    
    private JToggleButton btnDescargar3;
    
    private JToggleButton desconectar;
	
    private JList lista;
    
    ArrayList<String> archivos;
	
	// -----------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------
	
	/**
	 * Construye la ventana principal de la aplicación
	 * @param pServidor Es una referencia al servidor sobre el que funciona esta interfaz.
	 */
	public TCPClientFront(TCPClient pCliente)
	{
		archivos = new ArrayList();
		cliente = pCliente;
		setTitle( "ClienteTCP" );
        setSize( 700, 300 );
        setResizable( false );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setLayout( new BorderLayout( ) );
        
        panelBotones = new JPanel();
        lista = new JList();
        
        add(panelBotones, BorderLayout.NORTH);
    	add(lista, BorderLayout.CENTER);
    	
    	
    	btnPedirListaArchivos = new JToggleButton(LISTAR);
    	btnPedirListaArchivos.setActionCommand(LISTAR);
    	btnPedirListaArchivos.addActionListener(this);
      	panelBotones.add(btnPedirListaArchivos);

    	
      	btnDescargar1 = new JToggleButton(DESCARGAR_1);
      	btnDescargar1.setActionCommand(DESCARGAR_1);
      	btnDescargar1.addActionListener(this);
    	panelBotones.add(btnDescargar1);
    	
    	btnDescargar2 = new JToggleButton(DESCARGAR_2);
      	btnDescargar2.setActionCommand(DESCARGAR_2);
      	btnDescargar2.addActionListener(this);
    	panelBotones.add(btnDescargar2);
    	
    	btnDescargar3 = new JToggleButton(DESCARGAR_3);
      	btnDescargar3.setActionCommand(DESCARGAR_3);
      	btnDescargar3.addActionListener(this);
    	panelBotones.add(btnDescargar3);
    	
    	desconectar = new JToggleButton(DESCONECTAR);
    	desconectar.setActionCommand(DESCONECTAR);
    	desconectar.addActionListener(this);
    	panelBotones.add(desconectar);
        
	}
	
	 /**
     * Manejo de los eventos de los botones.
     * @param pEvento Accion que generó el evento. pEvento != null
     */
	public void actionPerformed(ActionEvent pEvento) 
	{
		String comando = pEvento.getActionCommand( );
		
		if( comando.equals( LISTAR ) )
        {
            try {
				archivos = cliente.pedirListaArchivos();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            lista.removeAll();
        	lista.setListData(archivos.toArray());
            if(btnPedirListaArchivos.isSelected())
            {
            	btnPedirListaArchivos.setSelected(false);
            }
        }
        else if( comando.equals( DESCARGAR_1 ) )
        {
        	cliente.descargarArchivo(archivos.get(1));
            if(btnDescargar1.isSelected())
            {
            	btnDescargar1.setSelected(false);
            }       
            JOptionPane.showMessageDialog(this, "Finalizado");

        }
        else if( comando.equals( DESCARGAR_2 ) )
        {
        	cliente.descargarArchivo(archivos.get(2));
            if(btnDescargar2.isSelected())
            {
            	btnDescargar2.setSelected(false);
            }
            JOptionPane.showMessageDialog(this, "Finalizado");

        }
        else if( comando.equals( DESCARGAR_3 ) )
        {
        	cliente.descargarArchivo(archivos.get(3));
            if(btnDescargar3.isSelected())
            {
            	btnDescargar3.setSelected(false);
            }
            JOptionPane.showMessageDialog(this, "Finalizado");

        }
        else if( comando.equals( DESCONECTAR ) )
        {
        	cliente.cerrarConexion();
            if(desconectar.isSelected())
            {
            	desconectar.setSelected(false);
            }
        }
	}
	
	
	
	
	/*
     * Se encarga de ejecutar la aplicación, creando una nueva interfaz.
     * @param args Parámetros de ejecución que no son usados.
     */
	public static void main(String[] args)
	{
		try
		{
			TCPClient client = new TCPClient();
			
			TCPClientFront interfaz = new TCPClientFront(client);
			interfaz.setVisible(true);
			
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}


}
