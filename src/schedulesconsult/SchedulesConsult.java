/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulesconsult;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author raevenswood
 */
public class SchedulesConsult extends Application {

    public final static String databaseConnectionString = "jdbc:mysql://52.206.157.109:3306/U04Fby";
    public final static String databaseUser = "U04Fby";
    public final static String databasePassword = "53688222587";
    public static String currentLogIn = "";
    public static Boolean isEnglish = false;
    public static Boolean isSpanish = false;
    public static int currentUserId = 0; 
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        launch(args);
        
        
        
    }
    
}
