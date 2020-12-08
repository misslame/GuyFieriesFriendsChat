
package chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

// Class that handles responses from the server 
// THREAD
class ReceiveMsg extends Thread {
    private BufferedReader reader;
    
    // constructor - create all necessary aspects of thread
    public ReceiveMsg(Socket socket) {
        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // on start() thread method
    // When a message from server is received: 
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                if(response != null){ // if null the server disconnected
                    //debug
                    System.out.println(response);
                    
                    Platform.runLater(new Runnable() { // for fxml controller
                        @Override 
                        public void run() {
                            ClientController.recieveMessage(response);   
                        }   
                    });
                }else{
                    // server disconnected
                    Platform.runLater(new Runnable() { // for fxml controller
                        @Override 
                        public void run() { 
                            ClientController.lostConnectionFlag();   
                        }   
                    });
                    
                    Thread.sleep(100000); // sleep before sending message again. (prevent spam and ram overload)
                }

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(ReceiveMsg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}