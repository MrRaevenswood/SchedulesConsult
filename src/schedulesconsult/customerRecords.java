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
    
    private String custAddress = "";
    private String custCountry = "";
    private String custCity = "";
    
    
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
    
    public String getcustomerAddress(){
        return this.custAddress;
    }
    
    public String getcustomerCountry(){
        return this.custCountry;
    }
    
    public String getcustomerCity(){
        return this.custCity;
    }
    
    public void setCustomerAddress(String Address){
        this.custAddress = Address;
    }
    
    public void setCustomerCountry(String Country){
        this.custCountry = Country;
    }
    
    public void setCustomerCity(String City){
        this.custCity = City;
    }
    
    public Boolean isAddressUnique(Connection dbConn, String Address) throws SQLException{
     
        Statement checkAddress = dbConn.createStatement();
        String checkAddressQuery = "Select * From Address Where Address = '" + Address + "'";
        ResultSet dupAddress = checkAddress.executeQuery(checkAddressQuery);
        
        if(!dupAddress.next()){
            return true;
        }else{
            return false;
        }
    }
    
    public Boolean isCountryUnique(Connection dbConn, String Country) throws SQLException{
        Statement checkCountry = dbConn.createStatement();
        String checkCountryQuery = "Select * From Country Where Country = '" +
               Country + "'";
        ResultSet dupCountry = checkCountry.executeQuery(checkCountryQuery);
        
        if(!dupCountry.next()){
            return true;
        }else {
            return false;
        }
    }
    
    public Boolean isCityUnique(Connection dbConn, String City) throws SQLException{
        Statement checkCity = dbConn.createStatement();
        String checkCityQuery = "Select * From City Where City = '" +
                City + "'";
        ResultSet dupCity = checkCity.executeQuery(checkCityQuery);
        
        if
                ()
    }

}