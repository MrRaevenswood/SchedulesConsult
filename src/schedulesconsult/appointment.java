package schedulesconsult;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.TemporalQueries.zone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import static java.util.Calendar.MINUTE;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

public class appointment implements Initializable {

	private String appointmentTitle;
	private String appointmentDescription;
	private String appointmentLocation;
	private String appointmentContact;
	private String appointmentUrl;
	private LocalDateTime appointmentStart;
	private LocalDateTime appointmentEnd;
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
        
        private Calendar currentTimeZone;
        
        BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.show();
               };
        
        public appointment(){}
        
        public appointment(String title, String description, String location, String contact,
                String url, LocalDateTime start, LocalDateTime end, int reminderIncrement){
            this.appointmentTitle = title;
            this.appointmentDescription = description;
            this.appointmentLocation = location;
            this.appointmentContact = contact;
            this.appointmentUrl = url;
            this.appointmentStart = start;
            this.appointmentEnd = end;
            this.reminderIncrement = reminderIncrement;
        }
        
        public void createAppointment() throws ClassNotFoundException, SQLException, IOException, ParseException{
            
            appointment newAppt = null;
            
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            try{
                newAppt = new appointment(txt_AppointmentTitle.getText(), txt_AppointmentDescription.getText(),
                txt_AppointmentLocation.getText(), txt_AppointmentContact.getText(), txt_AppointmentURL.getText(),
                LocalDateTime.parse(datePick_AppointmentDate.getValue() + " " + comBx_StartTime.getSelectionModel().getSelectedItem().toString(), dateFormat), 
                LocalDateTime.parse(datePick_AppointmentDate.getValue() + " " + comBx_endTime.getSelectionModel().getSelectedItem().toString(), dateFormat),
                15);
            }catch (NullPointerException ex){
                        alertPop.accept("All fields must be filled", "There is an empty field in the form. Please fill it out"
                                + " as all fields are necessary");
                    }
           
            
            LocalDateTime currentTime = LocalDateTime.now();
            LocalTime businessStart = LocalTime.of(8, 0);
            LocalTime businessEnd = LocalTime.of(17 , 0);

            Class.forName("com.mysql.jdbc.Driver");
            
            try(Connection dbConn = DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);){

               newAppt.setCustomerId(newAppt, dbConn);
                
               Statement stmt = dbConn.createStatement();
                
               addNewUser newUser = new addNewUser();
               
               BiPredicate<LocalDateTime,LocalDateTime> checkApptStartEnd = (s,e) -> s.isAfter(e);
               BiPredicate<LocalDateTime,LocalDateTime> checkApptAfterHours = (s,e) -> s.toLocalTime().isBefore(businessStart) ||
                       e.toLocalTime().isAfter(businessEnd);
               
               
               
               
               boolean isStartAfterEnd = checkApptStartEnd.test(newAppt.appointmentStart, newAppt.appointmentEnd);
               boolean isAfterHours = checkApptAfterHours.test(newAppt.appointmentStart, newAppt.appointmentEnd);
               
               if(isStartAfterEnd == true) {
                   if(SchedulesConsult.isEnglish == true){
                       alertPop.accept("Start Time Is After End Time", "Please select a time that is before the end time");
                   }else if(SchedulesConsult.isSpanish == false){
                       alertPop.accept("Hora de inicio es después del tiempo de finalización", "Por favor, seleccione una hora antes de la hora de finalización");
                   }
                  
                  return;
               }else if (isAfterHours == true) {
                   if(SchedulesConsult.isEnglish == true){
                       alertPop.accept("After Hours Selected", "Either your start, end, or both times are after or normal business hours of 8 am - 5 pm");
                   }else if(SchedulesConsult.isSpanish == true){
                       alertPop.accept("Fuera de horas", "O bien el inicio, el final o ambas veces son posteriores o el horario comercial normal es de 8 a. M. A 5 p. M.");
                   }
                  
                  return;
               }
               
               int customerId = 0;
               String customerIdQuery = "Select customerId From customer Where customerName = '" + newAppt.getAppointmentContact() + "'";
               ResultSet rs = stmt.executeQuery(customerIdQuery);
                
                if(!rs.next()){
                    if(SchedulesConsult.isEnglish == true){
                        alertPop.accept("Customer Not Found", "Please enter a valid customer: ");
                    }else if(SchedulesConsult.isSpanish == true){
                        alertPop.accept("Cliente no encontrado","Por favor ingrese un cliente válido:");
                    }
                    
                    return;
                }else{
                    customerId = rs.getInt(1);
                }
                
                rs.close();
               
                String addScheduleQuery = "Insert into appointment (customerId, title, description, location, contact, url, start, end, createDate"
                        + ", createdBy, lastUpdate, lastUpdatedBy, userId) values (" + customerId + " , " + "'" + newAppt.getAppointmentTitle()
                        + "','" + newAppt.getAppointmentDescription() + "','" + newAppt.getAppointmentLocation() + "','" + newAppt.getAppointmentContact() 
                        + "','" + newAppt.getAppointmentUrl() + "','" 
                        + newAppt.appointmentStart.atZone(ZoneId.systemDefault()).toLocalDateTime() + "','" + newAppt.appointmentEnd.atZone(ZoneId.systemDefault()).toLocalDateTime() + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "','" 
                        + currentTime + "','" + SchedulesConsult.currentLogIn + "'," + newUser.getUserIdByName(SchedulesConsult.currentLogIn) + ")" ;
               
                if(newAppt.isAppointmentOverlapping(dbConn, newAppt.appointmentStart.atZone(ZoneId.systemDefault()), newAppt.appointmentEnd.atZone(ZoneId.systemDefault()))){
                    if(SchedulesConsult.isEnglish == true){
                        alertPop.accept("Appointment Overlap Detected", "Please Select A New Start/End Time for your request.");
                    }else if(SchedulesConsult.isSpanish == true){
                        alertPop.accept("Superposición de citas detectada","Seleccione una nueva hora de inicio / finalización para su solicitud.");
                    }
                    
                    return;
                }
                
                Consumer<String> insertAppointment = s -> {
                    try {
                        stmt.executeUpdate(s);
                    } catch (SQLException ex) {
                        Logger.getLogger(appointment.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                };

                insertAppointment.accept(addScheduleQuery);
                
                LogIn currentLogIn = new LogIn();
                
                currentLogIn.startApptReminder(SchedulesConsult.currentUserId, dbConn);
                
            }
            
            close();
        }
        
        public int getCustomerId(){
            return this.customerId;
        }
        
        public void setCustomerId(appointment appt, Connection dbConn) throws SQLException, ClassNotFoundException, IOException{
            try{
                
                Statement stmt = dbConn.createStatement();
                String customerIdQuery = "Select customerId as customerName From customer Where lower(customerName) = '" +
                        appt.getAppointmentContact().toLowerCase() + "'";
                
                ResultSet customerExists = stmt.executeQuery(customerIdQuery);
                
                if(!customerExists.next()){
                    if(SchedulesConsult.isEnglish == true){
                        alertPop.accept("Customer does not exist in database","Customer was not found.");
                    }else if(SchedulesConsult.isSpanish == true){
                        alertPop.accept("El cliente no existe en la base de datos", "Cliente no fue encontrado.");
                    }
                    
                    
                }else{
                   String maxCustomerIDQuery = "Select customerId From customer";
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
        
        public boolean isAppointmentOverlapping(Connection dbConn, ZonedDateTime start, ZonedDateTime end) throws ClassNotFoundException{
            int startTime = start.getHour();
            int endTime = end.getHour();
            
            
            addNewUser currentUser = new addNewUser();
            try{
                Statement stmt = dbConn.createStatement();
                String allApptsQuery = "Select appointmentId, Hour(start), Hour(end) From appointment where userId = " + currentUser.getUserIdByName(SchedulesConsult.currentLogIn)
                        + " AND (Year(start) = " + start.getYear() + " AND Month(start) = " + start.getMonthValue() + " AND Day(start) = " + start.getDayOfMonth() + " )";
                
                ResultSet allAppts = stmt.executeQuery(allApptsQuery);
                
                while(allAppts.next()){
                   if ((allAppts.getInt(2) <= startTime && allAppts.getInt(3) >= endTime)
                           || (allAppts.getInt(2) < startTime && allAppts.getInt(3) > startTime)
                           || (allAppts.getInt(2) < startTime && allAppts.getInt(3) > endTime)
                           || (allAppts.getInt(2) > startTime && allAppts.getInt(3) <= endTime)){
                       if(SchedulesConsult.isEnglish == true){
                           alertPop.accept("Invalid Time Choosen", "Please try a time that is not within range of a current appointment");
                       }else if(SchedulesConsult.isSpanish == true){
                           alertPop.accept("Hora no válida seleccionada","Intente una hora que no esté dentro del alcance de una cita actual");
                       }
                       
                       return true;
                   } else {
                       return false;
                   }
                }
                
            }catch(SQLException ex){
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return false;
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

	public LocalDateTime getAppointmentStart() {
		return this.appointmentStart;
	}

	public void setAppointmentStart(LocalDateTime appointmentStart) {
		this.appointmentStart = appointmentStart;
	}

	public LocalDateTime getAppointmentEnd() {
		return this.appointmentEnd;
	}

	public void setAppointmentEnd(LocalDateTime appointmentEnd) {
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
            time.addAll(Arrays.asList("07:30","07:45","08:00","08:15","08:30","08:45","09:00","09:15"
                    ,"09:30","09:45","10:00","10:15","10:30","10:45","11:00","11:15","11:30","11:45","12:00"
                    ,"12:15","12:30","12:45","13:00","13:15","13:30","13:45","14:00","14:15","14:30","14:45"
                    ,"15:00","15:15","15:30","15:45","16:00","16:15","16:30","16:45","17:00","17:15","17:30"));

            comBx_StartTime.setItems(time);
            comBx_endTime.setItems(time);
        }

        public void close(){
            Stage stage = (Stage) bt_Cancel.getScene().getWindow();
            stage.close();
        }    
}