package airlinekiosk;

public class Flight {

    String status;
    String airline;
    String flightNumber;
    String destination;
    String date;
    String time;
    String terminal;
    String plane;
    String price;
    Ticket[] ticket; //Array to store ticket objects
    int totalSeats; //Total seats available based on flight
    int seatsAvailable; //Seats left available on flight

    public Flight(String[] flightInfo) { //Takes a string array of flight information passed from Counter class
        this.status = flightInfo[0];
        this.airline = flightInfo[1];
        this.flightNumber = flightInfo[2];
        this.destination = flightInfo[3];
        this.date = flightInfo[4];
        this.time = flightInfo[5];
        this.terminal = flightInfo[6];
        this.plane = flightInfo[7];
        if (flightInfo.length == 9) { //Checks if there is a 9th input, as only departing flights have a price
            this.price = flightInfo[8];
        }
        this.totalSeats = (this.plane.equals("B747")) ? 400 : (this.plane.equals("B787")) ? 300 : (this.plane.equals("A310")) ? 250 : 0; //Sets amount of seats based on plane type
        this.seatsAvailable = totalSeats;
        this.ticket = new Ticket[this.totalSeats]; //Creates ticket array the size of seats on flight
    }

    public void createTickets(int ticketQuantity, String name) { //Takes an input of # of tickets desired, and name of person

        for (int i = this.totalSeats - this.seatsAvailable; i < this.totalSeats - this.seatsAvailable + ticketQuantity; i++) { //Generates ticket numbers from the first available seat
            this.ticket[i] = new Ticket(this.flightNumber + ":" + String.format("%03d", i), name, Double.parseDouble(this.price.substring(1))); //Creates tickets, stores them in the ticket[] array
            this.ticket[i].isRefunded = false; //Sets the isRefunded property to false initially; ticket has not yet been refunded at this point
            System.out.println(this.ticket[i].ticketNumber); //Prints unique ticket number
        }
        
        this.seatsAvailable -= ticketQuantity; //Reduces the seats available on the flight by the amount purchased
    }
}
