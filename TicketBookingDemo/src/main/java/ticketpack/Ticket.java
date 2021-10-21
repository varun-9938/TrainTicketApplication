package ticketpack;

import lombok.Setter;
import model.Passenger;
import model.Train;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Ticket
{
    private int count = 100;
    private String pnr;
    @Setter
    private int travelData;
    private String travelDateString;
    private Train train;
    private TreeMap<Passenger, Integer> passengers = null;
    private Connection connection;
    private ArrayList<Passenger> passengerArrayList = null;
    public String PNRNumber;


    public Ticket(Train train, int date)
    {
        passengerArrayList = new ArrayList<>();
        passengers = new TreeMap<>();
        this.train = train;
        this.travelData = date;
        travelDateString = Integer.toString(travelData);
        System.out.println(travelData);

    }

    //To generate PNR number
    private String generatePNR() throws NullPointerException
    {
        connection = DBManager.getConnection();
        try
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("" +
                    "select *from TRAINS where TRAIN_NO = " + train.getTrainNo());
            while (resultSet.next())
            {
                //To get 0 index character, convert it to UpperCase in concatenate with date
                pnr = String.valueOf(resultSet.getString(3).charAt(0)).toUpperCase() +
                        String.valueOf(resultSet.getString(4).charAt(0)).toUpperCase()
                        + "_" + travelDateString.substring(4,8)+ travelDateString.substring(2,4)+ travelDateString.substring(0,2)+
                        "_" + ++count;
                System.out.println(pnr);
                return pnr;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    //To calculate the ticket price based on the conditions given
    private double calcPassengerFare(Passenger passenger)
    {
        if (passenger.getAge() <= 12)
        {
            return (train.getTicketPrice() * 0.5);
        }
        if (passenger.getAge() >= 60)
        {
            return (train.getTicketPrice() * 0.6);
        }
        if (passenger.getGender() == 'F' || passenger.getGender() == 'f')
        {
            return (train.getTicketPrice() * 0.75);
        }
        else
        {
            return train.getTicketPrice();
        }
    }

    //To add passenger with their details given and the ticket price that's calculate
    public void addPassenger(String name, int age, char gender)
    {
        passengerArrayList.add(new Passenger(name, age, gender));
        passengers.put(new Passenger(name, age, gender), (int)calcPassengerFare(new Passenger(name, age, gender)));
    }

    //To calculate total Ticket Price
    private double calculateTotalTicketPrice()
    {
        double totalPrice = 0.0;
        for (double p : passengers.values())
        {
            totalPrice += p;
        }
        return totalPrice;
    }

    private StringBuilder generateTicket()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(PNRNumber + ","
                + train.getTrainNo() + ","
                + train.getTrainName() + ","
                + train.getSource() + ","
                + train.getDestination() + ","
                + travelData + ","
                + calculateTotalTicketPrice());
        return stringBuilder;
    }

    //To write the booked ticket details into a file
    public void writeTicket()
    {
        PNRNumber=generatePNR();
        File file = new File("C:\\Users\\user114\\Desktop", PNRNumber+".txt");
        try
        {
            file.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter))
        {
            StringBuilder stringBuilder = generateTicket();

            bufferedWriter.write("PNR: " + stringBuilder.toString().split(",")[0]);
            bufferedWriter.newLine();

            bufferedWriter.write("Train no: " + stringBuilder.toString().split(",")[1]);
            bufferedWriter.newLine();

            bufferedWriter.write("Train name: " + stringBuilder.toString().split(",")[2]);
            bufferedWriter.newLine();

            bufferedWriter.write("From: " + stringBuilder.toString().split(",")[3]);
            bufferedWriter.newLine();

            bufferedWriter.write("To: " + stringBuilder.toString().split(",")[4]);
            bufferedWriter.newLine();

            bufferedWriter.write("Travel Date: " + stringBuilder.toString().split(",")[5]);
            bufferedWriter.newLine();

            bufferedWriter.write("Passengers: ");
            bufferedWriter.newLine();

            bufferedWriter.write("Name\t\tAge\t\tGender\t\tFare");
            bufferedWriter.newLine();

            for (Passenger passenger : passengers.keySet())
            {
                bufferedWriter.write(passenger.getName()+"\t\t");
                bufferedWriter.write(String.valueOf(passenger.getAge())+"\t\t");
                bufferedWriter.write(passenger.getGender()+"\t\t");
                bufferedWriter.write(String.valueOf(passengers.get(passenger))+"\t\t");
                bufferedWriter.newLine();
            }

            bufferedWriter.write("Total Price:  " + calculateTotalTicketPrice());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}