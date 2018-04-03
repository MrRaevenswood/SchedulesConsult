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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
        private GridPane gr_Week;
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
        
        private int day;
	private int month;
	private int year;
	private int time;
	private int appointments;
        
        private ArrayList<Integer> weeklyDates = new ArrayList<>();
        private final ArrayList<TextField> textFieldList = new ArrayList<>();
        private final List<Integer> daysOfWeek = Arrays.asList(1,2,3,4,5,6,7);
        
        public void calendarWeeklyDatePopulate(){
            LocalDate selectedDate = datePick_MonthlyDate.getValue();
            DayOfWeek day = selectedDate.getDayOfWeek();
            int dayOfMonth = selectedDate.getDayOfMonth();
            int firstDateOfWeek = dayOfMonth - (day.getValue() - 1);
            //Add Predicate Stream to fix this issue with end or beginning of months. 
            weeklyDates.addAll(Arrays.asList(firstDateOfWeek, firstDateOfWeek + 1, 
                    firstDateOfWeek + 2, firstDateOfWeek + 3, firstDateOfWeek + 3,
                    firstDateOfWeek + 4, firstDateOfWeek + 5, firstDateOfWeek + 6));
            
            daysOfWeek.stream().forEach(x -> textFieldList.get(x - 1).setText(weeklyDates.get(x - 1).toString())); 
            
            weeklyDates.clear();
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
    }

    
}