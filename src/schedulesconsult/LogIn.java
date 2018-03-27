package schedulesconsult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LogIn {

        //FXML Variables for LogIn Form
        @FXML
        private TextField txt_UserName;
        private TextField txt_Password;
        private RadioButton rb_SpanishLogin;
        private RadioButton rb_EnglishLogin;
        private Button bt_Login;
        private Button bt_CancelLogin;
    
	private String userName;
	private String passWord;
	private String language;
	private boolean active;
	private Date appointmentReminders;
        
        public void logInAttempt() throws IOException{
            LogIn newLogin = new LogIn();
            
            newLogin.setUserName();
            newLogin.setPassword();
            
            //SQLConnection Class Test
            
            newLogin.setLanguage();
            //figure out translation later
            try {
                Connection dbConn =  DriverManager.getConnection(SchedulesConsult.databaseConnectionString);
                Statement stmt = dbConn.createStatement();
                ResultSet userExists = stmt.executeQuery("Select userName, password From user Where userName = " + newLogin.userName);
                
                if(userExists.equals(newLogin.userName)){
                    ResultSet passwordCorrect = stmt.executeQuery("Select password From user Where password = " + newLogin.passWord);
                    
                    if(passwordCorrect.equals(newLogin.passWord)){
                       
                        Scene calendar = new Scene(FXMLLoader.load(getClass().getResource("LogIn.fxml")));
                        Stage appointmentStage = new Stage();
                        
                        appointmentStage.setScene(calendar);
                        appointmentStage.show();
                        
                    }else{
                        Alert wrongPassword = new Alert(Alert.AlertType.ERROR);
                        
                        if(newLogin.getLanguage() == "English"){
                            wrongPassword.setTitle("Error Connecting to Database");
                            wrongPassword.setHeaderText("Error");
                            wrongPassword.setContentText("Given password does not match the one for this user account");
                        } else if(newLogin.getLanguage() == "Spanish") {  
                            wrongPassword.setTitle("Error al conectarse a la base de datos");
                            wrongPassword.setHeaderText("Error");
                            wrongPassword.setContentText("La contraseña dada no coincide con la de esta cuenta de usuario");
                        }
                        
                        wrongPassword.showAndWait();
                        return;
                    }
                    
                }else{
                   Alert wrongUserName = new Alert(Alert.AlertType.ERROR);
                   
                   if(newLogin.getLanguage() == "English"){
                        wrongUserName.setTitle("Error Connecting to Database");
                        wrongUserName.setHeaderText("Error");
                        wrongUserName.setContentText("User name was not found in the system");
                   } else if(newLogin.getLanguage() == "Spanish"){
                       wrongUserName.setTitle("Error al conectarse a la base de datos");
                       wrongUserName.setHeaderText("Error");
                       wrongUserName.setContentText("El nombre de usuario no se encontró en el sistema");
                   }
                   
                   wrongUserName.showAndWait();
                   return;    
                   
                }
                
                
            } catch (SQLException ex) {
                Alert sqlException = new Alert(Alert.AlertType.ERROR);
                
                if(newLogin.getLanguage() == "English"){
                    sqlException.setTitle("Error Connecting to Database");
                    sqlException.setHeaderText("Error");
                    sqlException.setContentText("There was an error connecting to the database that is likely due to the network.");
                } else if(newLogin.getLanguage() == "Spanish"){
                    sqlException.setTitle("Error al conectarse a la base de datos");
                    sqlException.setHeaderText("Error");
                    sqlException.setContentText("Hubo un error al conectarse a la base de datos que probablemente se deba a la red.");
                }
                
                sqlException.showAndWait();
                
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        // Getter and Setter Methods
	public String getUserName() {
		return userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public String getLanguage() {
		return language;
	}



	public boolean getActive() {
		return active;
	}
        
        public void setUserName(){
            this.userName = txt_UserName.toString();
        }
        
        public void setPassword(){
            this.passWord = txt_Password.toString();
        }
        
        public void setLanguage(){
            if(rb_SpanishLogin.isSelected()){
                this.language = "Spanish";
            }
            
            if(rb_EnglishLogin.isSelected()){
                this.language = "English";
            }
        }

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getAppointmentReminders() {
		return appointmentReminders;
	}


	public void setAppointmentReminders(Date appointmentReminders) {
		this.appointmentReminders = appointmentReminders;
	}
        
        public void close(){
             System.exit(0);
        }

}