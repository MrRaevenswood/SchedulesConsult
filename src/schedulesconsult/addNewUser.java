/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulesconsult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class addNewUser {
    @FXML
    private TextField txt_NewUserName;
    @FXML
    private PasswordField pass_Password;
    @FXML
    private Button bt_AddNewUser;
    @FXML
    private Button bt_CancelAddNewUser;
    
    private String userName = "";
    private String password = "";
    private Connection dbConnection;
    
     BiConsumer<String,String> alertPop = (t,c) -> {
                   Alert newAlert = new Alert(Alert.AlertType.ERROR);
                   newAlert.setTitle(t);
                   newAlert.setContentText(c);
                   newAlert.showAndWait();
        };

    public Connection getDbConnection(){
        return this.dbConnection;
    }
    
    public void setDbConnection(Connection dbConn){
        this.dbConnection = dbConn;
    }
    
    public String getUserName(){
        return this.userName;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public void setUserName(String userName){
        this.userName = userName;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void createNewUser() throws ClassNotFoundException{
        addNewUser newUser = new addNewUser();
        newUser.setUserName(txt_NewUserName.getText());
        newUser.setPassword(pass_Password.getText());
        
        LocalDateTime currentTime = LocalDateTime.now();
        Connection dbConn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver");
                
                dbConn =  DriverManager.getConnection(
                    SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);

                Statement stmt = dbConn.createStatement();
                
                String dbUserQuery = "Select userName, password From user Where userName = '" +  
                        newUser.userName + "'";
                ResultSet userExists = stmt.executeQuery(dbUserQuery);
                
                if(!userExists.next()){
                    String createUserQuery = "Insert into user (userName, password, active, createBy, createDate, lastUpdate, lastUpdatedBy) values ('"+ 
                        newUser.getUserName() + "','" + newUser.getPassword() + "',True" + ",'"+ SchedulesConsult.databaseUser + "','" + currentTime + 
                        "','" + currentTime + "','" + SchedulesConsult.databaseUser + "')";
                    stmt.executeUpdate(createUserQuery);
                }else{
                    
                    if(SchedulesConsult.isEnglish == true){
                        alertPop.accept("User Already Exists", "User Name is already in the system. Please try another user name.");
                    }
                    
                    if(SchedulesConsult.isSpanish == true){
                        alertPop.accept("El usuario ya existe", "El nombre de usuario ya está en el sistema. Por favor intente con otro nombre de usuario.");
                    }
                 
                    dbConn.close();
                    return;
                }
        }catch(SQLException ex){
            
            if(SchedulesConsult.isEnglish == true){
                alertPop.accept("Error Connecting to Database", "There was an error connecting to the database that is likely due to the network.");
            }
 
            if(SchedulesConsult.isSpanish == true){
                alertPop.accept("Error al conectarse a la base de datos","Hubo un error al conectarse a la base de datos que probablemente se deba a la red.");
            }
            
            Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }finally{
            try {
                dbConn.close();
            } catch (SQLException ex) {
                Logger.getLogger(addNewUser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        close();
    }
    
    public int getUserIdByName (String name) throws ClassNotFoundException, SQLException{
        Connection dbConn = null;
        
        try{
            Class.forName("com.mysql.jdbc.Driver");
            
            dbConn = DriverManager.getConnection(
                SchedulesConsult.databaseConnectionString, SchedulesConsult.databaseUser, SchedulesConsult.databasePassword);
            
            Statement stmt = dbConn.createStatement();
            String userMatchQuery = "Select userId from user where lower(userName) = '" + name.toLowerCase() + "'";
            
            ResultSet userMatched = stmt.executeQuery(userMatchQuery);
            
            if(userMatched.next()){
                return userMatched.getInt(1);
            }
            
        }catch(SQLException ex){
            Logger.getLogger(LogIn.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            dbConn.close();
        }
        return 0;
    }
    
    public void close(){
       
       Stage stage = (Stage) bt_CancelAddNewUser.getScene().getWindow();
       stage.close();
    }    
}
