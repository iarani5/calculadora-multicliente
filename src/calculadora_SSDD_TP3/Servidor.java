package calculadora_SSDD_TP3;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {
    static int port = 6000;

	 private static boolean compareSymbols(String newSymbol,String existSymbol){
	        
	        if(existSymbol.equals("(")) return true;
	        //misma prioridad
	        if(newSymbol.equals(existSymbol)) return false;
	        else if((newSymbol.equals("*")||newSymbol.equals("/")||newSymbol.equals("^"))&&(existSymbol.equals("*")||existSymbol.equals("/")||existSymbol.equals("^"))) return false;
	        else if((newSymbol.equals("+")||newSymbol.equals("-"))&&(existSymbol.equals("+")||existSymbol.equals("-"))) return false;
	        //diferente prioridad
	        else if((newSymbol.equals("-") ||newSymbol.equals("+")) &&(existSymbol.equals("*") || existSymbol.equals("/")|| existSymbol.equals("^"))) return false;
	        
	        return true;
	 }

	//Get de nueva expresion
    private static LinkedList<String> getNewExpre(String[] expression){
        LinkedList<String> symbols=new LinkedList<>();//Stack para simbolos
        LinkedList<String> newExpre=new LinkedList<>();//Stack para nueva expresion
        for (int i=0;i<expression.length;i++){
            String val=expression[i];
            if(val.equals("")) continue;
            switch (val){
                case "(":symbols.add(val);break;
                case ")":
                    boolean isOk=true;
                    while(isOk){
                        String _symbol=symbols.pollLast();
                        if(_symbol.equals("(")) isOk=false;
                        else newExpre.add(_symbol);
                    };
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                case "^":
                    if(symbols.size()==0){
                        symbols.add(val);
                    } else if(compareSymbols(val,symbols.get(symbols.size()-1))) symbols.add(val);
                    else{
                        while (symbols.size()>0 && !compareSymbols(val,symbols.get(symbols.size()-1))){
                            newExpre.add(symbols.pollLast());// hace un stack all hasta que la priodidad sea mas chica que la actual
                        }
                        symbols.add(val);
                    }
                    break;
                default:
                    newExpre.add(val);
            }
        }
        while(symbols.size()>0){
            newExpre.add(symbols.pollLast());
        }
        return newExpre;
    }

	  private static String cal(String str){
		  
	        //Get a new expression
	        LinkedList<String> expre=getNewExpre(str.split(" "));
	      
	        for(var i=0;i<expre.size();i++){
	            var val=expre.get(i);
	            switch(val){
	                case "-":
	                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())-Double.valueOf(expre.get(i-1).toString())));
	                    expre.remove(i-1);
	                    expre.remove(i-1);
	                    i-=2;
	                    break;
	                case "+":
	                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())+Double.valueOf(expre.get(i-1).toString())));
	                    expre.remove(i-1);
	                    expre.remove(i-1);
	                    i-=2;
	                    break;
	                case "*":
	                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())*Double.valueOf(expre.get(i-1).toString())));
	                    expre.remove(i-1);
	                    expre.remove(i-1);
	                    i-=2;
	                    break;
	                case "^":
	                	double rta = Math.pow(Double.valueOf(expre.get(i-2).toString()),Double.valueOf(expre.get(i-1).toString()));
	                    expre.set(i-2,String.valueOf(rta));
	                    expre.remove(i-1);
	                    expre.remove(i-1);
	                    i-=2;
	                    break;
	                case "/":
	                    expre.set(i-2,String.valueOf(Double.valueOf(expre.get(i-2).toString())/Double.valueOf(expre.get(i-1).toString())));
	                    expre.remove(i-1);
	                    expre.remove(i-1);
	                    i-=2;
	                    break;
	                default:
	                    break;
	            }
	        }
	        return expre.get(0);
			
	    }
	  
	  public static void main(String[] args) throws IOException {
	        ServerSocket server = new ServerSocket(port);
	        System.out.println("Esperando cliente");
	        Socket cli = server.accept();

	      //  String recibido = "", enviado = "";

	        OutputStreamWriter outw = new OutputStreamWriter(cli.getOutputStream(), "UTF8");
	        InputStreamReader inw = new InputStreamReader(cli.getInputStream(), "UTF8");
	        DataOutputStream out = new DataOutputStream(cli.getOutputStream());
	        
            char[] cbuf = new char[512];

	        while (true) {
	            System.out.println("Esperando mensaje del cliente");
	            String recibido = ""; 
	            char[] cbuf_aux =  cbuf; 
	            	inw.read(cbuf);

		  	           if(Character.getNumericValue(cbuf[1])==-1) {
		  	         
		  		            cbuf = Arrays.copyOfRange(cbuf, 2, cbuf.length);
		  	            }
		  	            for (char c : cbuf) {
		  	                recibido += c;
		  	                if (c == 00) {
		  	                    break;
		  	                }
		  	            }
		  	          
		  	          //if(recibido.indexOf("exit") ==-1? true: false) {
		  	            System.out.println("Cliente dice: " + recibido);
		  	          
		  	            //calculo
		              	String resultado = cal(recibido);
		  	            
		  	            System.out.println("Enviar a cliente: >>>" + resultado);
			  	            
			  	          if(Character.getNumericValue(cbuf_aux[1])==-1) { // clientes java
			  	        	out.writeUTF(resultado);
			  	          }
			  	          else { // clientes python
			  	        	outw.write(resultado.toCharArray());
				  	        outw.flush();
			  	          }
		  	        
		  	            cbuf = new char[512];
		  	         /* }
		  	          else {
		  	        	server.close();
		  	        	System.out.println("\n>>>> Cliente desconectado ...");
		  	          }*/
	              
	            //  server.close();
	            //  System.out.println("\n>>>> Cliente desconectado ...");
	          
	        }

	    }
	
	  
	  /*public static void main(String[] args) {

        ServerSocket servidor = null;
        Socket sc = null;
        DataInputStream in;
        DataOutputStream out;

        //puerto de nuestro servidor
        final int PUERTO = 6000;

        try {
            //Creamos el socket del servidor
            servidor = new ServerSocket(PUERTO);
            System.out.println(">>>> Servidor Calculadora iniciado");

            //Siempre estara escuchando peticiones
            while (true) {

                //Espero a que un cliente se conecte
                sc = servidor.accept();

                System.out.println("\n>>>> Cliente conectado");
                in = new DataInputStream(sc.getInputStream());
                out = new DataOutputStream(sc.getOutputStream());


              String peticion = "s";                                       
              while (peticion.equals("s") || peticion.equals("S")){
            	  
            	  
                	System.out.print( "\n>>>> El cliente esta realizando una operacion ... " );
                	String operacion = in.readUTF();
                	
                	//calculo
                	String resultado = cal(operacion);
                	
                    //Le envio el resultado al cliente
                    out.writeUTF(resultado);
                                                     
                    //Leo el mensaje que me envia para ver si quiere realizar otra operacion
                    peticion = in.readUTF();	
                }
                                                 
                 	
            	//Cierro el socket
                sc.close();
                System.out.println("\n>>>> Cliente desconectado ...");
           
            
            }
                   


        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }*/

}
