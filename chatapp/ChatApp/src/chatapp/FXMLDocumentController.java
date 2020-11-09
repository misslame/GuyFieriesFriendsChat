
package chatapp;
 
// Client connection imports:
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;


public class FXMLDocumentController implements Initializable {
    
    static private List<HBox> messages;
    
    // Client Attributes
    private String ip;
    private int port;
    private Socket socket; 
    private OutputStream out;
    private PrintWriter writer;
    
    // UI Attributes
    @FXML
    private ScrollPane container;
    @FXML
    private TextField textMsg;
    static private VBox chatBox = new VBox(5);
    
    @FXML
    private void handleSendAction(ActionEvent event) {
       sendMessage();
    }
    
    @FXML
    private void onEnter(KeyEvent ke){
        if(ke.getCode().equals(KeyCode.ENTER)){
           sendMessage(); 
        }
    }
    
    private void sendMessage(){
        if(textMsg.getText() != null && textMsg.getText().length() > 0){
            HBox align = new HBox();
            align.setPadding(new Insets(5,5,5,5));
            Label l = new Label(textMsg.getText());
            l.setAlignment(Pos.CENTER_RIGHT);
            l.setFont(new Font("Comic Sans MS", 16));
            l.setWrapText(true);
            l.setMaxWidth(250);


            //timestamp
            String time = String.valueOf(new Date().getTime());
            Label timestamp = new Label(time);
            timestamp.setFont(new Font("Comic Sans MS", 8));
            timestamp.setStyle("-fx-background-color:#333333");
            align.getChildren().add(timestamp);

            align.getChildren().add(l);
            align.setAlignment(Pos.BASELINE_RIGHT);

            messages.add(align);
            chatBox.getChildren().add(messages.get(messages.size()-1));

            writer.println(textMsg.getText());
            textMsg.clear();
        }
    }
    
    
    @FXML
    static void recieveMessage(String response){
        HBox align = new HBox();
        Label l = new Label(response);
        l.setAlignment(Pos.CENTER_RIGHT);
        l.setFont(new Font("Comic Sans MS", 16));
        align.getChildren().add(l);
        align.setAlignment(Pos.BASELINE_LEFT);
        
        messages.add(align);
        chatBox.getChildren().add(messages.get(messages.size()-1));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        messages = new ArrayList<>();
        container.setPrefSize(392, 501);
        container.setHbarPolicy(ScrollBarPolicy.NEVER);
        container.setContent(chatBox);
        chatBox.getStyleClass().add("chatbox");
        
        readServerInfo();
        
        try{
           socket = new Socket(ip, port);
           out = socket.getOutputStream();
           writer = new PrintWriter(out, true);
           
           Label l = new Label("You have established a connection!");
           l.setStyle("-fx-background-color:#333333;");
           chatBox.getChildren().add(l);
           
           Thread r = new ReceiveMsg(socket);
           r.start();
           
        }catch(UnknownHostException ex){
            System.out.println("Server not found: " + ex.getMessage());
        }catch(IOException ex){
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }   
    
    
    void readServerInfo(){
        try {
            // Debug
            System.out.println(System.getProperty("user.dir"));
            
            File file = new File("src/assets/server_info.txt");
            Scanner in = new Scanner(file);
            
            if(in.hasNext()){
                ip = in.next();
            }
            
            if(in.hasNext()){
                port = in.nextInt();
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
}
