package WUSL.controller;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import WUSL.view.util.Customer;
import WUSL.dbClass.dbConnect;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class manageCusController {
    @FXML
    public TextField cusId;
    @FXML
    public TextField cusName;
    @FXML
    public TextField cusAddress;
    @FXML
    public AnchorPane frmCus;

    static ArrayList<Customer> customer = new ArrayList<>();

    @FXML
    private TableView<Customer> cTable;

    public void initialize() throws ClassNotFoundException, SQLException {
        viewData();
        String id = cusId.getText();
        String name = cusName.getText();
        String address = cusAddress.getText();
        cTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Customer>() {
                    @Override

                    public void changed(ObservableValue<? extends Customer> ov, Customer old_val, Customer new_val) {
                        cusId.setEditable(false);
                        cusId.setText(new_val.getId());
                        cusName.setText(new_val.getName());
                        cusAddress.setText(new_val.getAddress());
                    }
                });
        cTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        cTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        cTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));

        ObservableList<Customer> items = FXCollections.observableArrayList(customer);
        cTable.setItems(items);

    }

    public void cNewCus(ActionEvent actionEvent) {

        cusId.setEditable(true);
        cusName.setEditable(true);
        cusAddress.setEditable(true);
        cusId.clear();
        cusName.clear();
        cusAddress.clear();
    }

    public void cSave(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        String id = cusId.getText();
        String name = cusName.getText();
        String address = cusAddress.getText();
        if (id.trim().isEmpty() || name.trim().isEmpty() || address.trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can't have blank fields", ButtonType.OK).show();
            return;
        } else if (cTable.getSelectionModel().getSelectedIndex() == -1) {

            PreparedStatement pst = dbConnect.getConnection().prepareStatement("INSERT INTO customerinformation VALUES(?,?,?)");
            pst.setObject(1, id);
            pst.setObject(2, name);
            pst.setObject(3, address);

            int row = pst.executeUpdate();


            if (row > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer added successfully", ButtonType.OK).show();
                System.out.println("Customer added successfully");

            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add", ButtonType.OK).show();
                System.out.println("Failed to add");
            }


        } else {
            for (int k = 0;k < loginController.user.size();k++) {
                if(loginController.regi.get(0).getName().equals(loginController.user.get(k).getName())) {
                    new Alert(Alert.AlertType.ERROR, "User Cannot Update Customer", ButtonType.OK).show();
                    return;
                }
            }
            updateCustomer();
        }
        viewData();
        //connection.close();



    }


    public void cDelete(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        for (int k = 0;k < loginController.user.size();k++) {
            if(loginController.regi.get(0).getName().equals(loginController.user.get(k).getName())) {
                new Alert(Alert.AlertType.ERROR, "User Cannot Delete customer", ButtonType.OK).show();
                return;
            }
        }

        int index = cTable.getSelectionModel().getSelectedIndex();

        String Id = cusId.getText();
        PreparedStatement pst4 = dbConnect.getConnection().prepareStatement("DELETE from customerinformation WHERE cId = ?");
        pst4.setObject(1, Id);

//            try{
//                new Alert(Alert.AlertType.ERROR, "Customer Place an order!!! Cannot delete customer", ButtonType.OK).show();
//            }catch (Exception e){
//                e.printStackTrace();
//            }

        if (pst4.executeUpdate() > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Deleted successfully", ButtonType.OK).show();
            System.out.println("Customer Deleted successfully");
            viewData();
        } else {
            new Alert(Alert.AlertType.ERROR, "Customer Place an order!!! Cannot delete customer", ButtonType.OK).show();
            return;
        }

        cusId.setEditable(false);
        cusName.setEditable(false);
        cusAddress.setEditable(false);

    }

    public void cHome(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/WUSL/view/mainPage.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) frmCus.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    public void updateCustomer() throws SQLException, ClassNotFoundException {

        int index = cTable.getSelectionModel().getSelectedIndex();
        cusName.setEditable(true);
        cusAddress.setEditable(true);
        String Id = cusId.getText();
        String newName = cusName.getText();
        String newAddress = cusAddress.getText();
        PreparedStatement pst2 = dbConnect.getConnection().prepareStatement("UPDATE customerinformation SET cName=?,cAddress=? WHERE cId = ?");

        pst2.setObject(1,newName);
        pst2.setObject(2,newAddress);

        pst2.setObject(3,Id);
        int updatedRow = pst2.executeUpdate();
        if (updatedRow > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Customer Updated successfully", ButtonType.OK).showAndWait();
            System.out.println("Customer updated successfully");
            cTable.refresh();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to update", ButtonType.OK).show();
            System.out.println("Failed to update");
        }


        // connection.close();

    }

    public void viewData() throws ClassNotFoundException, SQLException {
        customer.clear();
        cTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        cTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        cTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        PreparedStatement pst1 = null;
        try {
            pst1 = dbConnect.getConnection().prepareStatement("SELECT * FROM customerinformation");
            ResultSet rst = pst1.executeQuery();
            while (rst.next()) {
                String ID = rst.getString("cId");
                String Name = rst.getString("cName");
                String add = rst.getString("cAddress");

                customer.add(new Customer(ID,Name,add));
                ObservableList<Customer> items = FXCollections.observableArrayList(customer);
                cTable.setItems(items);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void GenCusReport(ActionEvent actionEvent) throws JRException {

        /*JasperReport jasperReport = JasperCompileManager.compileReport("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/ReportCustomerDet.jrxml");

        Map<String, Object> parameters = new HashMap<String, Object>();


        JRDataSource dataSource = new JREmptyDataSource();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            JasperPrint filledReport = JasperFillManager.fillReport(jasperReport,parameters, DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root"));

            JasperViewer.viewReport(filledReport,false);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

       // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
             //   parameters, dataSource);



        File outDir = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/ReportCustomerDet.jasper");
        outDir.mkdirs();



        System.out.println("Done!"); */

        /*InputStream input = null;
        String query = "SELECT * from customerinformation" ;
        try{

            PreparedStatement pstmt = dbConnect.getConnection().prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                input = rs.getBinaryStream(1);
                System.out.println(input.toString());
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        Map map = new HashMap<String,Object>();

        Report.createReport(dbConnect.getConnection(),map,input);
        Report.showReport();*/

        File file = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/customerDet.jasper");

        JasperReport compileReport = (JasperReport) JRLoader.loadObject(file);

        DefaultTableModel dtm = new DefaultTableModel(new Object[]{"cId","cName","cAddress"},0);

        ObservableList<Customer> customers = cTable.getItems();

        for (Customer customer: customers) {
            Object[] rowData = {customer.getId(),customer.getName(),customer.getAddress()};
            dtm.addRow(rowData);
        }

        JasperPrint filledReport = JasperFillManager.fillReport(compileReport,new HashMap<>(),new JRTableModelDataSource(dtm));

        JasperViewer.viewReport(filledReport,false);

    }
}
