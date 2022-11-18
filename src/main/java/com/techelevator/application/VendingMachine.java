package com.techelevator.application;

import com.techelevator.Audit;
import com.techelevator.Item;
import com.techelevator.ItemManager;
import com.techelevator.Purchase;
import com.techelevator.ui.UserInput;
import com.techelevator.ui.UserOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class VendingMachine {
    private Purchase purchase;

    public VendingMachine() {
    }

    public void run() {
        UserOutput userOutput = new UserOutput();
        UserInput userInput = new UserInput();

        File stockFile = userInput.setStockFile();
        File auditFile = userInput.setAuditFile();

        List<String> auditStrings = new ArrayList<>();
        Audit audit = new Audit(auditStrings);
        String audits = "";

        //Created a list of Item class objects and initializing itemManager with list.
        List<Item> vendingItems = new ArrayList<>();
        ItemManager itemManager = new ItemManager(vendingItems);

        //Created a string to display each line and added it to a list.
        String displayItems = "";
        List<String> displayItem = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(stockFile)) {
            while (fileScanner.hasNextLine()) {
                String eachLine = fileScanner.nextLine();
                String[] vending = eachLine.split(",");

                //For each line in the file, the line gets split and a new item object is initialized.
                Item item = new Item(vending[0], vending[1], vending[2], vending[3]);

                //Appending the new string.
                displayItems = vending[0] + " " + vending[1] + " " + vending[2];
                displayItem.add(displayItems);

                //Added item into list of objects.
                vendingItems.add(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        while(true){
            userOutput.displayHomeScreen();
            String choice = userInput.getHomeScreenOption();

            if(choice.equals("display")) {
                // display the vending machine slots
                for (String s: displayItem){
                    System.out.println(s);
                }
            }

            else if(choice.equals("purchase")) {
                //purchase = new purchase();
                String purchaseChoice = userInput.getPurchaseOption();

                if (purchaseChoice.equals("insert")){
                    BigDecimal inserted = userInput.getMoneyProvided();
                    //set currentbalance to the money provided
                    purchase.setCurrentBalance(inserted);

                    //Record to audit.txt
                    BigDecimal currentBalance = purchase.getCurrentBalance();
                    String insertMessage = "Money inserted: ";
                    String currentTime = audit.getCurrentTime();
                    audits = String.format("%-22d%-22d%-22d%-22d\n",currentTime, insertMessage, inserted, currentBalance);
                    auditStrings.add(audits);
                }

                else if (purchaseChoice.equals("select")){
                    //Prompt user to select an item using the item key.
                    String selectedItem = userInput.getSelectedItem();

                    List<String> samePurchasePrice = new ArrayList<>();
                    //Iterate through the list of Item objects and select the item using input key.
                    for(Item i: itemManager.getItems()) {
                        String key = i.getItemKey();
                        if (key.equals(selectedItem)) {
                        //update and display currentBalance
                            BigDecimal currentBalance = purchase.getCurrentBalance();
                            BigDecimal balanceAfterPurchase = purchase.calculateChange(i.getPurchasePrice());
                            System.out.println("Your remaining balance: " + purchase.getCurrentBalance());

                            // record to audit
                            String itemAudit = i.getItemName();
                            String currentTime = audit.getCurrentTime();
                            audits = String.format("%-22d%-22d%-22d%-22d%-22d\n",currentTime, itemAudit, key, currentBalance, balanceAfterPurchase);
                            auditStrings.add(audits);
                        }
                    }


                }
                else if (purchaseChoice.equals("finish")){
                    

                    userOutput.displayHomeScreen();
                }

            }

            else if(choice.equals("exit")) {
                //good bye
                break;
            }
        }
    }
    
}
