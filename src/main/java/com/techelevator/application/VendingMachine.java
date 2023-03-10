package com.techelevator.application;

import com.techelevator.*;
import com.techelevator.ui.UserInput;
import com.techelevator.ui.UserOutput;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Month;

public class VendingMachine {

    public VendingMachine() {
    }

    public void run() {
        UserOutput userOutput = new UserOutput();
        UserInput userInput = new UserInput();

        Stock stock = new Stock();
        stock.stockItems();
        ItemManager itemManager = stock.getItemManager();

        File auditFile = userInput.setAuditFile();
        Audit audit = new Audit();

        while(true){
            userOutput.displayHomeScreen();
            String choice = userInput.getHomeScreenOption();

            if(choice.equals("display")) {
                // display the vending machine slots
                for (String s: stock.getDisplayOfItems()){
                    System.out.println(s);
                }
            }

            else if (choice.equals("purchase")) {
                Purchase purchase = new Purchase();

                Innerloop:
                while(true) {
                    String purchaseChoice = userInput.getPurchaseOption();
                    if (purchaseChoice.equals("insert")) {
                        BigDecimal inserted = userInput.getMoneyProvided();
                        //set current balance to the money provided
                        System.out.println(purchase.addToBalance(inserted));

                        //Record to audit.txt
                        BigDecimal currentBalance = purchase.getCurrentBalance();
                        String insertMessage = "Money inserted: ";
                        String currentTime = audit.getCurrentTime();
                        audit.recordToAudit(currentTime, insertMessage, inserted, currentBalance);
                    }

                    else if (purchaseChoice.equals("select")) {
                        //If user has not inserted any money:
                        while (purchase.getCurrentBalance().equals(BigDecimal.valueOf(0))) {
                            System.out.println("Please insert money first.");
                            System.out.println();
                            userInput.getMoneyProvided();
                        }

                        //Prompt user to select an item using the item key.
                        String selectedItem = userInput.getSelectedItem();

                        //Iterate through the list of Item objects and select the item using input key.
                        for (Item i : itemManager.getItems()) {
                            String key = i.getItemKey();
                            if (key.equalsIgnoreCase(selectedItem)) {
                                if (itemManager.isInStock(i)) {
                                    //Keep track of purchased items for discount
                                    purchase.increaseNumberOfItems();

                                    // record to audit and calculate change
                                    String currentTime = audit.getCurrentTime();
                                    BigDecimal currentBalance = purchase.getCurrentBalance();
                                    BigDecimal balanceAfterPurchase = purchase.calculateChange(i.getPurchasePrice(), currentTime.equals(Month.NOVEMBER));
                                    String itemAudit = i.getItemName();
                                    audit.recordToAudit(currentTime, itemAudit, key, currentBalance, balanceAfterPurchase);

                                    //update stock
                                    itemManager.decreaseStock(key);

                                    //display message and dispense item
                                    System.out.println();
                                    System.out.println("You received " + i.getItemName() + ".");
                                    userOutput.displayMessage(i.getType());
                                }

                                else {
                                    userOutput.outOfStock();
                                }
                            }
                        }
                    }

                    else if (purchaseChoice.equals("finish")) {
                        //If user did nothing:
                        while (purchase.getCurrentBalance().equals(BigDecimal.valueOf(0))) {
                            break Innerloop;
                        }

                        BigDecimal balanceBeforeChange = purchase.getCurrentBalance();
                        userOutput.displayChange(purchase.receiveChange());

                        //Record to audit.txt.
                        BigDecimal balanceAfterChange = purchase.getCurrentBalance();
                        String changeMessage = "CHANGE GIVEN: ";
                        String currentTime = audit.getCurrentTime();
                        audit.recordToAudit(currentTime, changeMessage, balanceBeforeChange, balanceAfterChange);
                        audit.addAuditString("");
                        break Innerloop;
                    }

                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String formatedBalance = formatter.format(purchase.getCurrentBalance());
                    System.out.println();
                    System.out.println("Remaining Balance: " + formatedBalance);
                    System.out.println();

                }
            }
            else if(choice.equals("exit")) {
                audit.printToFile(auditFile);
                break;
            }
        }
    }
    
}
