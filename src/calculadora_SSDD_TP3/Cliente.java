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
        //DataInputStream in;
        DataOutputStream out;
        //OutputStreamWriter out;
        InputStreamReader in;
        
        try {
            //Creo el socket para conectarme con el cliente
            Socket sc = new Socket(HOST, PUERTO);
       
            //in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            //out = new OutputStreamWriter(sc.getOutputStream(), "UTF8");
	        in = new InputStreamReader(sc.getInputStream(), "UTF8");

           // String peticion = "s";              
	       String operacion = ""; //creamos string vacio de operacion  
	       Scanner entradaEscaner = new Scanner (System.in); //Creación de un objeto Scanner
           if (operacion.indexOf("exit") == -1){
            		
                    System.out.print( "\nEnvia una operacion al servidor: " );
                    operacion = entradaEscaner.nextLine (); //guardamos el dato que se ingrese en la variable operacion     
                   
                    //Envio la operacion al servidor                   
                    
                    out.writeUTF(operacion);               	
               
                    //Recibo el mensaje del servidor
                    
                   
                   // System.out.println(in.read());
                   // String resultado = in.read();

                    System.out.println("\nEl resultado es: "+in.read());
               
              //Envio mensaje si deseo realizar otra operacion 
               // Scanner entrada = new Scanner (System.in); //Creación de un objeto Scanner
                //System.out.println( "\nAprete 's' para realizar otra operacion o cualquier otro digito para finalizar: " );
              //  peticion = entrada.nextLine (); //guardamos el dato que se ingrese en la variable operacion   
              //  System.out.println(peticion);              
              //  out.writeUTF(peticion); 
               
            }
           else {
            	//Cierro el socket
                sc.close();
                entradaEscaner.close();
           }

        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
