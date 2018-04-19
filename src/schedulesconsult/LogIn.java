package schedulesconsult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LogIn implements Initializable{

        //FXML Variables for LogIn Form
        @FXML
        private TextField txt_UserName;
        @FXML
        private PasswordField pass_Password;
        @FXML
        private RadioButton rb_SpanishLogin;
        @FXML
        private RadioButton rb_EnglishLogIn;
        @FXML
        private Label lbl_UserLocation;
    
	private String userName;
	private String passWord;
	private String language;
	private boolean active;
	private Date appointmentReminders;
        
        BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.showAndWait();
        };
        
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
            newLogin.setPassword(pass_Password.getText());
            
            if(rb_SpanishLogin.isSelected()){
                logInLanguage = "Spanish";
                SchedulesConsult.isSpanish = true;
                SchedulesConsult.isEnglish = false;
            }
            
            if(rb_EnglishLogIn.isSelected()){
                logInLanguage = "English";
                SchedulesConsult.isEnglish = true;
                SchedulesConsult.isSpanish = false;
            }
            Connection dbConn = null;
            newLogin.setLanguage(logInLanguage);
            
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
                       
                       alertPop.accept("Error Connecting to Database", "User name / Password Combination was not found in the system");

                   } else if(newLogin.getLanguage() == "Spanish"){
                       
                       alertPop.accept("Error al conectarse a la base de datos", "La combinación nombre de usuario / contraseña no se encontró en el sistema");

                   }
                   
                   userNotFound.showAndWait();
                   dbConn.close();
                   return;
                    
                }else{
                       
                        dbConn.close();
                        
                        SchedulesConsult.currentLogIn = newLogin.getUserName();
                        
                        logUserLogIn(SchedulesConsult.currentLogIn);
                        
                        Scene calendar = new Scene(FXMLLoader.load(getClass().getResource("Calendar.fxml")));
                        Stage appointmentStage = new Stage();
                        
                        appointmentStage.setScene(calendar);
                        appointmentStage.show();
                        
                }
                
            } catch (SQLException ex) {
                Alert sqlException = new Alert(Alert.AlertType.ERROR);
                
                if(newLogin.getLanguage() == "English"){
                    
                    alertPop.accept("Error Connecting to Database", "There was an error connecting to the database that is likely due to the network.");

                } else if(newLogin.getLanguage() == "Spanish"){
                    
                    alertPop.accept("Error al conectarse a la base de datos", "Hubo un error al conectarse a la base de datos que probablemente se deba a la red.");

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
        
        public void logUserLogIn(String logIn) throws IOException{
            Logger userLog = Logger.getLogger("SignInLog");
            FileHandler userLogFile;
            File userFile = new File("C:\\Users\\Public\\Documents\\userLog.log");
            
            if(userFile.exists()){
                try(PrintWriter appendLog = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\Public\\Documents\\userLog.log", true)))){
                    appendLog.println("User: " + logIn + " logged in to the system at " + LocalDateTime.now());
                }    
            }else{
                userLogFile = new FileHandler("C:\\Users\\Public\\Documents\\userLog.log");
                userLog.addHandler(userLogFile);
                SimpleFormatter logFormat = new SimpleFormatter();
                userLogFile.setFormatter(logFormat);

                userLog.info("User: " + logIn + " logged in to the system at " + LocalDateTime.now());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ZoneId locationOfUser = ZoneId.systemDefault();
        
        lbl_UserLocation.setText("User Location is: " + locationOfUser.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " Zone");
    }

}