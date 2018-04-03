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
import javafx.scene.layout.TextBox;
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
        private TextBox textBox_SundayDate;
        @FXML
        private TextBox textBox_MondayDate;
        @FXML
        private TextBox textBox_TuesdayDate;
        @FXML
        private TextBox textBox_WednesdayDate;
        @FXML
        private TextBox textBox_ThursdayDate;
        @FXML
        private TextBox textBox_FridayDate;
        @FXML
        private TextBox textBox_SaturdayDate;
        
        private int day;
	private int month;
	private int year;
	private int time;
	private int appointments;
        
        private ArrayList<Integer> weeklyDates;
        private Map<Integer,String> dayToHbox = new HashMap<Integer,String>();
        private final ArrayList<TextBox> textBoxList = Arrays.asArrayList(textBox_MondayDate, textBox_TuesdayDate,
                textBox_WednesdayDate, textBox_ThursdayDate, textBox_FridayDate, textBox_SaturdayDate
                ,textBox_SundayDate);
        private final List<Integer> daysOfWeek = Arrays.asList(1,2,3,4,5,6,7);
        
        public void calendarWeeklyDatePopulate(){
            LocalDate selectedDate = datePick_MonthlyDate.getValue();
            DayOfWeek day = selectedDate.getDayOfWeek();
            int dayOfMonth = selectedDate.getDayOfMonth();
            int firstDateOfWeek = dayOfMonth - (day.getValue() - 1);
            weeklyDates.addAll(Arrays.asList(firstDateOfWeek, firstDateOfWeek + 1, 
                    firstDateOfWeek + 2, firstDateOfWeek + 3, firstDateOfWeek + 3,
                    firstDateOfWeek + 4, firstDateOfWeek + 5, firstDateOfWeek + 6));
            
            weeklyDates.stream().forEach(x -> textBoxList);         
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
        
        daysOfWeek.forEach(x->dayToHbox.put(x, textBoxList.get(x - 1)));
    }

    
}