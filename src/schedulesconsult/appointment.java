package schedulesconsult;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class appointment {

	private String appointmentTitle;
	private String appointmentDescription;
	private String appointmentLocation;
	private String appointmentContact;
	private String appointmentUrl;
	private String appointmentStart;
	private String appointmentEnd;
	private int reminderIncrement;
        
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
        
        public void createAppointment(){
            appointment newAppt = new appointment(txt_AppointmentTitle.getText(), txt_AppointmentDescription.getText(),
                txt_AppointmentLocation.getText(), txt_AppointmentContact.getText(), txt_AppointmentURL.getText(),
                comBx_StartTime.getSelectionModel().getSelectedItem().toString(), comBx_EndTime.getSelectionModel().getSelectedItem().toString(),
                15);
            
            
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