package schedulesconsult;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class calendar implements Initializable{
    
        @FXML
        private Button bt_SchAppt;
        @FXML
        private Button bt_Exit;
        @FXML
        private Button bt_AddCustomer;
        @FXML
        private Button bt_LastWeek;
        @FXML
        private Button bt_NextWeek;
        @FXML
        private DatePicker datePick_MonthlyDate;
        
        @FXML
        private TextField textField_SundayDate;
        @FXML
        private TextField textField_MondayDate;
        @FXML
        private TextField textField_TuesdayDate;
        @FXML
        private TextField textField_WednesdayDate;
        @FXML
        private TextField textField_ThursdayDate;
        @FXML
        private TextField textField_FridayDate;
        @FXML
        private TextField textField_SaturdayDate;
        
        @FXML
        private TableView tbl_Appointments;
        @FXML
        private TableColumn col_StartTime;
        @FXML
        private TableColumn col_EndTime;
        @FXML
        private TableColumn col_Monday;
        @FXML
        private TableColumn col_Tuesday;
        @FXML
        private TableColumn col_Wednesday;
        @FXML
        private TableColumn col_Thursday;
        @FXML
        private TableColumn col_Friday;
        @FXML
        private TableColumn col_Saturday;
        @FXML
        private TableColumn col_Sunday;
        
        private int day;
	private int month;
	private int year;
	private int time;
	private int appointments;
        
        private ArrayList<Integer> weeklyDates = new ArrayList<>();
        private final ArrayList<TextField> textFieldList = new ArrayList<>();
        private final List<Integer> daysOfWeek = Arrays.asList(1,2,3,4,5,6,7);
        private LocalDate selectedDate;
        private ObservableList<AppointmentQueryLabels> data = FXCollections.observableArrayList();

        public void calendarWeeklyDatePopulate() throws ClassNotFoundException{
            
            tbl_Appointments.refresh();
            
            selectedDate = datePick_MonthlyDate.getValue();
            DayOfWeek day = selectedDate.getDayOfWeek();
            int dayOfMonth = selectedDate.getDayOfMonth();
            int firstDateOfWeek = dayOfMonth - (day.getValue() - 1);
            int firstDateInRange = 0;
            int selectedDateNum = selectedDate.getDayOfMonth();
            
            Month currentMonth = selectedDate.getMonth();
            Month lastMonth;
            Month nextMonth;
            int lastDayofLastMonth = 0;
            
            System.out.println(firstDateOfWeek);
            
            if(firstDateOfWeek < 1){
                lastMonth = selectedDate.getMonth().minus(1);              
                lastDayofLastMonth = lastMonth.maxLength();
                firstDateInRange = lastDayofLastMonth + firstDateOfWeek;
                
                while(weeklyDates.size() < 7){
                    if(firstDateInRange > lastDayofLastMonth){
                        firstDateInRange = 1;
                        weeklyDates.add(firstDateInRange);
                    }else{
                        weeklyDates.add(firstDateInRange);
                    }
                    
                    firstDateInRange++;
                }
  
            } else if(firstDateOfWeek > currentMonth.maxLength()){
                
                while(weeklyDates.size() < 7){
                    if(selectedDateNum > currentMonth.maxLength()){
                        selectedDateNum = 1;
                        weeklyDates.add(selectedDateNum);
                    }else{
                        weeklyDates.add(selectedDateNum);
                    }
                    
                    selectedDateNum++;
                }
                
            } else{
                
                while(weeklyDates.size() < 7){
                    if(firstDateOfWeek > currentMonth.maxLength()){
                        firstDateOfWeek = 1;
                        weeklyDates.add(firstDateOfWeek);
                    }else{
                        weeklyDates.add(firstDateOfWeek);
                    }
                    
                    firstDateOfWeek++;
                }
            }
            daysOfWeek.stream().forEach(x -> textFieldList.get(x - 1).setText(weeklyDates.get(x - 1).toString())); 
            
            populateAppointmentTable(selectedDate);
            
            weeklyDates.clear();
        }
        
        public void populateAppointmentTable(LocalDate selectedDate) throws ClassNotFoundException{
            int year = selectedDate.getYear();
            Month month = selectedDate.getMonth();
            int currentUserId = 1;
            Connection dbConn = null;
            
            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                //Use a pivot table and DAYNAME(start) in the Select clause to create the table instead of this mess
                String apptThisWeekQuery = "Select Hour(start) as StartTime, Hour(end) as EndTime," +
                    "	CASE" +
                    "		WHEN dayname(start) = 'Monday'" +
                    "        THEN  'X' " +
                    "        ELSE null" +
                    "	END As 'Monday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Tuesday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Tuesday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Wednesday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Wednesday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Thursday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Thursday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Friday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Friday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Saturday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Saturday Appointments'," +
                    "    CASE" +
                    "		WHEN dayname(start) = 'Sunday'" +
                    "        THEN 'X'" +
                    "        ELSE null" +
                    "	END As 'Sunday Appointments'" +
                    " From appointment" +
                    " Where userid = "+ currentUserId +" AND ( Year(start) = " + year + " AND lower(monthname(start)) = '"+ 
                        month.toString().toLowerCase() +"' AND day(start) BETWEEN "+ weeklyDates.get(0) + " AND " 
                        + weeklyDates.get(weeklyDates.size() - 1) + ")";

                ResultSet apptThisWeek = stmt.executeQuery(apptThisWeekQuery);
               
                while(apptThisWeek.next()){

                   
                   AppointmentQueryLabels appts = new AppointmentQueryLabels();
                   appts.startTime.set(apptThisWeek.getString(1));
                   appts.endTime.set(apptThisWeek.getString(2));
                   appts.monday.set(apptThisWeek.getString(3));
                   appts.tuesday.set(apptThisWeek.getString(4));
                   appts.wednesday.set(apptThisWeek.getString(5));
                   appts.thursday.set(apptThisWeek.getString(6));
                   appts.friday.set(apptThisWeek.getString(7));
                   appts.saturday.set(apptThisWeek.getString(8));
                   appts.sunday.set(apptThisWeek.getString(9));
                   
                   data.add(appts);
 
                }
                
                tbl_Appointments.setItems(data);
                              
                
                            
            }catch(SQLException ex){
                Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                try {
                    dbConn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(calendar.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        }
	public void getDay() {
		// TODO - implement calendarMonthApp.getDay
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param day
	 */
	public void setDay(int day) {
		this.day = day;
	}

	public void getMonth() {
		// TODO - implement calendarMonthApp.getMonth
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param month
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	public void getYear() {
		// TODO - implement calendarMonthApp.getYear
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param year
	 */
	public void setYear(int year) {
		this.year = year;
	}

	public void getTime() {
		// TODO - implement calendarMonthApp.getTime
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param time
	 */
	public void setTime(int time) {
		this.time = time;
	}

	public void getAppointments() {
		// TODO - implement calendarMonthApp.getAppointments
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param appointments
	 */
	public void setAppointments(int appointments) {
		this.appointments = appointments;
	}

        public void openCustomerAdd() throws IOException{
            
            Scene newCustomer = new Scene(FXMLLoader.load(getClass().getResource("customerRecords.fxml")));
            Stage newCustomerStage = new Stage();
                        
            newCustomerStage.setScene(newCustomer);
            newCustomerStage.show();  
        
        }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldList.add(textField_MondayDate);
        textFieldList.add(textField_TuesdayDate);
        textFieldList.add(textField_WednesdayDate);
        textFieldList.add(textField_ThursdayDate);
        textFieldList.add(textField_FridayDate);
        textFieldList.add(textField_SaturdayDate);
        textFieldList.add(textField_SundayDate);
        
        
        col_StartTime.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("startTime"));
        col_EndTime.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("endTime"));
        col_Monday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("monday"));
        col_Tuesday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("tuesday"));
        col_Wednesday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("wednesday"));
        col_Thursday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("thursday"));
        col_Friday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("friday"));
        col_Saturday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("saturday"));
        col_Sunday.setCellValueFactory(new PropertyValueFactory<AppointmentQueryLabels,String>("sunday"));
        
    }
    public void openScheduleAppointmentWindow() throws IOException{
        Scene newAppointment = new Scene(FXMLLoader.load(getClass().getResource("appointment.fxml")));
        Stage newAppointmentStage = new Stage();
        
        newAppointmentStage.setScene(newAppointment);
        newAppointmentStage.show();
    }

    public static class AppointmentQueryLabels {
       
        private SimpleStringProperty startTime = new SimpleStringProperty();
        private SimpleStringProperty endTime = new SimpleStringProperty();
        private SimpleStringProperty monday = new SimpleStringProperty();
        private SimpleStringProperty tuesday = new SimpleStringProperty();
        private SimpleStringProperty wednesday = new SimpleStringProperty();
        private SimpleStringProperty thursday = new SimpleStringProperty();
        private SimpleStringProperty friday = new SimpleStringProperty();
        private SimpleStringProperty saturday = new SimpleStringProperty();
        private SimpleStringProperty sunday = new SimpleStringProperty();
        
        public String getStartTime(){
            return startTime.get();
        }
        
        public String getEndTime(){
            return endTime.get();
        }
        
        public String getMonday(){
            return monday.get();
        }
        
        public String getTuesday(){
            return tuesday.get();
        }
        
        public String getWednesday(){
            return wednesday.get();
        }
        
        public String getThursday(){
            return thursday.get();
        }
        
        public String getFriday(){
            return friday.get();
        }
        
        public String getSaturday(){
            return saturday.get();
        }
        
        public String getSunday(){
            return sunday.get();
        }
    }
    
}