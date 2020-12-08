
package chatapp;
 
// Client connection imports:
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

// UI:
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

// Other:
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TitledPane;


public class ClientController implements Initializable {
    private static String clientUsername;

    // Client Attributes
    private String ip;
    private int port;
    private Socket socket; 
    private OutputStream out;
    private static PrintWriter writer;
    
    // UI Attributes
    @FXML private ScrollPane container;
    @FXML private TextField textMsg;
    @FXML private TitledPane title;
    static private VBox chatBox = new VBox(5);
   
    // Statically called from the login controller class to set nickname
    public static void setUsername(String name){
        clientUsername = name;
    }
    
    @Override // On start method.
    public void initialize(URL url, ResourceBundle rb) {
        container.setPrefSize(392, 501);
        container.setHbarPolicy(ScrollBarPolicy.NEVER);
        container.setContent(chatBox);
        chatBox.getStyleClass().add("chatbox");
        
        container.vvalueProperty().bind(chatBox.heightProperty());
        
        title.setText("Guy Fieri's Friends: " + clientUsername);
        
        readServerInfo(); // Reads the file for IP/Port to connect to.
        // Initializes IP and Port Variables. 
        try{
           socket = new Socket(ip, port);
           
           // Where messages are sent. (send)
           out = socket.getOutputStream();
           writer = new PrintWriter(out, true);
           
           // Send User nickname to server
           writer.print(formatPostRequest(clientUsername, "/nickname"));
           writer.flush();
           
           // If the above are successful (connected) let user know. 
           Label l = new Label("You have established a connection!");
           l.setStyle("-fx-background-color:#333333;");
           chatBox.getChildren().add(l);
           // Start thread for waiting for incoming messages/updates. 
           Thread r = new ReceiveMsg(socket);
           r.start();
           
        }catch(UnknownHostException ex){
            System.out.println("Server not found: " + ex.getMessage());
        }catch(IOException ex){
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }   
    
    // Reads from an assets file the server information to connect to. 
    void readServerInfo(){
        try {
            File file = new File("src/assets/server_info.txt");
            Scanner in = new Scanner(file);
            
            if(in.hasNext()){
                ip = in.next();
            }
            
            if(in.hasNext()){
                port = in.nextInt();
            }
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    /*************************************
        UI: Action Methods
    *************************************/
    @FXML // When the send button is pressed.
    private void handleSendAction(ActionEvent event) {
       sendMessage();
    }
    
    @FXML // When a user presses enter after typing a message. 
    private void onEnter(KeyEvent ke){
        if(ke.getCode().equals(KeyCode.ENTER)){
           sendMessage(); 
        }
    }
    
    // Send message from current client. 
    private void sendMessage(){
        if(textMsg.getText() != null && textMsg.getText().length() > 0){
            
            // Initialize label with message in it. 
            Label message = new Label(textMsg.getText());
            message.setAlignment(Pos.CENTER_RIGHT);
            message.setFont(new Font("Comic Sans MS", 16));
            message.setWrapText(true);
            message.setMaxWidth(235);


            // Initialize label with timestamp.
            ZonedDateTime time = ZonedDateTime.now();
            DateTimeFormatter timeFormatted = DateTimeFormatter.ofPattern("hh:mm a");
            
            Label timestamp = new Label(time.format(timeFormatted));
            timestamp.setFont(new Font("Comic Sans MS", 8));
            timestamp.setStyle("-fx-background-color:#333333");
            
            // Add message and timestamp to UI. 
            HBox align = new HBox();
            align.setPadding(new Insets(5,5,5,5));
            align.getChildren().add(timestamp); // Add timestamp
            align.getChildren().add(message); // Add message. 
            align.setAlignment(Pos.BASELINE_RIGHT); // Set right

            chatBox.getChildren().add(align); // Adds to screen. 
            
            // Send to server. 
            writer.print(formatPostRequest(textMsg.getText(), "/send_message"));
            writer.flush();
            textMsg.clear(); // clears text input box. 
        }
    }
    
    // formats messages sent in a http request to be sent to the server. 
    private String formatPostRequest(String message, String version){        
        //debug
        System.out.println(message);
        
        return "POST " + version + " HTTP/1.1\n"  + 
               "Host: " + ip + ":" + port + "\n" +
               "Content-Type: text/plain;\n" + 
               "Content-Length: " + message.length() +
               "\nAccept-Language: en-us\n" +
               "Connection: Keep-Alive\n\n" + message + "\n";    
    }



    @FXML // Statically called (RecieveMessageThread)
    static void recieveMessage(String response){
        
        // Initialize label with message in it
        Label message = new Label(response);
        message.setAlignment(Pos.CENTER_LEFT);
        message.setFont(new Font("Comic Sans MS", 16));
        message.setWrapText(true);
        message.setMaxWidth(235);
        
     
        // Initialize label with timestamp.
        ZonedDateTime time = ZonedDateTime.now();
        DateTimeFormatter timeFormatted = DateTimeFormatter.ofPattern("hh:mm a");
        
        Label timestamp = new Label(time.format(timeFormatted));
        timestamp.setFont(new Font("Comic Sans MS", 8));
        timestamp.setStyle("-fx-background-color:#333333");
        
        // Add message and timestamp to UI
        HBox align = new HBox();
        align.setPadding(new Insets(5,5,5,5));
         align.getChildren().add(message);
        align.getChildren().add(timestamp);
        align.setAlignment(Pos.BASELINE_LEFT);
        
        chatBox.getChildren().add(align);
    }
   
    // function for flagging the user if user lost connection to server (server closed)
    static void lostConnectionFlag(){
        // If the above are successful (connected) let user know. 
        Label l = new Label("You have lost connection to the server!");
        l.setStyle("-fx-background-color:#333333;");
        chatBox.getChildren().add(l);
    }
}
