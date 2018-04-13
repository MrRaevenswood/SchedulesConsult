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
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
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
                
            }else{
                
            } 
        }
        
        public void numOfApptsPerMonth() throws ClassNotFoundException, SQLException{
            String month = JOptionPane.showInputDialog("Please enter the month");
            String year = JOptionPane.showInputDialog("Please enter the year");
            String apptType = JOptionPane.showInputDialog("Please enter the appointment type");
            
            System.out.println(month);
            try{
                Month.valueOf(month.toUpperCase());
            }catch(IllegalArgumentException ex){
                alertPop.accept("Not a valid month","Please enter a valid month");
                return;
            }
            
            try{
                Year.parse(year);
            }catch(DateTimeParseException ex){
                alertPop.accept("Not a valid year", "Please enter a valid year");
            }
            
            try{
                 Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                String numOfApptsPerMonthQuery = "Select monthname(start), count(description) From "
                        + "(Select * From appointment where description = '" + apptType + "' AND monthname(start) = '" + month
                        + "' AND Year(start) = " + year + ") as appts  Group By monthname(start)";
                
                ResultSet rs = stmt.executeQuery(numOfApptsPerMonthQuery);
                ArrayList<String> apptsTypeReport = new ArrayList<>();
                
                txtArea_Results.clear();
                
                if(rs.next()){
                    
                    apptsTypeReport.add(rs.getString(1) + "               " + rs.getString(2));

                    txtArea_Results.setText(apptsTypeReport.get(apptsTypeReport.size() - 1));
                }
                
                rs.close();
                
                
            } catch (SQLException ex) {
                Logger.getLogger(report.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                dbConn.close();
            }
        }

}