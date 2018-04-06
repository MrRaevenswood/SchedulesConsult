package schedulesconsult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class appointment {

	private String appointmentTitle;
	private String appointmentDescription;
	private String appointmentLocation;
	private String appointmentContact;
	private String appointmentUrl;
	private String appointmentStart;
	private String appointmentEnd;
	private int reminderIncrement;
        private int customerId; 
        
        @FXML
        private TextField txt_AppointmentTitle;
        @FXML
        private TextField txt_AppointmentDescription;
        @FXML
        private TextField txt_AppointmentLocation;
        @FXML
        private TextField txt_AppointmentContact;
        @FXML
        private TextField txt_AppointmentURL;
        
        @FXML
        private ComboBox comBx_StartTime;
        @FXML
        private ComboBox comBx_EndTime;
        
        @FXML
        private Button bt_ScheduleAppointment;
        @FXML
        private Button bt_Cancel;
        
        
        public appointment(String title, String description, String location, String contact,
                String url, String start, String end, int reminderIncrement){
            this.appointmentTitle = title;
            this.appointmentDescription = description;
            this.appointmentLocation = location;
            this.appointmentContact = contact;
            this.appointmentUrl = url;
            this.appointmentStart = start;
            this.appointmentEnd = end;
            this.reminderIncrement = reminderIncrement;
        }
        
        public void createAppointment() throws ClassNotFoundException, SQLException{
            
            appointment newAppt = new appointment(txt_AppointmentTitle.getText(), txt_AppointmentDescription.getText(),
                txt_AppointmentLocation.getText(), txt_AppointmentContact.getText(), txt_AppointmentURL.getText(),
                comBx_StartTime.getSelectionModel().getSelectedItem().toString(), comBx_EndTime.getSelectionModel().getSelectedItem().toString(),
                15);
            
            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                Connection dbConn = DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);
                
                Statement stmt = dbConn.createStatement();
                
                String addScheduleQuery = "Insert into appointment (customerId, title, description, location, contact, url, start, end, createDate"
                        + ", createdBy, lastUpdate, lastUpdateBy, userId) values (" + SchedulesConsult.currentLogIn + " , " ;
            }
            
            
            
            
        }
        
        public int getCustomerId(){
            return this.customerId;
        }
        
        public void setCustomerId(appointment appt, Connection dbConn) throws SQLException, ClassNotFoundException, IOException{
            try{
                
                Scene newCustomer = new Scene(FXMLLoader.load(getClass().getResource("customerRecords.fxml")));
                Stage newCustomerStage = new Stage();
                newCustomerStage.setScene(newCustomer);
                
                Statement stmt = dbConn.createStatement();
                String customerIdQuery = "Select customerId as customerName From customer Where lower(customerName) = '" +
                        appt.getAppointmentContact().toLowerCase() + "'";
                
                ResultSet customerExists = stmt.executeQuery(customerIdQuery);
                
                
                
                if(!customerExists.next()){
                    Alert customerDoesNotExist = new Alert(Alert.AlertType.CONFIRMATION);
                    customerDoesNotExist.setTitle("Customer does not exist in database");
                    customerDoesNotExist.setHeaderText("Customer Not Found");
                    customerDoesNotExist.setContentText("Customer was not found.");
                    Optional<ButtonType> result;
                    
                    customerDoesNotExist.showAndWait()
                            .filter(response -> response == ButtonType.OK)
                            .ifPresent(showCustomerAddStage -> showCustomerAddStage = true);
                    
                }
            }
        }
        
	public String getAppointmentTitle() {
		return this.appointmentTitle;
	}

	public void setAppointmentTitle(String appointmentTitle) {
		this.appointmentTitle = appointmentTitle;
	}

	public String getAppointmentDescription() {
		return this.appointmentDescription;
	}

	public void setAppointmentDescription(String appointmentDescription) {
		this.appointmentDescription = appointmentDescription;
	}

	public String getAppointmentLocation() {
		return this.appointmentLocation;
	}
        
	public void setAppointmentLocation(String appointmentLocation) {
		this.appointmentLocation = appointmentLocation;
	}

	public String getAppointmentContact() {
		return this.appointmentContact;
	}

	public void setAppointmentContact(String appointmentContact) {
		this.appointmentContact = appointmentContact;
	}

	public String getAppointmentUrl() {
		return this.appointmentUrl;
	}

	public void setAppointmentUrl(String appointmentUrl) {
		this.appointmentUrl = appointmentUrl;
	}

	public String getAppointmentStart() {
		return this.appointmentStart;
	}

	public void setAppointmentStart(String appointmentStart) {
		this.appointmentStart = appointmentStart;
	}

	public String getAppointmentEnd() {
		return this.appointmentEnd;
	}

	public void setAppointmentEnd(String appointmentEnd) {
		this.appointmentEnd = appointmentEnd;
	}

	public int getReminderIncrement() {
		return this.reminderIncrement;
	}

	public void setReminderIncrement(int reminderIncrement) {
		this.reminderIncrement = reminderIncrement;
	}
}