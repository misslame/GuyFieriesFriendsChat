
package chatapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController implements Initializable {


    @FXML
    private TextField nickname;
    
    @FXML
    private PasswordField password;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      
    } 
    
    @FXML
    public void onSend(Event event){
        if(nickname.getText() != null && nickname.getText().length() > 0){
            if(password.getText().equals("flavortown")){
                try {
                    FXMLDocumentController.setUsername(nickname.getText());

                    Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

                    Scene scene = new Scene(root);

                    // This will get the stage:
                    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.setResizable(false);
                    window.show();
                    FXMLDocumentController.sendUserName();
                } catch (IOException ex) {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                // ALERT INCORRECT PASSWORD> 
                Alert a = new Alert(AlertType.NONE, "That was not the sauce! Wrong password, bro!",ButtonType.OK); 

                // show the dialog 
                a.show(); 

            }
        }else{
            // ALERT EMPTY NICKNAME
            Alert a = new Alert(AlertType.NONE, "You can\'t have an empty nickname!", ButtonType.OK);
            
            // show the dialog
            a.show();
        }
        
        
    }
    
}
