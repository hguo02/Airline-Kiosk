package airlinekiosk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    
    //Instance Variables
    private static Scanner keyboard = new Scanner(System.in);
    private static DecimalFormat df = new DecimalFormat("$###.00");
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static LocalDateTime time = LocalDateTime.now();
    private static String currentTime = String.format("%s:%s", (time.getHour() < 10) ? "0" + time.getHour() : time.getHour(),
            (time.getMinute() < 10) ? "0" + time.getMinute() : time.getMinute());
    private static String currentDate = String.format("%s/%s/%s", (time.getDayOfMonth() < 10) ? "0" + time.getDayOfMonth() : time.getDayOfMonth(),
            (time.getMonthValue() < 10) ? "0" + time.getMonthValue() : time.getMonthValue(), time.getYear());
    private static final LocalDateTime cutOffTime = LocalDateTime.parse(currentDate + " " + currentTime, formatter);
    private static ArrayList<Flight> planeInfo = new ArrayList();
    private static ArrayList<Flight> purchase = new ArrayList();
    private static boolean firstTime = true;

    public static void main(String args[]) throws IOException {

        int input;

        while (true) {           
            do {
                System.out.println("************ MAIN MENU ************");
                System.out.println("1. Update Database");
                System.out.println("2. Display Arrivals");
                System.out.println("3. Display Departures");
                System.out.println("4. Display Air Canada Flights");
                System.out.println("5. Purchase Tickets");
                System.out.println("6. Refund Tickets");
                System.out.println("7. Logoff");
                
                try {
                    input = keyboard.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Invalid menu entry!");
                    keyboard.next();
                }
            } while (true);
            

            switch (input) {

                case 1:
                    updateDatabase();
                    break;

                case 2:
                    displayArrivals();
                    break;

                case 3:
                    displayDepartures();
                    break;

                case 4:
                    displayAirCanada();
                    break;

                case 5:
                    purchaseTickets();
                    break;

                case 6:
                    refundTickets();
                    break;

                case 7:
                    logoff();
                    break;

                default:
                    System.out.println("Invalid menu entry!");
                    break;

            }
        }
    }
    
    public static void updateDatabase() {
        System.out.println("Please enter the name of your file.");
        keyboard.nextLine();
        String input = keyboard.nextLine();
        
        try {
            //Initializes Readers
            FileReader fileReader = new FileReader(input);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            ArrayList<String> textReader = new ArrayList();
            
            //Reads text file provided
            while (bufferedReader.ready()) {
                textReader.add(bufferedReader.readLine());
            }
            
            //Closing reading resources
            fileReader.close();
            bufferedReader.close();
            
            for (int i = 0; i < textReader.size(); i++) {
                planeInfo.add(new Flight(textReader.get(i).split(","))); // passes on flight info to each plane created
            }

        } catch (IOException e) { //Catches exception for file not found
            System.out.println("File Not Found! Please try again.");
        }
    }

    public static void displayArrivals() {
        System.out.printf("Arrival flights are:%n");
        System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", "Airline", "Flight Number", "Destination", "Date", "Time", "Terminal");

        for (int i = 0; i < planeInfo.size(); i++) { //Checks arraylist size, loops for 'planeInfo.size()' iterations
            if (planeInfo.get(i).status.equals("ARR") && currentDate.equals(planeInfo.get(i).date)) { //Checks plane is arriving, and that the date of arrival is today
                System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", planeInfo.get(i).airline, planeInfo.get(i).flightNumber, planeInfo.get(i).destination,
                        planeInfo.get(i).date, planeInfo.get(i).time, planeInfo.get(i).terminal);
            }
        }
    }

    public static void displayDepartures() {
        LocalTime now = LocalTime.parse(currentTime); //Checks current time
        LocalTime departureTime;

        System.out.printf("Departure flights are:%n");
        System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", "Airline", "Flight Number", "Destination", "Date", "Time", "Terminal");

        for (int i = 0; i < planeInfo.size(); i++) {
            departureTime = LocalTime.parse(planeInfo.get(i).time); //Gets the departure time of flight(i)

            // If condition checks flight is departing, departing today, and departure time is after current time
            if (planeInfo.get(i).status.equals("DEP") && currentDate.equals(planeInfo.get(i).date) && departureTime.isAfter(now)) {
                System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", planeInfo.get(i).airline, planeInfo.get(i).flightNumber, planeInfo.get(i).destination,
                        planeInfo.get(i).date, planeInfo.get(i).time, planeInfo.get(i).terminal);
            }
        }
    }

    public static void displayAirCanada() {
        LocalTime now = LocalTime.parse(currentTime); //Gets current time
        LocalTime upcomingFlightTimes;

        System.out.printf("Air Canada flights are:%n");
        System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", "Airline", "Flight Number", "Destination", "Date", "Time", "Terminal");

        for (int i = 0; i < planeInfo.size(); i++) {
            upcomingFlightTimes = LocalTime.parse(planeInfo.get(i).time); //Gets the flight time of flight(i)
            
            //If condition checks flight is an Air Canada flight, flight is from today, and flight has not yet arrived or departed
            if (planeInfo.get(i).flightNumber.substring(0, 2).equals("AC") && currentDate.equals(planeInfo.get(i).date) && upcomingFlightTimes.isAfter(now)) {
                System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s %n", planeInfo.get(i).status, planeInfo.get(i).flightNumber, planeInfo.get(i).destination,
                        planeInfo.get(i).date, planeInfo.get(i).time, planeInfo.get(i).terminal);
            }
        }
    }

    public static void purchaseTickets() {
        int choiceNumber = 0;
        int flightChoice = 0;
        int ticketQuantity = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime departureTime;
        
        if (purchase.isEmpty() && !firstTime) { //Checks if there are any flights still available
            System.out.println("All departing Air Canada flights are sold out!");
        } else {
            System.out.printf("All departing Air Canada flights are:%n");
            System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s%-20s %n", "Choice", "Flight Number", "Destination", "Date", "Time", "Terminal", "Seats Left", "Price");

            for (int i = 0; i < planeInfo.size(); i++) {
                departureTime = LocalDateTime.parse(planeInfo.get(i).date + " " + planeInfo.get(i).time, formatter); //Gets flight(i)'s date and time of departure
                //If condition checks that it is an Air Canada flight, departing, it is still 1 hour before departure, and if there are still seats available
                if (planeInfo.get(i).flightNumber.substring(0, 2).equals("AC") && planeInfo.get(i).status.equals("DEP") && cutOffTime.plusHours(1).isBefore(departureTime) && planeInfo.get(i).seatsAvailable > 0) {
                    choiceNumber++;
                    System.out.printf("%-20d%-20s%-20s%-20s%-20s%-20s%-20s%-20s %n", choiceNumber, planeInfo.get(i).flightNumber, planeInfo.get(i).destination,
                            planeInfo.get(i).date, planeInfo.get(i).time, planeInfo.get(i).terminal, planeInfo.get(i).seatsAvailable, planeInfo.get(i).price);

                    if (firstTime) { //If it is first time they are purchasing, it will add the flight information into the purchase arraylist
                        purchase.add(planeInfo.get(i));
                    }
                }
            }

            firstTime = false; //Sets to false, no longer adds the flight information into the purchase arraylist for any other iterations

            System.out.println("Which flight would you like?");

            while (true) {
                try {
                    flightChoice = keyboard.nextInt() - 1; //Collects user flight choice
                    purchase.get(flightChoice); //Attemps to get the user flight choice from arraylist
                    break;
                } catch (InputMismatchException e) { //Catches incorrect input
                    System.out.println("Invalid input! Please try again!");
                    keyboard.nextLine();
                    continue;
                } catch (IndexOutOfBoundsException e) { //Catches an input of an invalid choice
                    System.out.println("Please select a valid choice!");
                    keyboard.nextLine();
                    continue;
                }
            }

            System.out.println("What is your name?");
            keyboard.nextLine();
            String name = keyboard.nextLine(); //Gets name

            System.out.println("How many tickets would you like to purchase?");

            while (ticketQuantity > purchase.get(flightChoice).seatsAvailable || ticketQuantity <= 0) {
                try {
                    ticketQuantity = keyboard.nextInt(); //Gets user's amount of tickets desired

                    //Checks if there are enough seats for amount of tickets desired
                    if (ticketQuantity > purchase.get(flightChoice).seatsAvailable || ticketQuantity <= 0) {
                        System.out.println("There are not enough seats on this flight! \nSeats available: " + purchase.get(flightChoice).seatsAvailable);
                    }

                    if (purchase.get(flightChoice).seatsAvailable == 0) {
                        break;
                    }

                } catch (InputMismatchException e) { //Catches improper input of ticket quantity
                    System.out.println("Invalid input! Please try again.");
                    keyboard.nextLine();
                }
            }

            String price = df.format(Double.parseDouble(purchase.get(flightChoice).price.substring(1)) * ticketQuantity); //Calculates the total cost

            System.out.println("========================================================================================================================================");
            System.out.println("Invoice:");
            System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s %n", "Flight Number", "Destination", "Date", "Time", "Terminal", "Quantity", "Price");
            System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s %n", purchase.get(flightChoice).flightNumber, purchase.get(flightChoice).destination, 
                    purchase.get(flightChoice).date, purchase.get(flightChoice).time, purchase.get(flightChoice).terminal, ticketQuantity, price);
            System.out.println("\nYour ticket numbers are:");

            purchase.get(flightChoice).createTickets(ticketQuantity, name); //Creates ticket objects

            //If there are no longer any seats available on the flight, it is removed from the options
            if (purchase.get(flightChoice).seatsAvailable == 0) {
                purchase.remove(flightChoice); //Removes sold out flight from arraylist
            }

            System.out.println("\nThank you for your business, " + name + "!");
            System.out.println("========================================================================================================================================");
        }
    }
    
    public static void refundTickets() {
        LocalTime cutoffCheck = LocalTime.parse(currentTime).plusHours(12); //Gets the current time, adds 12 hours to make sure flight is within refund period
        boolean valid = false; //Used for if statement, set to true when ticket is valid, otherwise is left false
        
        System.out.println("Please enter a valid ticket number:");
        String ticketNumber = keyboard.next();

        for (int i = 0; i < purchase.size(); i++) {
            for (int j = 0; j < purchase.get(i).totalSeats - purchase.get(i).seatsAvailable; j++) {
                if (!purchase.get(i).ticket[j].isRefunded) { //Checks ticket refund status
                    //Checks if ticket number is a purchased ticket, and that 
                    if (ticketNumber.equals(purchase.get(i).ticket[j].ticketNumber) && cutoffCheck.isBefore(LocalTime.parse(purchase.get(i).time))) {
                        valid = true;
                        purchase.get(i).ticket[j].isRefunded = true;
                        System.out.println("Your refund has been approved in the amount of " + purchase.get(i).price + ". Have a nice day " + purchase.get(i).ticket[j].name);

                        break;
                    }
                }
            }
        }
        
        if (!valid) {
            System.out.println("Sorry! Invalid ticket number. Please try again.");
        }
    }

    public static void logoff() {
        double refund = 0.0;
        double sales = 0.0;

        System.out.println("\n\n\nSummary for " + currentDate);
        System.out.println("\nPurchases:\n");
        System.out.printf("%-20s%-20s%-20s %n", "Flight Number", "TicketNumber", "Price");
        
        for (int i = 0; i < purchase.size(); i++) {
            for (int j = 0; j < purchase.get(i).totalSeats - purchase.get(i).seatsAvailable; j++) { //Runs for however many tickets were sold
                sales += Double.parseDouble(purchase.get(i).price.substring(1)); //Adds up total amount sold, including refunded tickets
                System.out.printf("%-20s%-20s%-20s %n", purchase.get(i).flightNumber, purchase.get(i).ticket[j].ticketNumber, purchase.get(i).price);

            }
        }
        
        System.out.println("======================================================================");
        System.out.printf("%-40s%s %n", "Total Sales:", df.format(sales));
        System.out.println("\nRefunds:\n");
        System.out.printf("%-20s%-20s%-20s %n", "Flight Number", "TicketNumber", "Price");

        for (int i = 0; i < purchase.size(); i++) {
            for (int j = 0; j < purchase.get(i).totalSeats - purchase.get(i).seatsAvailable; j++) {
                if (purchase.get(i).ticket[j].isRefunded) { //Checks for whichever tickets are refunded
                    refund += Double.parseDouble(purchase.get(i).price.substring(1)); //Adds up total amount refunded
                    System.out.printf("%-20s%-20s%-20s %n", purchase.get(i).flightNumber, purchase.get(i).ticket[j].ticketNumber, purchase.get(i).price);
                }
            }
        }

        System.out.println("======================================================================");
        System.out.printf("%-40s%s%n", "Total Refunds:", df.format(refund));
        System.out.println("**********************************************************************");
        System.out.printf("%-40s%s%n", "Profit:", df.format(sales - refund));

        System.exit(0); //Exits program
    }
}
