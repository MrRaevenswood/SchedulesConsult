package schedulesconsult;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Calendar.MINUTE;
import java.util.Map;
import java.util.ResourceBundle;
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
        
        private final ScheduledExecutorService appointmentReminder = Executors.newScheduledThreadPool(1);
        
        
        BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.showAndWait();
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
        
        public void createAppointment() throws ClassNotFoundException, SQLException, IOException{
            
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            appointment newAppt = new appointment(txt_AppointmentTitle.getText(), txt_AppointmentDescription.getText(),
                txt_AppointmentLocation.getText(), txt_AppointmentContact.getText(), txt_AppointmentURL.getText(),
                LocalDateTime.parse(datePick_AppointmentDate.getValue() + " " + comBx_StartTime.getSelectionModel().getSelectedItem().toString(), dateFormat), 
                LocalDateTime.parse(datePick_AppointmentDate.getValue() + " " + comBx_endTime.getSelectionModel().getSelectedItem().toString(), dateFormat),
                15);
            
            LocalDateTime currentTime = LocalDateTime.now();
            LocalTime businessStart = LocalTime.of(8, 0);
            LocalTime businessEnd = LocalTime.of(17 , 0);

            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                Connection dbConn = DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);
                
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
                        + newAppt.appointmentStart + "','" + newAppt.appointmentEnd + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "','" 
                        + currentTime + "','" + SchedulesConsult.currentLogIn + "'," + newUser.getUserIdByName(SchedulesConsult.currentLogIn) + ")" ;
               
                if(newAppt.isAppointmentOverlapping(dbConn, newAppt.appointmentStart, newAppt.appointmentEnd)){
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
                
            }catch(SQLException ex){ 
                
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
                return;
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
        
        public boolean isAppointmentOverlapping(Connection dbConn, LocalDateTime start, LocalDateTime end) throws ClassNotFoundException{
            int startTime = start.getHour();
            int endTime = end.getHour();
            
            
            addNewUser currentUser = new addNewUser();
            try{
                Statement stmt = dbConn.createStatement();
                String allApptsQuery = "Select appointmentId, Hour(start), Hour(end) From appointment where userId = " + currentUser.getUserIdByName(SchedulesConsult.currentLogIn)
                        + " AND (Year(start) = " + start.getYear() + " AND Month(start) = " + start.getMonthValue() + " AND Day(start) = " + start.getDayOfMonth() + " )";
                
                ResultSet allAppts = stmt.executeQuery(allApptsQuery);
                
                while(allAppts.next()){
                   if (allAppts.getInt(2) <= startTime && allAppts.getInt(3) >= startTime){
                       if(SchedulesConsult.isEnglish == true){
                           alertPop.accept("Invalid StartTime Choosen", "Please try a start time that is not within range of a current appointment");
                       }else if(SchedulesConsult.isSpanish == true){
                           alertPop.accept("Hora de inicio no válida seleccionada","Intente una hora de inicio que no esté dentro del alcance de una cita actual");
                       }
                       
                       return true;
                   }
                   
                   if(allAppts.getInt(2) <= endTime && allAppts.getInt(3) >= endTime){
                       
                       if(SchedulesConsult.isEnglish == true){
                           alertPop.accept("Invalid EndTime Choosen", "Please try an end time that is not within range of a current appointment");
                       }else if(SchedulesConsult.isSpanish == true){
                           alertPop.accept("Hora de finalización no válida elegida","Intente una hora de finalización que no esté dentro del alcance de una cita actual");
                       }
                       
                       return true;
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

        public void appointmentReminder(LocalDateTime dateOfAppointment){
            Runnable fifteenMinuteReminder = new Runnable(){
                @Override
                public void run() {
                    alertPop.accept("Appointment in 15 mins", "Please be ready for your appointment in 15 mins");
                }
            };
            
            
            
            appointmentReminder.schedule(fifteenMinuteReminder, dateOfAppointment , TimeUnit.DAYS)
        }
        
        public int computeDelay(LocalDateTime dateOfAppointment){
            dateOfAppointment.until(LocalDateTime.now(),ChronoUnit.MINUTES);
        }

        public class AppointmentsThisWeek{

        }    

        @Override
        public void initialize(URL location, ResourceBundle resources) {
            ObservableList<String> time = FXCollections.observableArrayList();;
            time.addAll(Arrays.asList("01:00","02:00","03:00","04:00","05:00","06:00",
                    "07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00",
                    "16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00"));

            comBx_StartTime.setItems(time);
            comBx_endTime.setItems(time);
        }

        public void close(){
            Stage stage = (Stage) bt_Cancel.getScene().getWindow();
            stage.close();
        }    
}