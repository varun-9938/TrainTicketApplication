package mainpack;
import model.Train;
import ticketpack.Ticket;
import ticketpack.TrainDAO;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

 class main {
    public static void main(String[] args)
    {
        int numOfPass=0;
        int trainNumber;
        Ticket ticket;
        int travelDate;

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the train number : ");
        trainNumber = sc.nextInt();

        while(true)
        {
            try
            {
                if(trainNumber > 1000 && trainNumber <= 1006)
                {
                    break;
                }
                else
                {
                    System.out.println("Enter the valid train number : ");
                    trainNumber = sc.nextInt();
                }

            }
            catch (Exception e)
            {
                System.out.println("Enter the valid train number.");
            }
        }
        Train train = null;
        try
        {
            train = TrainDAO.findTrain(trainNumber);
        }
        catch (SQLException e)
        {
            System.out.println("train not found");
        }
        System.out.println("Enter the Date of travel(ddMMyyyy)");
        Date d=new Date();
        SimpleDateFormat s=new SimpleDateFormat("ddMMyyyy");
        int currentDate=Integer.parseInt(s.format(d));
        while (true)
        {
            travelDate = sc.nextInt();
            if (travelDate <= currentDate) {
                System.out.println("Enter the date after current date");
            }
            else {
                break;
            }
        }

        ticket=new Ticket(train, travelDate);
        System.out.print("Enter the number of passengers : ");
        numOfPass = sc.nextInt();
        for(int i=0;i<numOfPass;i++)
        {
            System.out.println("Enter the "+(i+1)+" Passenger Name : ");
            String passName = sc.next();
            System.out.println("Enter the age of the  "+(i+1)+"  Passenger : ");
            int passAge = sc.nextInt();
            System.out.println("Enter the gender of the  "+(i+1)+ " Passenger : (Type 'M' or 'F') : " );
            char passGen = sc.next().charAt(0);
            while(true)
            {
                if(Character.toString(passGen).equalsIgnoreCase("M") || Character.toString(passGen).equalsIgnoreCase("F"))
                {
                    ticket.addPassenger(passName, passAge, passGen);
                    break;
                }
                else
                {
                    System.out.println("Please enter 'M' or 'F' : ");
                    passGen = sc.nextLine().charAt(0);
                }
            }
        }
        ticket.writeTicket();
        System.out.println("Ticket Booked Successfully.");
    }
}