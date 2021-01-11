package airlinekiosk;

public class Ticket {
    
    //Instance Variables
    String ticketNumber;
    String name;
    boolean isRefunded; //Outlines if the ticket has been refunded yet
    double price;

    public Ticket(String ticketNumber, String name, double price) {
        this.ticketNumber = ticketNumber;
        this.name = name;
        this.price = price;
        this.isRefunded = false; //Initializes as false, not refunded
    }
}
