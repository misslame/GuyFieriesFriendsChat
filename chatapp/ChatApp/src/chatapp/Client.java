
package chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import javafx.application.Platform;


class ReceiveMsg extends Thread {
    private BufferedReader reader;

    public ReceiveMsg(Socket socket) {

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            System.out.println("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public void run() {
        while (true) {
            try {
                String response = reader.readLine();
                //debug
                System.out.println("test" + response);
                Platform.runLater(new Runnable() {
                    @Override 
                    public void run() {
                        FXMLDocumentController.recieveMessage(response);   
                    }   
                });

            } catch (IOException ex) {
                System.out.println("Error reading from server: " + ex.getMessage());
                ex.printStackTrace();
                break;
            }
        }
    }
}