package schedulesconsult;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javax.swing.JOptionPane;

public class report{

	@FXML
        private Button bt_RunReport;
        @FXML
        private Button bt_Cancel;
        @FXML
        private RadioButton rb_ApptsPerMonth;
        @FXML
        private RadioButton rb_ApptsByConsultant;
        @FXML
        private RadioButton rb_ApptsByCustomer;
        @FXML
        private TextArea txtArea_Results;
        @FXML
        private ToggleGroup reports;
                
        BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.showAndWait();
        };
        
        BiFunction<String,String,String> inputDiagPop = (t,c)-> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle(t);
            dialog.setContentText(c);
            
            Optional<String> result = dialog.showAndWait();
            return result.get();
        };
        
        Connection dbConn = null;
        
        public void runReports(){
            
            if(rb_ApptsPerMonth.isSelected()){
                try {
                    numOfApptsPerMonth();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else if(rb_ApptsByConsultant.isSelected()){
                try {
                    appointmentsByConsultant();
                } catch (SQLException ex) {
                    Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                appointmentByCustomer();
            } 
        }
        
        public void numOfApptsPerMonth() throws ClassNotFoundException, SQLException{
            
            txtArea_Results.clear();
            
            String month = inputDiagPop.apply("Month Required", "Please enter the month:");
            try{
                Month.valueOf(month.toUpperCase());
            }catch(IllegalArgumentException ex){
                alertPop.accept("Not a valid month","Please enter a valid month");
                return;
            }
            
            String year = inputDiagPop.apply("Year Required","Please enter the year");
             try{
                Year.parse(year);
            }catch(DateTimeParseException ex){
                alertPop.accept("Not a valid year", "Please enter a valid year");
            }
            
            String apptType = inputDiagPop.apply("Appointment Type Required","Please enter the appointment type");
            
           
            try{
                 Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                String numOfApptsPerMonthQuery = "Select monthname(start), count(description) From "
                        + "(Select * From appointment where description = '" + apptType + "' AND monthname(start) = '" + month
                        + "' AND Year(start) = " + year + ") as appts  Group By monthname(start)";
                
                ResultSet rs = stmt.executeQuery(numOfApptsPerMonthQuery);
                
                
                txtArea_Results.setText("Month          Number of Appointments");
                txtArea_Results.appendText(System.getProperty("line.separator"));
                txtArea_Results.appendText("-----------------------------------------------");
                txtArea_Results.appendText(System.getProperty("line.separator"));
                
                while(rs.next()){
                    txtArea_Results.appendText(rs.getString(1) + "               " + rs.getString(2));
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                }
           
                rs.close();
                
                
            } catch (SQLException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                dbConn.close();
            }
        }

        public void appointmentsByConsultant() throws SQLException{
            String consultant = inputDiagPop.apply("Consultant Name Required", "Please enter the name of the Consultant");
            int userId = 0;
            
            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                String consultantSearchQuery = "Select userId from user Where lower(userName) = '" + consultant + "'";
                ResultSet rs = stmt.executeQuery(consultantSearchQuery);
                
                if(!rs.next()){
                    alertPop.accept("Consultant Not Found", "Consultant Not Found");
                }else{
                    userId = rs.getInt(1);
                }
                rs.close();
                
                String consultantScheduleQuery = "Select title, location, contact, start, end"
                        + " From appointment Where userId = " + userId;
                
                rs = stmt.executeQuery(consultantScheduleQuery);
                
                if(!rs.next()){
                    alertPop.accept("No Appointments Found", "Consultant Does Not Have Appointments");
                    return;
                }else{
                    txtArea_Results.setText("Title                Location        Contact          Start                                   End");
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    txtArea_Results.appendText("--------------------------------------------------------------------------------------------------------------------");
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    txtArea_Results.appendText(rs.getString(1) + "                 " + rs.getString(2) + "                " + rs.getString(3)
                            + "                " + rs.getString(4) + "       " + rs.getString(5));
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    
                    while(rs.next()){
                        txtArea_Results.appendText(rs.getString(1) + "                 " + rs.getString(2) + "                " + rs.getString(3)
                            + "                " + rs.getString(4) + "       " + rs.getString(5));
                        txtArea_Results.appendText(System.getProperty("line.separator"));
                    }
                }
                
                rs.close();
                
                
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            }finally{
                dbConn.close();
            }
        }
        public void appointmentByCustomer(){
            String customer = inputDiagPop.apply("Customer Name Required", "Please Enter A Customer Name: ");
            int custId = 0;
            
            try{
                Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                String customerIdQuery = "Select customerId From customer Where customerName = '" + customer + "'";
                
                ResultSet rs = stmt.executeQuery(customerIdQuery);
                
                if(!rs.next()){
                    alertPop.accept("Customer Not Found", "Please enter a valid customer: ");
                    return;
                }else{
                    custId = rs.getInt(1);
                }
                
                rs.close();
                
                String customerScheduleQuery = "Select title, location, contact, start, end"
                        + " From appointment Where customerId = " + custId;
                
                rs = stmt.executeQuery(customerScheduleQuery);
                
                if(!rs.next()){
                    alertPop.accept("No Appointments Found", "Customer Does Not Have Appointments");
                    return;
                }else{
                    txtArea_Results.setText("Title                Location        Contact          Start                                   End");
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    txtArea_Results.appendText("--------------------------------------------------------------------------------------------------------------------");
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    txtArea_Results.appendText(rs.getString(1) + "                 " + rs.getString(2) + "                " + rs.getString(3)
                            + "                " + rs.getString(4) + "       " + rs.getString(5));
                    txtArea_Results.appendText(System.getProperty("line.separator"));
                    
                    while(rs.next()){
                        txtArea_Results.appendText(rs.getString(1) + "                 " + rs.getString(2) + "                " + rs.getString(3)
                            + "                " + rs.getString(4) + "       " + rs.getString(5));
                        txtArea_Results.appendText(System.getProperty("line.separator"));
                    }
                }
                
                rs.close();
                
                
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}