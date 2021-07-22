package WUSL.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import WUSL.view.util.Inventory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class viewSearchItemController {
    public TextField searchId;

    public void getSearchReport(ActionEvent actionEvent) throws JRException, SQLException, ClassNotFoundException {

        String id = searchId.getText();
        File file = new File("C:/Users/mf/Downloads/RAD Final/RAD_Project-master/src/WUSL/Reports/searchItem.jasper");

        JasperReport compileReport = (JasperReport) JRLoader.loadObject(file);

        DefaultTableModel dtm = new DefaultTableModel(new Object[]{"itemCode","description","unitPrice","qty"},0);

        Map<String,Object> params = new HashMap<>();
        params.put("itemId",id);

        Class.forName("com.mysql.jdbc.Driver");
        //Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root");
//        ObservableList<Inventory> inventories = itemTable.getItems();
//
//
//        for (Inventory inventory: inventories) {
//            Object[] rowData = {inventory.getCode(),inventory.getDescription(),inventory.getPrice(),inventory.getQty()};
//            dtm.addRow(rowData);
//        }

        JasperPrint filledReport = JasperFillManager.fillReport(compileReport,params,DriverManager.getConnection("jdbc:mysql://localhost:3306/inventoryInfo", "root", "root"));

        JasperViewer.viewReport(filledReport,false);
    }
}

