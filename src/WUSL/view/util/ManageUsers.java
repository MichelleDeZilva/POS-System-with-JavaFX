package WUSL.view.util;

import WUSL.dynaminARR.DynamicArray;
import WUSL.dynaminARR.SinglyLinkedList;
import WUSL.view.util.Customer;

public class ManageUsers {

    private static SinglyLinkedList<Customer> usersList = new SinglyLinkedList<>();
    private static Customer currentUser = null;
    private static int currentUserIndex = -1;

    public static boolean registerCustomer(String id, String name,String address){

        for (int i = 0; i < usersList.getSize() ; i++) {
            Customer customer = usersList.get(i);
            if (customer.getId().equals(id)){
                return false;
            }
        }

        Customer customer = new Customer(id, name,address);
        usersList.add(customer);
        return true;
    }

    public static boolean authenticate(String id,String name, String address){

        for (int i = 0; i < usersList.getSize() ; i++) {
            Customer customer = usersList.get(i);
            if (customer.getId().equals(id) &&
                customer.getName().equals(name)&& customer.getAddress().equals(address)){
                currentUser = customer;
                currentUserIndex = i;
                return true;
            }
        }

        return false;

    }

}
