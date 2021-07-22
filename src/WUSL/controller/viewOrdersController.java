package WUSL.controller;

import WUSL.dbClass.dbConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import WUSL.view.util.Inventory;
import WUSL.view.util.Order;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.xml.soap.Name;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static WUSL.controller.manageCusController.customer;

public class viewOrdersController {
    @FXML
    private AnchorPane frmViewOrder;
    @FXML
    private TextField searchText;

    ArrayList<Order> newOrderList = new ArrayList<>();
    @FXML
    private TableView<Order> viewOrderTable;

    public void initialize(){
        viewOrderTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("orderID"));
        viewOrderTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("day"));
        viewOrderTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("cuID"));
        viewOrderTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("customerName"));
    }

    public void setOnKeyEnterForOrder(KeyEvent keyEvent) {
        searchText.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {

                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Connection connection = null;
                    try {
                        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
                        String oid = searchText.getText();
                        PreparedStatement pst1 = connection.prepareStatement("SELECT * FROM orderinvinfo WHERE oId=?");
                        pst1.setObject(1,oid);
                        ResultSet rst = pst1.executeQuery();
                        while (rst.next()) {
                            String ID = rst.getString("custId");
                            String oDate = rst.getString("orderDate");

                            PreparedStatement pst2 = connection.prepareStatement("SELECT * FROM customerinformation WHERE cId=?");
                            pst2.setObject(1,ID);
                            ResultSet rst1 = pst2.executeQuery();
                            String Name ="";
                            while (rst1.next()) {
                                Name = rst1.getString("cName");
                                break;
                            }

                            placeOrderController.perOrders.add(new Order(oid,oDate,ID,Name));
                            ObservableList<Order> items = FXCollections.observableArrayList(placeOrderController.perOrders);
                            viewOrderTable.setItems(items);

                            return;

                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }
            }
        });


    }

    public void viewOrderHome(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("/WUSL/view/mainPage.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) frmViewOrder.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    public void generateOrderRep(ActionEvent actionEvent) throws JRException, ClassNotFoundException, SQLException {

        File file = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/OrderInformaion.jasper");
        File f = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/subOrderDetails.jasper");

        JasperReport compileReport = (JasperReport) JRLoader.loadObject(file);
        JasperReport compReport = (JasperReport) JRLoader.loadObject(f);

        Map<String,Object> params = new HashMap<>();

        params.put("subRep",compReport);



        Class.forName("com.mysql.jdbc.Driver");


        JasperPrint filledReport = JasperFillManager.fillReport(compileReport,params, DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root"));

        JasperViewer.viewReport(filledReport,false);
    }
}
