package calculadora_SSDD_TP3;

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

// ------------------------ CALCULADORA  

class Calculadora{
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

	  public String cal(String str){
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
}

// -------------------------- SERVIDOR 
public class Servidor extends Thread{
    static int port = 6000;
    
	  public static void main(String[] args) throws IOException {
     	 ServerSocket server = new ServerSocket(port);
	   
	    	 while(true){
	             try{
	     	         System.out.println("Esperando cliente");
	     	    	 Socket cli = server.accept();
	                 System.out.println( "\n>>>> Cliente "+ cli.getInetAddress() +":"+cli.getPort()+" esta actualmente conectado");
	                 ServerThread st = new ServerThread(cli);
	                 st.start();
	             }
	             catch(Exception e){
	                 e.printStackTrace();
	                 System.out.println("Error en la conexion");
	             }
	         }

	 }
}
	  
//-------------------------- THREADS 

class ServerThread extends Thread{

	   String line=null;
	   OutputStreamWriter outw = null;
	   InputStreamReader inw = null; 
	   DataOutputStream out = null;
	   Socket socket=null;

	   //constructor
	   public ServerThread(Socket s){
	       this.socket=s;
	   }

	   public void run() {
	       try{
	    	   outw = new OutputStreamWriter(socket.getOutputStream(), "UTF8");
	           inw = new InputStreamReader(socket.getInputStream(), "UTF8");
	           out = new DataOutputStream(socket.getOutputStream());
	           
	       }catch(IOException ex){
	           Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
	       }

	       try {
	    	    char[] cbuf = new char[512];
	            char[] cbuf_aux =  cbuf; 
  	            Calculadora calcu = new Calculadora();
	            boolean ban = true;
	            
	            while (ban) {
	            	System.out.println("Esperando mensaje del cliente\n");
	            	String recibido = ""; 
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
		  	         
		  	          System.out.println("Cliente "+socket.getInetAddress() +":"+socket.getPort()+" dice: " + recibido);
		  	          
		  	          if(recibido.indexOf("salir") != -1) {
			  	            System.out.println(">>> Cliente "+socket.getInetAddress() +":"+socket.getPort()+" desconectado");
		  	            	//break;
				            //this.socket.close();
			  	          	ban = false;
		  	          }
		  	          else {
		  	        	  //calculo
			              	String resultado = calcu.cal(recibido);
			  	            
			  	            System.out.println(" >>>Enviar a cliente: " + resultado);
				  	            
				  	        if(Character.getNumericValue(cbuf_aux[1])==-1) { // clientes java
				  	        	out.writeUTF(resultado);
				  	        }
				  	        else { // clientes python
				  	        	outw.write(resultado.toCharArray());
					  	        outw.flush();
				  	        }
		  	          }
		  	          
		  	          cbuf = new char[512];
	            }
	 	       ban = true;

	            
	       } 
	       catch (IOException ex) {
	    	   Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
	       }
	       
	       try {
	           inw.close();
	           outw.close();
	           out.close();
	           this.socket.close();
	           System.out.println("\n>>>> Cerrado...");
	       }
	       catch (IOException ex) {
	    	   Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
	       }
	      /* catch(NullPointerException ex){
	    	   Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
	       }*/

	     /*  finally{
	           try{
	               inw.close();
	               outw.close();
	               out.close();
	               //socket.close();
	               this.socket.close();
	               System.out.println("\n>>>> Cerrado...");
	           }
	           catch(IOException ex){
	        	   Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
	           }
	       }*/
	   }
	}

