/**
 * ChatGUIclient - Interfaccia Grafica Client per Chat Application
 * 
 * Implementa l'UI JavaFX per il client con pattern MVC.
 * Utilizza Platform.runLater() per aggiornamenti thread-safe della GUI.
 * 
 * @author [Il Tuo Nome]
 * @version 1.0
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Optional;

public class ChatGUIclient extends Application {

    private client chatClient;
    private TextArea chatArea;
    private TextField inputField;
    
    // Configurazione connessione (configurabile dall'utente)
    private String serverIP = "127.0.0.1";
    private int serverPort = 5000;
    private String username = "User"; 


    @Override
    public void start(Stage stage) {
        
        // Mostra dialog configurazione connessione
        if (!showConnectionDialog()) {
            Platform.exit();
            return;
        }
        
        // Connessione al server con parametri configurati
        chatClient = new client(serverIP, serverPort, username); 
        chatClient.connectToServer();

        // Setup chat area
        chatArea = new TextArea(); 
        chatArea.setEditable(false);
        chatArea.setPrefSize(400, 700);
        chatArea.setMaxSize(400, 700);
        chatArea.setFocusTraversable(false);
        chatArea.setMouseTransparent(true);
        chatArea.setBorder(null);
        
        // Listener per messaggi in arrivo (usa Platform.runLater per thread-safety)
        chatClient.setMessageListener(new client.MessageListener() {
            @Override
            public void onMessageReceived(String message) {
                Platform.runLater(() -> chatArea.appendText(message + "\n"));
            }
        });
        
        // Styling tema scuro
        chatArea.setStyle(
            "-fx-control-inner-background: #1a1a1a;" +
            "-fx-background-color: #1a1a1a;" +
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Arial';" +
            "-fx-font-size: 14px;" +
            "-fx-margin-bottom: 14;" +
            "-fx-border: 0;"
        );

        // Sfondo decorativo
        Rectangle chatBackground = new Rectangle(400, 800); 
        chatBackground.setFill(Color.BLACK);
        chatBackground.setOpacity(0.9);
        
        // Campo input messaggi
        inputField = new TextField();
        inputField.setPrefWidth(50);
        inputField.setPromptText("Scrivi un messaggio...");
        inputField.setMaxWidth(350);
        inputField.setMaxHeight(70);
        inputField.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.2);" +
            "-fx-border-color: white;" +
            "-fx-border-width: 0.2;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 12.5;" +
            "-fx-border-radius: 12.5;" +
            "-fx-font-size: 12;"
        );
        inputField.setOnAction(e -> getInputfield());
        
        // Layout e posizionamento
        StackPane root = new StackPane();
        StackPane.setMargin(inputField, new Insets(0, 0, 16, 0));
        StackPane.setMargin(chatArea, new Insets(0, 0, 85, 0));

        root.setAlignment(Pos.CENTER);
        root.getChildren().add(chatBackground);
        root.getChildren().add(chatArea);
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.getChildren().add(inputField);
        
        Scene ChatUtenteView = new Scene(root, 400, 800);
        stage.setScene(ChatUtenteView);
        stage.setTitle("Chat Client");
        stage.setResizable(false);
        stage.show();
    }

    // Gestisce invio messaggio
    public void getInputfield(){
        if(!inputField.getText().isEmpty()){
            String msg = inputField.getText().trim();
            chatClient.inviaMessaggio(msg);
            inputField.clear();
            chatArea.appendText("[" + username + "]: " + msg + "\n");
        }
    }

    /**
     * Mostra dialog per configurazione connessione
     * Permette all'utente di inserire IP server, porta e username
     * 
     * @return true se l'utente ha confermato, false se ha cancellato
     */
    private boolean showConnectionDialog() {
        // ========== CREA FORM DI CONFIGURAZIONE ==========
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
     


        TextField ipField = new TextField(serverIP);
        ipField.setPromptText("Server IP");
        
        TextField portField = new TextField(String.valueOf(serverPort));
        portField.setPromptText("Porta");
        
        TextField usernameField = new TextField(username);
        usernameField.setPromptText("Username");

        grid.add(new Label("IP Server:"), 0, 0);
        grid.add(ipField, 1, 0);
        grid.add(new Label("Porta:"), 0, 1);
        grid.add(portField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Configurazione Connessione");
        dialog.setHeaderText("Inserisci i parametri di connessione al server");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

       
     
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serverIP = ipField.getText().trim();
                serverPort = Integer.parseInt(portField.getText().trim());
                username = usernameField.getText().trim();
                
                if (serverIP.isEmpty() || username.isEmpty()) {
                    System.err.println("IP e username non possono essere vuoti!");
                    return false;
                }
                
                if (serverPort < 1 || serverPort > 65535) {
                    System.err.println("Porta deve essere tra 1 e 65535!");
                    return false;
                }
                
                return true;
            } catch (NumberFormatException e) {
                System.err.println("Porta non valida!");
                return false;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }
}