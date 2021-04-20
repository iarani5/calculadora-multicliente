package calculadora_SSDD_TP3;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
/*
	 private boolean compareSymbols(String newSymbol,String existSymbol){
	        
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
    private  LinkedList<String> getNewExpre(String[] expression){
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

	  private String cal(String str){
		  
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
	  */
	 
   // Socket s = null;
   // Servidor ss2 = null;
    
	  public static void main(String[] args) throws IOException {
     	 ServerSocket server = new ServerSocket(port);
	   
	    	 while(true){
	             try{
	     	         System.out.println("Esperando cliente");
	     	    	 Socket cli = server.accept();
	                 System.out.println( " THE CLIENT"+" "+ cli.getInetAddress() +":"+cli.getPort()+" IS CONNECTED ");
	                 ServerThread st = new ServerThread(cli);
	                 st.start();
	             }
	             catch(Exception e){
	                 e.printStackTrace();
	                 System.out.println("Connection Error");
	             }
	         }

	    	 
	    	//s = ss2.accept();

   	      //  Servidor servidor = new Servidor(cli);
   	      //  servidor.start();
   	        
	      /*  OutputStreamWriter outw = new OutputStreamWriter(cli.getOutputStream(), "UTF8");
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
		  	         
		  	            System.out.println("Cliente dice: " + recibido);
		  	          
		  	            //calculo
		  	            Calculadora calcu = new Calculadora();
		              	String resultado = calcu.cal(recibido);
		  	            
		  	            System.out.println("Enviar a cliente: >>>" + resultado);
			  	            
			  	          if(Character.getNumericValue(cbuf_aux[1])==-1) { // clientes java
			  	        	out.writeUTF(resultado);
			  	          }
			  	          else { // clientes python
			  	        	outw.write(resultado.toCharArray());
				  	        outw.flush();
			  	          }
		  	        
		  	            cbuf = new char[512];
		  	          
		  	          if(recibido.indexOf("salir") != -1) {
		  	        	server.close();
		  	        	System.out.println("\n>>>> Cliente desconectado ...");
		  	        	 break;
		  	          }
	        }*/

	 }
}
	  


class ServerThread extends Thread{

	   String line=null;
	   OutputStreamWriter outw = null;
	   InputStreamReader inw = null; 
	   DataOutputStream out = null;
	   Socket s=null;

	   public ServerThread(Socket s){
	       this.s=s;
	   }

	   public void run() {
	       try{
	    	   outw = new OutputStreamWriter(s.getOutputStream(), "UTF8");
	           inw = new InputStreamReader(s.getInputStream(), "UTF8");
	           out = new DataOutputStream(s.getOutputStream());
	           
	       }catch(IOException e){
	           System.out.println("IO error in server thread");
	       }

	       try {
	    	    char[] cbuf = new char[512];
	            char[] cbuf_aux =  cbuf; 
	            while (true) {
	            	System.out.println("Esperando mensaje del cliente");
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
		  	         
		  	            System.out.println("Cliente dice: " + recibido);
		  	          
		  	            //calculo
		  	            Calculadora calcu = new Calculadora();
		              	String resultado = calcu.cal(recibido);
		  	            
		  	            System.out.println("Enviar a cliente: >>>" + resultado);
			  	            
			  	          if(Character.getNumericValue(cbuf_aux[1])==-1) { // clientes java
			  	        	out.writeUTF(resultado);
			  	          }
			  	          else { // clientes python
			  	        	outw.write(resultado.toCharArray());
				  	        outw.flush();
			  	          }
			  	        /*if(recibido.indexOf("salir") != -1) {
			  	        	server.close();
			  	        	System.out.println("\n>>>> Cliente desconectado ...");
			  	        	 break;
			  	          }*/
		  	            cbuf = new char[512];
		  	          
	            }
	          /* String result;
	           line=is.readLine();
	           while(line.compareTo("QUIT")!=0){
	               result = calculate.cal(line);
	               os.println(result);
	               os.flush();
	               System.out.println("Response to Client  :  "+line);
	               line=is.readLine();
	           }*/
	       } catch (IOException e) {
	           line=this.getName();
	           System.out.println("IO Error/ Client "+line+" terminated abruptly");
	       }
	       catch(NullPointerException e){
	           line=this.getName();
	           System.out.println("Client "+line+" Closed");
	       }

	       finally{
	           try{
	               System.out.println("Connection Closing..");
	               if (inw!=null){
	                   inw.close();
	                   System.out.println(" Socket Input Stream Closed");
	               }
	               if(outw!=null){
	                   outw.close();
	                   System.out.println("Socket Out Closed");
	               }
	               if(out!=null){
	                   out.close();
	                   System.out.println("Socket Out Closed");
	               }
	               if (s!=null){
	                   s.close();
	                   System.out.println("Socket Closed");
	               }
	           }
	           catch(IOException ie){
	               System.out.println("Socket Close Error");
	           }
	       }
	   }
	}

