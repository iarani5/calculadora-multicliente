package calculadora_SSDD_TP3;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente {

    public static void main(String[] args) {

        //Host del servidor
        final String HOST = "127.0.0.1";
        //Puerto del servidor
        final int PUERTO = 6000;
        DataInputStream in;
        DataOutputStream out;
        String operacion = ""; //creamos string vacio de operacion  
        
        try {
            //Creo el socket para conectarme con el cliente
            Socket sc = new Socket(HOST, PUERTO);
            
            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());
 	        Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner

 	       while (true){
            	System.out.print( operacion.indexOf("salir"));
                      		
                    System.out.print( "\nEnvia una operacion al servidor: " );
                    operacion = entradaEscaner.nextLine (); //guardamos el dato que se ingrese en la variable operacion     
                   
                    //Envio la operacion al servidor        
                    out.writeUTF(operacion);               	
               
                    //Recibo el mensaje del servidor
                     String resultado = in.readUTF();

                    System.out.println("\nEl resultado es: "+resultado);
               
                    
		  	          if(operacion.indexOf("salir") != -1) {
		  	        	sc.close();
		                entradaEscaner.close();

		  	        	System.out.println("\n>>>> Cliente desconectado ...");
		  	        	 break;
		  	          }
            }
            sc.close();
            entradaEscaner.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
