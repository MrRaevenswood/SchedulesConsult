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
        @FXML
        private TextField txt_Password;
        @FXML
        private RadioButton rb_SpanishLogin;
        @FXML
        private RadioButton rb_EnglishLogIn;
    
	private String userName;
	private String passWord;
	private String language;
	private boolean active;
	private Date appointmentReminders;
        
        public void createNewUser() throws IOException{
            Scene newUser = new Scene(FXMLLoader.load(getClass().getResource("addNewUser.fxml")));
            Stage newUserStage = new Stage();
                        
            newUserStage.setScene(newUser);
            newUserStage.show();         
        }
        
        
        public void logInAttempt() throws IOException, ClassNotFoundException{
            LogIn newLogin = new LogIn();
            String logInLanguage = "";
            newLogin.setUserName(txt_UserName.getText());
            newLogin.setPassword(txt_Password.getText());
            
            if(rb_SpanishLogin.isSelected()){
                logInLanguage = "Spanish";
            }
            
            if(rb_EnglishLogIn.isSelected()){
                logInLanguage = "English";
            }
            Connection dbConn = null;
            newLogin.setLanguage(logInLanguage);
            //figure out translation later
            try {
                
                Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                String dbUserQuery = "Select userName, password From user Where userName = '" +  
                        newLogin.userName + "' And password = '" + newLogin.passWord + "'";
                ResultSet userExists = stmt.executeQuery(dbUserQuery);
                
                if(!userExists.next()){
                    
                    Alert userNotFound = new Alert(Alert.AlertType.ERROR);
                   
                   if(newLogin.getLanguage() == "English"){
                        userNotFound.setTitle("Error Connecting to Database");
                        userNotFound.setHeaderText("Error");
                        userNotFound.setContentText("User name / Password Combination was not found in the system");
                   } else if(newLogin.getLanguage() == "Spanish"){
                       userNotFound.setTitle("Error al conectarse a la base de datos");
                       userNotFound.setHeaderText("Error");
                       userNotFound.setContentText("La combinación nombre de usuario / contraseña no se encontró en el sistema");
                   }
                   
                   userNotFound.showAndWait();
                   dbConn.close();
                   return;
                    
                }else{
                       
                        dbConn.close();
                        
                        SchedulesConsult.currentLogIn = newLogin.getUserName();
                        
                        Scene calendar = new Scene(FXMLLoader.load(getClass().getResource("Calendar.fxml")));
                        Stage appointmentStage = new Stage();
                        
                        appointmentStage.setScene(calendar);
                        appointmentStage.show();
                        
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
            } finally {
                try {
                    dbConn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        
        public void setUserName(String userName){
            this.userName = userName;
        }
        
        public void setPassword(String password){
            this.passWord = password;
        }
        
        public void setLanguage(String language){
            this.language = language;
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