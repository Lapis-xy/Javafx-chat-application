/**
 * Server - Multi-Client Chat Server con Broadcast Messaggi
 * 
 * Implementa server che gestisce connessioni multiple con HashMap per stream I/O.
 * Thread-per-client model per ricezione asincrona, broadcast a tutti i client.
 * 
 * @author [Il Tuo Nome]
 * @version 1.0
 */

import java.net.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.*;


public class server{

    ServerSocket S1;
    int port;
    ArrayList<Socket> client = new ArrayList<>();
    int userCounter = 0;
    Socket nuovoUser;
    
    // HashMap per gestire stream I/O per ogni client (accesso O(1))
    Map<Socket, PrintWriter> clientWriters = new HashMap<>();
    Map<Socket, BufferedReader> clientReaders = new HashMap<>(); 
    public server(int p){
        this.port = p; 
    }

    public void startServer(){
        try{
            this.S1 = new ServerSocket(port); 
            System.out.println("Server avviato: " + port); 
            System.out.println("server in ascolto");
           
            // Thread per accettare nuove connessioni
            Thread accettaUser = new Thread(() -> clientaccept()); 
            accettaUser.start();
                
        } catch (IOException e) {
            System.out.println("Errore Server non avviato: " + e.getMessage());
        }
    }

    // Accetta nuove connessioni e crea stream I/O per ogni client
    // Avvia thread dedicato per ricezione messaggi da ogni client
    public void clientaccept(){
        while (true) {
            // Avvia chat server dopo primo client connesso
            if (client.size() == 1) {
                Thread inviamsg = new Thread(() -> avviaChat()); 
                inviamsg.start();  
            }
            
            try {
                nuovoUser = this.S1.accept(); 
                this.client.add(nuovoUser);
                System.out.println("Nuovo utente connesso: " + nuovoUser.getInetAddress()); 
                
                // Inizializza stream I/O per questo client
                BufferedReader Reader = new BufferedReader(
                    new InputStreamReader(nuovoUser.getInputStream())
                ); 
                clientReaders.put(nuovoUser, Reader);

                PrintWriter writer = new PrintWriter(nuovoUser.getOutputStream(), true); 
                clientWriters.put(nuovoUser, writer);
                
                // Thread di lettura dedicato per questo client
                Thread leggiMessagg = new Thread(() -> leggiMsg(nuovoUser)); 
                leggiMessagg.start();
                 
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
    }


    // Thread per leggere messaggi da un client specifico
    public void leggiMsg(Socket currentClient){
        while (true) {
            // Attende inizializzazione reader
            while (clientReaders.get(currentClient) == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            try{
                String msg; 
                while ((msg = clientReaders.get(currentClient).readLine()) != null){
                    System.out.println(msg);
                }
                
            } catch(IOException e){
                System.out.println(e.getMessage()); 
            } 
        }
    }

    // Input console server per broadcast messaggi
    public void avviaChat(){
        try (Scanner leggiInput = new Scanner(System.in)) {
            String msg;
            while (true) {
                System.out.println("[Server]: "); 
                msg = leggiInput.nextLine();
                inviaMsg(msg);
            }
        }
    }

    // Broadcast messaggio a TUTTI i client connessi
    public void inviaMsg(String msg){
        for (Socket socket : client) {
            try {
                if (clientWriters.get(socket) != null) {
                    clientWriters.get(socket).println("[Server]: " + msg);
                    clientWriters.get(socket).flush();
                }
            } catch (Exception e) {
                System.out.println("Errore invio messaggio: " + e.getMessage());
                client.remove(socket);
            }
        }
    }

    public static void main(String[] args){

        Scanner scanner = new Scanner(System.in);
            System.out.println("insersici la porta");
     
            int porta = scanner.nextInt(); 
            while (porta < 1 || porta > 65535) {
                System.err.println("Porta deve essere tra 1 e 65535");
                System.out.println("insersici la porta");
                porta = scanner.nextInt(); 
            }
     
                     
            server s = new server(porta);
            s.startServer();
        
    }
}

