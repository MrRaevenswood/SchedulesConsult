package schedulesconsult;

import java.util.Date;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        
        public void logInAttempt(){
            LogIn newLogin = new LogIn();
            
            newLogin.setUserName();
            newLogin.setPassword();
            
            //SQLConnection Class Test
            
            newLogin.setLanguage();
            //figure out translation later
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