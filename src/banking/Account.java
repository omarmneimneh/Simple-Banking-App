package banking;


import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.random;

public class Account {

    Database dataBase;
    Random random;
    int balance = 0;


    Account(String fileName){
        this.dataBase=new Database(fileName);
        outerMenu();
    }

    public void outerMenu() {
        System.out.println();
        System.out.println("1. Create an account\n" +
                "2. Log into account\n" +
                "0. Exit");

        Scanner scanner = new Scanner(System.in);
        int input = scanner.nextInt();
        scanner.nextLine();
        switch (input) {

            case 1:
                createNewAccount();
                break;
            case 2:
                checkData(scanner, dataBase);
                break;

            case 0:
                System.out.println("Bye!");
                return;
        }
    }

    public void createNewAccount() {

        String cardNum = createCardNumber();
        random = new Random();

        int minPin = 1000;
        int maxPin = 9999;
        int pin = (int) (random()*(maxPin - minPin + 1) + minPin);
        int id = random.nextInt(999999999);
        int balance = 0;
        System.out.println();
        System.out.println("Your card has been created\n" +
                "Your card number:\n" + cardNum);
        System.out.println("Your card PIN:\n" + pin);


        String dataBasePin = String.valueOf(pin);
        this.dataBase.addCard(id,cardNum,dataBasePin, balance);

        outerMenu();
    }

    public void checkData (Scanner scanner, Database dataBase) {
        boolean result = false;
        System.out.println();
        System.out.println("Enter your card number:");
        String cardInput = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String pinInput = scanner.nextLine();
        result = dataBase.checkForCard(cardInput, pinInput);
        if (result) {
            System.out.println("You have successfully logged in!");
            innerMenuGreetings(scanner, cardInput);
        } else {
            System.out.println("Wrong card number or PIN!");
            outerMenu();
        }
    }

    public void innerMenuGreetings (Scanner scanner, String card) {
        System.out.println();
        System.out.println("1. Balance\n" +
                "2. Add Income\n" +
                "3. Do transfer\n"+
                "4. Close account\n"+
                "5. Log out\n"+
                "0. Exit");
        innerMenu(scanner, card);
    }

    public void innerMenu (Scanner scanner,  String currentCardNum){
        int income = 0;
        int transfer = 0;

        int secondInput = scanner.nextInt();
        scanner.nextLine();
        switch (secondInput) {
            case 1:
                System.out.println();
                System.out.println("Balance: "+dataBase.viewBalance(currentCardNum));
                innerMenuGreetings(scanner, currentCardNum);
                break;
            case 2:
                addIncome(currentCardNum, scanner);
                innerMenuGreetings(scanner, currentCardNum);

                break;
            case 3:
                transfer(currentCardNum, scanner);
                innerMenuGreetings(scanner, currentCardNum);
                break;
            case 4:
                deleteAccount(currentCardNum);
                outerMenu();
                break;
            case 5:
                System.out.println("You have succefully logged out!");
                outerMenu();
            case 0:
                System.out.println("Bye!");
                return;
        }
    }

    public String createCardNumber(){
        Random random = new Random();
        String bin = "400000";
        int length = 16-(bin.length()+1);
        StringBuilder builder = new StringBuilder();
        builder=builder.append(bin);
        for(int i = 0; i<length; i++){
            int digit = random.nextInt(10);
            builder.append(digit);
        }

        int checkDigit = generateCheckDigit(builder.toString());
        builder.append(checkDigit);
        return builder.toString();
    }

    public int generateCheckDigit(String number) {
        int sum = 0;

        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }
        int mod = sum%10;
        return ((mod == 0) ? 0 : 10 - mod);
    }

    public void addIncome(String cardNum,Scanner scanner){
        System.out.println("Enter income: ");
        int income = scanner.nextInt();
        System.out.println("income: "+income);
        scanner.nextLine();
        dataBase.addBalance(income,cardNum);
    }

    public void transfer(String cardNum1, Scanner scanner){
        int currentAmount = dataBase.viewBalance(cardNum1);
        System.out.println("Enter card number: ");
        String cardNum2 = scanner.nextLine();
        if(!cardNum2.equals(cardNum1)){
            if(luhnAlgorithmCheck(cardNum2)) {
                if(dataBase.transferCheck(cardNum2)){
                    System.out.println("Enter how much money you want to transfer:");
                    int transferAmount = scanner.nextInt();
                    scanner.nextLine();
                    boolean amountsLineUp = verifyTransferAmount(transferAmount, currentAmount);
                    if(amountsLineUp){
                        dataBase.subtractBalance(transferAmount, cardNum1);
                        dataBase.addBalance(transferAmount, cardNum2);
                    } else System.out.println("Not enough money!");
                } else System.out.println("Such a card does not exist.");
            }else System.out.println("Probably you made a mistake in the card number. Please try again!");
        }else System.out.println("You can't transfer money to the same account!");
    }

    public boolean verifyTransferAmount(int requestedAmount, int amountAvailable){
        return requestedAmount<amountAvailable;
    }

    public boolean luhnAlgorithmCheck(String cardNum2){
        int sum = 0;

        for (int i = 0; i < cardNum2.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(cardNum2.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }
        int mod = sum%10;
        return mod==0;
    }

    public void deleteAccount(String cardNum){

        dataBase.deleteCard(cardNum);
        System.out.println("The account has been closed!");
    }
}
