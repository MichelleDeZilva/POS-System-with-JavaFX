package WUSL.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import WUSL.view.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static WUSL.controller.manageCusController.customer;
import static WUSL.controller.manageInventoryController.inventory;


public class placeOrderController {
    @FXML
    private Button pAdd;
    @FXML
    private Button pRemove;
    @FXML
    private AnchorPane frmPlaceOrder;
    @FXML
    private TextField oID;
    @FXML
    private TextField cID;
    @FXML
    private TextField itemCode;
    @FXML
    private TextField currentDate;
    @FXML
    private TextField cName;
    @FXML
    private TextField placeDes;
    @FXML
    private TextField placeQty;
    @FXML
    private TextField price;
    @FXML
    private TextField avQty;
    @FXML
    private TableView<Order> placeTable;
    @FXML
    private TextField subTotal;
    static int count = 1;
     ArrayList<Order> orders = new ArrayList<>();
    final static ArrayList<Order> perOrders = new ArrayList<>();
    static double tot =0;
    static int temp;
    static double nTot = 0;
    static int decQty = 0;
    private static int num = 1;

    public void initialize() throws SQLException, ClassNotFoundException {

        setOrder();
        oID.setEditable(false);
        initClock();
        currentDate.setEditable(false);

        placeTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("ICode"));
        placeTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("des"));
        placeTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        placeTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("qty"));
        placeTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("netPrice"));


        ObservableList<Order> items = FXCollections.observableArrayList(orders);
        placeTable.setItems(items);

        placeDes.clear();
        avQty.clear();
        price.clear();
        placeQty.clear();

        placeTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Order>() {
                    @Override

                    public void changed(ObservableValue<? extends Order> ov, Order old_val, Order new_val) {
                        oID.setEditable(false);
                        cID.setEditable(false);
                        currentDate.setEditable(false);
                        itemCode.setEditable(false);
                        cName.setEditable(false);
                        avQty.setEditable(false);
                        price.setEditable(false);
                        placeDes.setEditable(false);

                        itemCode.setText(new_val.getICode());
                        placeDes.setText(new_val.getDes());
                        avQty.setText(String.valueOf(new_val.getAvaQty()));
                        price.setText(String.valueOf(new_val.getUnitPrice()));
                        placeQty.setText(String.valueOf(new_val.getQty()));

                    }
                });

    }

    private void initClock() {

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            currentDate.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
    public void placeADD(ActionEvent actionEvent) {

        String cId = cID.getText();
        String code = itemCode.getText();
        String des = placeDes.getText();
        int qty = Integer.parseInt(placeQty.getText());
        double unit = Double.parseDouble(price.getText());
        int realQty = Integer.parseInt(avQty.getText());
        double netTot = unit * qty;
        tot = tot + netTot;



        if (cId.trim().isEmpty() || code.trim().isEmpty() || String.valueOf(qty).trim().isEmpty()) {
            pAdd.disableProperty();
            pRemove.disableProperty();
            new Alert(Alert.AlertType.ERROR, "Can't have blank fields for customer Id , Item code and Quantity", ButtonType.OK).show();

        } else {

            if (realQty >= qty) {
                for (int j = 0; j < orders.size(); j++) {

                    if (orders.get(j) != null && code.equals(orders.get(j).getICode())) {
                        //orders.get(j).setAvaQty(temp);
                        nTot = orders.get(j).getNetPrice() + netTot;
                        decQty = orders.get(j).getQty() + qty;
                        orders.get(j).setNetPrice(nTot);
                        orders.get(j).setQty(decQty);
                        placeTable.refresh();
                        subTotal.setText(String.valueOf(tot));
                        cID.setEditable(false);
                        temp = orders.get(j).getAvaQty() - qty;
                        if (temp >= 0) {
                            realQty = temp;
                            orders.get(j).setAvaQty(realQty);
                            avQty.setText(String.valueOf(realQty));
                        }
                        itemCode.clear();
                        placeDes.clear();
                        avQty.clear();
                        price.clear();
                        placeQty.clear();
                        return;
                    }
                }


                    orders.add(new Order(code, des, unit, qty, netTot,realQty));
                    ObservableList<Order> items = FXCollections.observableArrayList(orders);
                    placeTable.setItems(items);
                    placeTable.refresh();
                    System.out.println(qty);
                    temp = realQty - qty;
                    realQty = temp;

                    for (int j = 0; j < orders.size(); j++) {
                        if (orders.get(j) != null && code.equals(orders.get(j).getICode())) {
                            orders.get(j).setAvaQty(realQty);
                        }

                    }
                    avQty.setText(String.valueOf(realQty));
                    subTotal.setText(String.valueOf(tot));
                    itemCode.clear();
                placeDes.clear();
                avQty.clear();
                price.clear();
                placeQty.clear();

            }
        }

    }

    public void placeRemove(ActionEvent actionEvent) {


        oID.setEditable(false);
        cID.setEditable(false);
        currentDate.setEditable(false);
        cName.setEditable(false);
        avQty.setEditable(false);
        price.setEditable(false);
        placeDes.setEditable(false);
        itemCode.setEditable(false);
        placeQty.setEditable(true);

        int no = Integer.parseInt(placeQty.getText());

        int index = placeTable.getSelectionModel().getSelectedIndex();

        if (no < orders.get(index).getQty()){
            int newQty = orders.get(index).getQty() - no;
            double newPrice = orders.get(index).getNetPrice() - (no * Double.parseDouble(price.getText()));
            double tot = Double.parseDouble(subTotal.getText()) - (no * Double.parseDouble(price.getText()));
            int availableQty = no + orders.get(index).getAvaQty();
            System.out.println("Available");
            System.out.println(availableQty);
            orders.get(index).setQty(newQty);
            avQty.setText(String.valueOf(availableQty));
            orders.get(index).setAvaQty(availableQty);
            orders.get(index).setNetPrice(newPrice);
            subTotal.setText(String.valueOf(tot));

            placeTable.refresh();
            itemCode.clear();
            placeDes.clear();
            avQty.clear();
            price.clear();
            placeQty.clear();

            avQty.setEditable(false);
            price.setEditable(false);
            placeDes.setEditable(false);
            itemCode.setEditable(true);
            placeQty.setEditable(true);

        }else if (no == orders.get(index).getQty()){
            orders.remove(index);

            double tot = Double.parseDouble(subTotal.getText()) - (no * Double.parseDouble(price.getText()));
            subTotal.setText(String.valueOf(tot));

            itemCode.clear();
            placeDes.clear();
            avQty.clear();
            price.clear();
            placeQty.clear();

            avQty.setEditable(false);
            price.setEditable(false);
            placeDes.setEditable(false);
            itemCode.setEditable(true);
            placeQty.setEditable(true);

            ObservableList<Order> items = FXCollections.observableArrayList(orders);
            placeTable.setItems(items);

        }else{
            new Alert(Alert.AlertType.ERROR, "Quantity should less than Added quantity", ButtonType.OK).show();
        }

    }

    public void placeHolder(ActionEvent actionEvent) throws JRException, ClassNotFoundException, SQLException {
        int orderId = Integer.parseInt(oID.getText());
        String day = currentDate.getText();
        String Name = cName.getText();
        System.out.println("Check Name");
        System.out.println(Name);
        String cId = cID.getText();
        String total =subTotal.getText();
        String code = itemCode.getText();

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");

        PreparedStatement pst = connection.prepareStatement("INSERT INTO orderinvinfo VALUES(?,?,?)");
        pst.setObject(1, orderId);
        pst.setObject(2, cId);
        pst.setObject(3, day);
        int row1 = pst.executeUpdate();


        for (int i = 0;i < orders.size();i++) {
            PreparedStatement pst1 = connection.prepareStatement("INSERT INTO orderdetails VALUES(?,?,?,?,?,?)");
            pst1.setObject(1, orderId);
            pst1.setObject(2, orders.get(i).getICode());
            pst1.setObject(3, orders.get(i).getDes());
            pst1.setObject(4, orders.get(i).getUnitPrice());
            pst1.setObject(5, orders.get(i).getQty());
            pst1.setObject(6, orders.get(i).getNetPrice());
            int row2 = pst1.executeUpdate();

            PreparedStatement pst2 = connection.prepareStatement("UPDATE iteminfo SET qty=? WHERE itemCode=?");
            pst2.setObject(1,orders.get(i).getAvaQty());
            pst2.setObject(2,orders.get(i).getICode());
            int updateRow = pst2.executeUpdate();
        }

        generatePlaceOrderReport();

            setOrder();
            tot = 0;
            orders.clear();
            ObservableList<Order> items = FXCollections.observableArrayList(orders);
            placeTable.setItems(items);
            cID.clear();
            cName.clear();
    }

    public void placeHome(ActionEvent actionEvent) throws IOException {
        tot = 0;
        temp = 0;
        Parent parent = FXMLLoader.load(this.getClass().getResource("/WUSL/view/mainPage.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage = (Stage) frmPlaceOrder.getScene().getWindow();
        primaryStage.setScene(scene);
    }

    public void setOnKeyEnter(KeyEvent keyEvent) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
        cID.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {

                    String cid = cID.getText();
                    ;
                    try {
                        PreparedStatement pst1 = connection.prepareStatement("SELECT * FROM customerinformation WHERE cId=?");
                        pst1.setObject(1,cid);
                        ResultSet rst = pst1.executeQuery();
                        while (rst.next()) {
                            String ID = rst.getString("cId");
                            String Name = rst.getString("cName");
                            String add = rst.getString("cAddress");
                            cName.setText(Name);
                            cID.setEditable(false);
                            cName.setEditable(false);
                            itemCode.requestFocus();
                            return;

                        }



                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                        new Alert(Alert.AlertType.ERROR, "customer not in", ButtonType.OK).show();



                }
            }
        });


    }

    public void PressEnterForItem(KeyEvent keyEvent) throws ClassNotFoundException, SQLException {

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
        itemCode.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                    String item = itemCode.getText();

                    for (int i = 0; i < orders.size(); i++) {

                        if (orders.get(i) != null && item.equals(orders.get(i).getICode())) {
                            String description = orders.get(i).getDes();
                            int qty = orders.get(i).getAvaQty();
                            double uPrice = orders.get(i).getUnitPrice();
                            avQty.setText(String.valueOf(qty));
                            price.setText(String.valueOf(uPrice));
                            placeDes.setText(description);
                            return;
                        }
                    }

                    try {
                        PreparedStatement pst1 = connection.prepareStatement("SELECT * FROM iteminfo WHERE itemCode=?");
                        pst1.setObject(1,item);
                        ResultSet rst = pst1.executeQuery();

                        while (rst.next()) {
                            String ID = rst.getString("itemCode");
                            String des = rst.getString("description");
                            double uPrice = Double.parseDouble(rst.getString("unitPrice"));
                            int qt = Integer.parseInt(rst.getString("qty"));

                            placeDes.setText(des);
                            price.setText(String.valueOf(uPrice));
                            avQty.setText(String.valueOf(qt));

                            placeDes.setEditable(false);
                            price.setEditable(false);
                            avQty.setEditable(false);
                            placeQty.requestFocus();
                            return;

                        }



                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                        new Alert(Alert.AlertType.ERROR, "Item not in", ButtonType.OK).show();


                }
            }

        });


    }

    public void setOrder() throws ClassNotFoundException, SQLException {

        count=1;
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");

        PreparedStatement pst1 = connection.prepareStatement("SELECT oId FROM orderinvinfo");
        ResultSet rst = pst1.executeQuery();
        int c=0;

        while (rst.next()) {

            int ID = Integer.parseInt((rst.getString("oId")));
            c = rst.getRow();
            System.out.println(c);

        }
        System.out.println(c);
        oID.setText(String.valueOf(c+1));
    }

    public void generatePlaceOrderReport() throws JRException, SQLException, ClassNotFoundException {

        int ID = Integer.parseInt(oID.getText());
        String cDate = currentDate.getText();
        String cId = cID.getText();

        Class.forName("com.mysql.jdbc.Driver");
        File file = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/billInformation.jasper");

        JasperReport compileReport = (JasperReport) JRLoader.loadObject(file);

        Map<String,Object> params = new HashMap<>();

        params.put("oId",ID);
        params.put("oDate",cDate);
        params.put("cId",cId);

        JasperPrint filledReport = JasperFillManager.fillReport(compileReport,params,DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root"));

        JasperViewer.viewReport(filledReport,false);
    }

}
