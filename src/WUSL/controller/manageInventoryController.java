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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import WUSL.view.util.Customer;
import WUSL.view.util.Inventory;
import WUSL.view.util.ManageItems;
import WUSL.view.util.ManageUsers;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static WUSL.controller.manageCusController.customer;

public class manageInventoryController {
    @FXML
    public AnchorPane itemId;
    @FXML
    public TextField itemCode;
    @FXML
    public TextField itemDes;
    @FXML
    public TextField itemPrice;
    @FXML
    public TextField ItemQty;

    static ArrayList<Inventory> inventory = new ArrayList<>();

    @FXML
    private TableView<Inventory> itemTable;

    public void clickNewItem(ActionEvent actionEvent) {
        itemCode.setEditable(true);
        itemDes.setEditable(true);
        itemPrice.setEditable(true);
        ItemQty.setEditable(true);
        itemCode.clear();
        itemDes.clear();
        itemPrice.clear();
        ItemQty.clear();
    }

    public void ItemHome(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/WUSL/view/mainPage.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) itemId.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    public void itemSave(ActionEvent actionEvent) throws ClassNotFoundException, SQLException {

        for (int k = 0; k < loginController.user.size(); k++) {
            if (loginController.regi.get(0).getName().equals(loginController.user.get(k).getName())) {
                new Alert(Alert.AlertType.ERROR, "User Cannot Manage Inventory", ButtonType.OK).show();
                return;
            }
        }

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
        String code = itemCode.getText();
        String des = itemDes.getText();
        double price = Double.parseDouble(itemPrice.getText());
        int qty = Integer.parseInt(ItemQty.getText());
        if (code.trim().isEmpty() || des.trim().isEmpty() || String.valueOf(price).trim().isEmpty() || String.valueOf(qty).trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Can't have blank fields", ButtonType.OK).show();
            return;
        } else if (itemTable.getSelectionModel().getSelectedIndex() == -1) {

            PreparedStatement pst = connection.prepareStatement("INSERT INTO iteminfo VALUES(?,?,?,?)");
            pst.setObject(1, code);
            pst.setObject(2, des);
            pst.setObject(3, price);
            pst.setObject(4, qty);


            int row = pst.executeUpdate();


            if (row > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item added successfully", ButtonType.OK).show();
                System.out.println("Item added successfully");
                viewData();
                return;
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to add", ButtonType.OK).show();
                System.out.println("Failed to add");
                return;
            }
        } else {
            updateCustomer();
            viewData();
            return;
        }

    }

    public void initialize() throws SQLException, ClassNotFoundException {
        viewData();

        itemTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Inventory>() {
                    @Override

                    public void changed(ObservableValue<? extends Inventory> ov, Inventory old_val, Inventory new_val) {
                        itemCode.setEditable(false);
                        itemCode.setText(new_val.getCode());
                        itemDes.setText(new_val.getDescription());
                        itemPrice.setText(String.valueOf(new_val.getPrice()));
                        ItemQty.setText(String.valueOf(new_val.getQty()));


                    }
                });

        itemTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        itemTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        itemTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));
        itemTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty"));

        ObservableList<Inventory> items = FXCollections.observableArrayList(inventory);

        itemTable.setItems(items);
        itemCode.setEditable(false);
        itemDes.setEditable(true);
        itemPrice.setEditable(true);
        ItemQty.setEditable(true);

    }
    public void itemDelete(ActionEvent actionEvent) throws ClassNotFoundException, SQLException {

        for (int k = 0; k < loginController.user.size(); k++) {
            if (loginController.regi.get(0).getName().equals(loginController.user.get(k).getName())) {
                new Alert(Alert.AlertType.ERROR, "User Cannot Manage Inventory", ButtonType.OK).show();
                return;
            }
        }

        int index = itemTable.getSelectionModel().getSelectedIndex();

        String Id = itemCode.getText();

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");

        PreparedStatement pst4 = connection.prepareStatement("DELETE from iteminfo WHERE itemCode = ?");
        pst4.setObject(1, Id);

        try{
            new Alert(Alert.AlertType.ERROR, "Item is in an order!!! Cannot delete Item", ButtonType.OK).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (pst4.executeUpdate() > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Item Deleted successfully", ButtonType.OK).show();
            System.out.println("Customer Deleted successfully");
            viewData();
        } else {
            new Alert(Alert.AlertType.ERROR, "Item is in an order!!! Cannot delete Item", ButtonType.OK).show();
            return;
        }

        itemCode.setEditable(false);
        itemDes.setEditable(false);
        itemPrice.setEditable(false);
        ItemQty.setEditable(false);
    }

    public void updateCustomer() throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
        int index = itemTable.getSelectionModel().getSelectedIndex();
        itemDes.setEditable(true);
        itemPrice.setEditable(true);
        ItemQty.setEditable(true);
        String Id = itemCode.getText();
        String DES = itemDes.getText();
        double PRICE = Double.parseDouble(itemPrice.getText());
        int QTY = Integer.parseInt(ItemQty.getText());
        PreparedStatement pst2 = connection.prepareStatement("UPDATE iteminfo SET description=?,unitPrice=?,qty=? WHERE itemCode = ?");

        pst2.setObject(1,DES);
        pst2.setObject(2,PRICE);
        pst2.setObject(3,QTY);

        pst2.setObject(4,Id);
        int updatedRow = pst2.executeUpdate();

        if (updatedRow > 0) {
            new Alert(Alert.AlertType.INFORMATION, "Item Updated successfully", ButtonType.OK).showAndWait();
            System.out.println("Item updated successfully");
            itemTable.refresh();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to update", ButtonType.OK).show();
            System.out.println("Failed to update");
        }


        connection.close();
    }

    public void viewData() throws ClassNotFoundException, SQLException {
        inventory.clear();
        itemTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        itemTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        itemTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));
        itemTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty"));
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");

        PreparedStatement pst1 = connection.prepareStatement("SELECT * FROM iteminfo");
        ResultSet rst = pst1.executeQuery();
        while (rst.next()) {
            String ID = rst.getString("itemCode");
            String des = rst.getString("description");
            double uPrice = Double.parseDouble(rst.getString("unitPrice"));
            int qt = Integer.parseInt(rst.getString("qty"));
            inventory.add(new Inventory(ID,des,uPrice,qt));
            ObservableList<Inventory> items = FXCollections.observableArrayList(inventory);
            itemTable.setItems(items);

        }
    }

    public void genItemReport(ActionEvent actionEvent) throws JRException {

        File file = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/itemDet.jasper");

        JasperReport compileReport = (JasperReport) JRLoader.loadObject(file);

        DefaultTableModel dtm = new DefaultTableModel(new Object[]{"itemCode","description","unitPrice","qty"},0);

        ObservableList<Inventory> inventories = itemTable.getItems();

        for (Inventory inventory: inventories) {
            Object[] rowData = {inventory.getCode(),inventory.getDescription(),inventory.getPrice(),inventory.getQty()};
            dtm.addRow(rowData);
        }

        JasperPrint filledReport = JasperFillManager.fillReport(compileReport,new HashMap<>(),new JRTableModelDataSource(dtm));

        JasperViewer.viewReport(filledReport,false);
    }

    public void itemSearch(ActionEvent actionEvent) throws IOException {

        Parent parent = FXMLLoader.load(this.getClass().getResource("/WUSL/view/viewSearchItem.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) itemId.getScene().getWindow();
        primaryStage.setScene(scene);
    }
}
