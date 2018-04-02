package schedulesconsult;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class calendar {
    
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
        private HBox hbox_SundayDate;
        @FXML
        private HBox hbox_MondayDate;
        @FXML
        private HBox hbox_TuesdayDate;
        @FXML
        private HBox hbox_WednesdayDate;
        @FXML
        private HBox hbox_ThursdayDate;
        @FXML
        private HBox hbox_FridayDate;
        @FXML
        private HBox hbox_SaturdayDate;
        
        private int day;
	private int month;
	private int year;
	private int time;
	private int appointments;
        
        private ArrayList<Integer> weeklyDates = new ArrayList();
        
        public void calendarWeeklyDatePopulate(){
            LocalDate selectedDate = datePick_MonthlyDate.getValue();
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
}