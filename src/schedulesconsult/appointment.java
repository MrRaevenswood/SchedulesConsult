package schedulesconsult;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class appointment implements Initializable {

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
        private ComboBox comBx_endTime;
        
        @FXML
        private DatePicker datePick_AppointmentDate;
        
        @FXML
        private Button bt_ScheduleAppointment;
        @FXML
        private Button bt_Cancel;
        
        public appointment(){}
        
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
        
        public void createAppointment() throws ClassNotFoundException, SQLException, IOException{
            
            appointment newAppt = new appointment(txt_AppointmentTitle.getText(), txt_AppointmentDescription.getText(),
                txt_AppointmentLocation.getText(), txt_AppointmentContact.getText(), txt_AppointmentURL.getText(),
                datePick_AppointmentDate.getValue() + " " + comBx_StartTime.getSelectionModel().getSelectedItem().toString(), 
                datePick_AppointmentDate.getValue() + " " + comBx_endTime.getSelectionModel().getSelectedItem().toString(),
                15);
            
            LocalDateTime currentTime = LocalDateTime.now();

            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                Connection dbConn = DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);
                
                newAppt.setCustomerId(newAppt, dbConn);
                
                Statement stmt = dbConn.createStatement();
                
                addNewUser newUser = new addNewUser();
                
                System.out.println(newAppt.customerId);
                
                String addScheduleQuery = "Insert into appointment (customerId, title, description, location, contact, url, start, end, createDate"
                        + ", createdBy, lastUpdate, lastUpdateBy, userId) values (" + 1 + " , " + "'" + newAppt.getAppointmentTitle()
                        + "','" + newAppt.getAppointmentDescription() + "','" + newAppt.getAppointmentLocation() + "','" + newAppt.getAppointmentContact() 
                        + "','" + newAppt.getAppointmentUrl() + "','" 
                        + newAppt.appointmentStart + "','" + newAppt.appointmentEnd + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "','" 
                        + currentTime + "','" + SchedulesConsult.currentLogIn + "'," + newUser.getUserIdByName(SchedulesConsult.currentLogIn) + ")" ;
                
                stmt.executeUpdate(addScheduleQuery);
                
            }catch(SQLException ex){ 
                
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                return;
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
                    
                    customerDoesNotExist.showAndWait()
                            .filter(response -> response == ButtonType.OK)
                            .ifPresent(showCustomerAddStage -> newCustomerStage.showAndWait());
                    
                }else{
                   String maxCustomerIDQuery = "Select Max(customerId) From customer";
                   ResultSet maxCustomerId = stmt.executeQuery(maxCustomerIDQuery);
                   if(maxCustomerId.next()){
                       appt.customerId = maxCustomerId.getInt(1);
                   }
                }
            }catch(SQLException ex){
                
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                return;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> time = FXCollections.observableArrayList();;
        time.addAll(Arrays.asList("1:00:00","2:00:00","3:00:00","4:00:00","5:00:00","6:00:00",
                "7:00:00","8:00:00","9:00:00","10:00:00","11:00:00","12:00:00","13:00:00","14:00:00","15:00:00",
                "16:00:00","17:00:00","18:00:00","19:00:00","20:00:00","21:00:00","22:00:00","23:00:00"));
       
        comBx_StartTime.setItems(time);
        comBx_endTime.setItems(time);
    }
    
    public void close(){
        Stage stage = (Stage) bt_Cancel.getScene().getWindow();
        stage.close();
    }

}