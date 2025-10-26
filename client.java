/**
 * Client - Gestione Connessione Socket e Comunicazione con Server
 * 
 * Gestisce connessione socket, invio/ricezione messaggi su thread separati
 * e pattern Observer (MessageListener) per notifiche alla GUI.
 * 
 * @author [Il Tuo Nome]
 * @version 1.0
 */

import java.io.*;
import java.net.*;
import java.util.Scanner; 


public class client{

    Socket c1;
    int port;
    String ip;
    boolean connesso = false;
    PrintWriter out;
    BufferedReader in;
    String nome;
    
    // Pattern Observer per notificare GUI di nuovi messaggi
    public interface MessageListener {
        void onMessageReceived(String Message); 
    }  
    private MessageListener messageListener; 
    

    public client(String ip, int port, String nome){
        this.port = port;
        this.ip = ip; 
        this.nome = nome; 
    }

    public void connectToServer(){
        try{
            this.c1 = new Socket(this.ip, this.port);
            connesso = true; 
            out = new PrintWriter(c1.getOutputStream(), true);  // autoFlush=true
            in = new BufferedReader(new InputStreamReader(c1.getInputStream())); 
            
        } catch(IOException e){
            System.out.println("Impossibile connettersi al server: " + e.getMessage()); 
            connesso = false; 
        }

        if(connesso){
            // Thread per input console (modalità standalone)
            Thread Chat = new Thread(() -> avviaChatToServer()); 
            Chat.start();

            // Thread per ricezione continua messaggi
            Thread Leggimessaggi = new Thread(() -> leggiMsg()); 
            Leggimessaggi.start(); 
        }
    }

    public void setMessageListener(MessageListener listener){
        this.messageListener = listener;
    }


    // Input da console per modalità standalone
    public void avviaChatToServer(){
        try (Scanner leggiInput = new Scanner(System.in)) {
            String msg;
            while (connesso) {
                System.out.println("Scrivi Messaggio: "); 
                msg = leggiInput.nextLine();
                inviaMessaggio(msg); 
            }
        }
    }

    public String getNome() {
        return nome;
    }

    public void inviaMessaggio(String msg){ 
        out.println("[" + nome + "]: " + msg);
        out.flush();
    }


    // Thread per ricezione continua messaggi dal server
    // Notifica listener (GUI) usando Platform.runLater per thread-safety
    public void leggiMsg(){
        while (true) {
            // Attende inizializzazione listener
            while (messageListener == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            try{
                String msg; 
                while ((msg = in.readLine()) != null){
                    System.out.println(msg);
                    
                    if (messageListener != null) {
                        final String finalMsg = msg;
                        messageListener.onMessageReceived(finalMsg);
                    }
                }
                        
            } catch(IOException e){
                System.out.println(e.getMessage()); 
            } 
        }
    }

    public static void main(String[] args){
        client c1 = new client("127.0.0.1", 5000, "bigs"); 
        c1.connectToServer();
    }
}