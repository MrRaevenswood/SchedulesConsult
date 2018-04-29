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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LogIn implements Initializable{

        //FXML Variables for LogIn Form
        @FXML
        private TextField txt_UserName;
        @FXML
        private PasswordField pass_Password;
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
        
        BiConsumer<String,String> scheduleReminderPop = (t,c) -> {
            Alert newReminder = new Alert(Alert.AlertType.INFORMATION);
            newReminder.setTitle(t);
            newReminder.setContentText(c);
            newReminder.showAndWait();
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
            int userId;
            
            Locale loginLocale = Locale.getDefault();
           
            if(loginLocale.getLanguage().equals(new Locale("es").getLanguage())){
                logInLanguage = "Spanish";
                SchedulesConsult.isSpanish = true;
                SchedulesConsult.isEnglish = false;
            }
            
            if(loginLocale.getLanguage().equals(new Locale("en").getLanguage())){
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
                String dbUserQuery = "Select userId, userName, password From user Where userName = '" +  
                        newLogin.userName + "' And password = '" + newLogin.passWord + "'";
                ResultSet userExists = stmt.executeQuery(dbUserQuery);
                
                if(!userExists.next()){
                   
                   if(newLogin.getLanguage() == "English"){
                       
                       alertPop.accept("Error Connecting to Database", "User name / Password Combination was not found in the system");

                   } else if(newLogin.getLanguage() == "Spanish"){
                       
                       alertPop.accept("Error al conectarse a la base de datos", "La combinación nombre de usuario / contraseña no se encontró en el sistema");

                   }
                 
                   dbConn.close();
                   return;
                    
                }else{
                       
                        SchedulesConsult.currentUserId = userExists.getInt(1);
                        startApptReminder(SchedulesConsult.currentUserId,dbConn);
                        
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
            } catch (ParseException ex) {
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
                    appendLog.println("User: " + logIn + " logged in to the system at " + LocalDateTime.now().atZone(ZoneId.systemDefault()));
                }    
            }else{
                userLogFile = new FileHandler("C:\\Users\\Public\\Documents\\userLog.log");
                userLog.addHandler(userLogFile);
                SimpleFormatter logFormat = new SimpleFormatter();
                userLogFile.setFormatter(logFormat);

                userLog.info("User: " + logIn + " logged in to the system at " + LocalDateTime.now().atZone(ZoneId.systemDefault()));
            }  
        }
        
        public void startApptReminder(int userId, Connection dbConn) throws SQLException, ParseException{

            String getUserSchedulesQuery = "Select contact, title, start From appointment Where userId = " + userId;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int millisecondsUntilAppt;
            int fifteenMinsInMilliSeconds = 15 * 60 * 1000;
            
            
                Statement stmt = dbConn.createStatement();
                ResultSet schedulesForUser = stmt.executeQuery(getUserSchedulesQuery);
                
                while(schedulesForUser.next()){
                    
                    millisecondsUntilAppt = calculateSecondsTillAppointment(
                            format.parse(schedulesForUser.getString(3)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                            fifteenMinsInMilliSeconds);
                    
                    if(millisecondsUntilAppt > fifteenMinsInMilliSeconds){
                        startApptTimer(schedulesForUser.getString(1), schedulesForUser.getString(2), millisecondsUntilAppt);
                    }else if (millisecondsUntilAppt <= fifteenMinsInMilliSeconds && millisecondsUntilAppt >= 0){
                        scheduleReminderPop.accept(schedulesForUser.getString(2) + " appointment In 15 Minutes for user " + SchedulesConsult.currentLogIn , "Please prepare for your appointment,with "
                        + schedulesForUser.getString(1) + " ,which is in 15 minutes");
                    }

                }
            
        }
        
        public int calculateSecondsTillAppointment(LocalDateTime apptTime, int fifteenMinsInMilliSeconds){

            int delayInMilliseconds = (int) ((apptTime.toEpochSecond(ZoneOffset.UTC) - 
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) * 1000);;
            return delayInMilliseconds;
        }
        
        public void startApptTimer(String contact, String title, int delayInMilliseconds){
                
                Timeline apptAlarm = new Timeline(new KeyFrame(Duration.millis(delayInMilliseconds),
                ae -> scheduleReminderPop.accept(title + " appointment In 15 Minutes for user " + SchedulesConsult.currentLogIn , "Please prepare for your appointment,with "
                        + contact + " ,which is in 15 minutes")));
                
                apptAlarm.play();
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
        Locale country = Locale.getDefault();
        
        if(country.getLanguage().equals(new Locale("es"))){
            SchedulesConsult.isEnglish = false;
            SchedulesConsult.isSpanish = true;
        }else if(country.getLanguage().equals(new Locale("en"))){
            SchedulesConsult.isEnglish = true;
            SchedulesConsult.isSpanish = false;
        }
        
        lbl_UserLocation.setText("User Location is: " + country.getCountry() + " "
                + "on the " + locationOfUser.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " Zone");
    }

}