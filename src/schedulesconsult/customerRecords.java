package schedulesconsult;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class customerRecords {
    
    @FXML
    private TextField txt_FullName;
    @FXML
    private TextField txt_City;
    @FXML
    private TextField txt_PostalCode;
    @FXML
    private TextField txt_Country;
    @FXML
    private TextField txt_Address;
    @FXML
    private TextField txt_PhoneNumber;
    @FXML
    private Button bt_AddCustomer;
    @FXML
    private Button bt_CancelCustomerAdd;
    
    public void addCustomerRecord() throws ClassNotFoundException{
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
                
                Connection dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
            String customerInsertQuery = "Insert into "
            
        }catch(SQLException ex){
            
        }
    }
	

}