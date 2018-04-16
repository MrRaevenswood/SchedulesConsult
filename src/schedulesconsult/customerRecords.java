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
import java.util.function.BiConsumer;
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
    private TextArea txt_Address;
    @FXML
    private TextField txt_PhoneNumber;
    @FXML
    private Button bt_AddCustomer;
    @FXML
    private Button bt_CancelCustomerAdd;
    
    private String custAddress = "";
    private String custCountry = "";
    private String custCity = "";
    
    BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.showAndWait();
               };
    
    
    public void addCustomerRecord() throws ClassNotFoundException, SQLException{
        
        customerRecords newCustomer = new customerRecords();
        newCustomer.setCustomerAddress(txt_Address.getText());
        newCustomer.setCustomerCity(txt_City.getText());
        newCustomer.setCustomerCountry(txt_Country.getText());
        
        boolean isAddressUnique, isCityUnique,isCountryUnique;      
        int addressId, cityId, countryId;
        
        String countryIdQuery = "Select countryId From country Where country = '" + newCustomer.getcustomerCountry() + "'";
        String cityIdQuery = "Select cityId From city Where city = '" + newCustomer.getcustomerCity() + "'";
        String addressIdQuery = "Select addressId From address Where address = '" + newCustomer.getcustomerAddress() + "'";
         
        LocalDateTime currentTime = LocalDateTime.now();
        Connection dbConn = null;
        try{
            
            Class.forName("com.mysql.jdbc.Driver");
            dbConn =  DriverManager.getConnection(
            SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);
            Statement stmt = dbConn.createStatement();
            
            isAddressUnique = isAddressUnique(dbConn,newCustomer.getcustomerAddress());
            isCityUnique = isCityUnique(dbConn,newCustomer.getcustomerCity());
            isCountryUnique = isCountryUnique(dbConn,newCustomer.getcustomerCountry());
            
            if(isCountryUnique == true){
                String countryInsertQuery = "Insert into country (country, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "Values ('" + newCustomer.getcustomerCountry() + "','" + currentTime + "','" + SchedulesConsult.currentLogIn 
                        + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "')";
                
                stmt.executeUpdate(countryInsertQuery);
                
                
                ResultSet countryIdResult = stmt.executeQuery(countryIdQuery);
                countryIdResult.first();
                countryId = countryIdResult.getInt(1);
            }else{
               ResultSet countryIdResult = stmt.executeQuery(countryIdQuery);
               countryIdResult.first();
               countryId = countryIdResult.getInt(1);
            }
            
            if(isCityUnique == true){
                String cityInsertQuery = "Insert into city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "Values ('" + newCustomer.getcustomerCity() + "','" + countryId + "','" + currentTime + "','"
                        + SchedulesConsult.currentLogIn + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "')";
                stmt.executeUpdate(cityInsertQuery);
                
                ResultSet cityIdResult = stmt.executeQuery(cityIdQuery);
                cityIdResult.first();
                cityId = cityIdResult.getInt(1);
            }else{
                ResultSet cityIdResult = stmt.executeQuery(cityIdQuery);
                cityIdResult.first();
                cityId = cityIdResult.getInt(1);
            }
            
            if(isAddressUnique == true){
                String addressInsertQuery = "Insert into address (address, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)"
                        + "Values ('" + newCustomer.getcustomerAddress() + "','" + cityId + "','" + txt_PostalCode.getText() + "','" +
                        txt_PhoneNumber.getText() + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "','" + currentTime + "','" +
                        SchedulesConsult.currentLogIn + "')";
                stmt.executeUpdate(addressInsertQuery);
                
                ResultSet addressIdResult = stmt.executeQuery(addressIdQuery);
                addressIdResult.first();
                addressId = addressIdResult.getInt(1);
            }else{
                ResultSet addressIdResult = stmt.executeQuery(addressIdQuery);
                addressIdResult.first();
                addressId = addressIdResult.getInt(1);
            }                                                                                                          
            
            String customerInsertQuery = "Insert into customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "Values ('" + txt_FullName.getText() + "'," + addressId + ",true," + "'" + currentTime + "','" + SchedulesConsult.currentLogIn
                    + "','" + currentTime + "','" + SchedulesConsult.currentLogIn + "')";
            stmt.executeUpdate(customerInsertQuery);
            dbConn.close();
        }catch(SQLException ex){
            
            if(SchedulesConsult.isEnglish == true){
                alertPop.accept("Error Connecting to Database", "There was an error connecting to the database that is likely due to the network.");
            }else if(SchedulesConsult.isSpanish == true){
                alertPop.accept("Error al conectarse a la base de datos", "Hubo un error al conectarse a la base de datos que probablemente se deba a la red.");
            }

            
            Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }finally{
            dbConn.close();
        }
        
        close();
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
        String checkAddressQuery = "Select * From address Where Address = '" + Address + "'";
        ResultSet dupAddress = checkAddress.executeQuery(checkAddressQuery);
        
        if(!dupAddress.next()){
            return true;
        }else{
            return false;
        }
    }
    
    public Boolean isCountryUnique(Connection dbConn, String Country) throws SQLException{
        Statement checkCountry = dbConn.createStatement();
        String checkCountryQuery = "Select * From country Where Country = '" +
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
        String checkCityQuery = "Select * From city Where City = '" +
                City + "'";
        ResultSet dupCity = checkCity.executeQuery(checkCityQuery);
        
        if(!dupCity.next()){
            return true;
        }else {
            return false;
        }
    }
    
    public void close(){
       Stage stage = (Stage) bt_AddCustomer.getScene().getWindow();
       stage.close();
    }
}