import java.net.*;
import java.util.Scanner;
import java.io.*;

public class ThreadedClient {

    public static void main(String[] args) {
        // Initialize port variable
        int port = 0;

        // Check if a command line argument was passed
        if (args.length < 1) {
            System.out.println("Please pass a port number as a command line argument.");
            System.exit(-1);
        }
        else {
            // Get the port number
            port = Integer.parseInt(args[0]);
        }

        // Set the hostName variable
        // Hard coded the server's IP for less redundancy when testing
        String hostName = "139.62.210.155";

        /*  UNCOMMENT THIS CODE AND COMMENT OUT IF/ELSE BRANCH AND hostName LINE ABOVE IF YOU WANT TO TAKE HOSTNAME AS A COMMAND LINE ARG 

        // Initialize hostName variable
        String hostName = "";

        // Check if two command line arguments were passed
        if (args.length < 2) {
            System.out.println("Please pass the server IP address followed by the port number as a command line argument.");
            System.exit(-1);
        }
        else {
            // Get the host name
            hostName = args[0];
            // Get the port number
            port = Integer.parseInt(args[1]); 
        }

        */

        // Try to connect to the server on the given port number
        try (Socket socket = new Socket(hostName, port)){

            // Instantiate buffered readers and writers from the socket streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Scanner and variables used for getting input from user
            Scanner scanner = new Scanner(System.in);
            int input = 0;
            int requests = 0;
            String command = "";

            while (true) {
                // Print menu
                System.out.println("Please enter the number corresponding to the desired command: ");
                System.out.println("1: Date and Time");
                System.out.println("2: Uptime");
                System.out.println("3: Memory Use");
                System.out.println("4: Netstat");
                System.out.println("5: Current Users");
                System.out.println("6: Running Processes");
                System.out.println("7: Exit Program");

                // Read command number from user
                input = scanner.nextInt();

                // Check to see if user chose to exit program
                if (input == 7) {
					// Write the disconnect message to the server
                    writer.write("END");
					writer.newLine();
					writer.flush();

                    // Close everything up
					scanner.close();
					reader.close();
					writer.close();
					socket.close();

                    // Kill program
					System.exit(0);
                }

                // Ask user how many requests they want to run
                System.out.println("Please enter the number of requests");
                System.out.println("Options: 1, 5, 10, 15, 20, 25, 100");

                // Read request number from user
                requests = scanner.nextInt();

                // Update user
                System.out.println("Running request(s)...");

                // Switch statement to determine the command and store the correct string in the command variable
                switch(input) {
                    case 1:
                        // Date and Time
                        command = "Date and Time";
                        break;
                    case 2:
                        // Uptime
                        command = "Uptime";
                        break;
                    case 3:
                        // Memory Use
                        command = "Memory Use";
                        break;
                    case 4:
                        // Netstat
                        command = "Netstat";
                        break;
                    case 5:
                        // Current Users
                        command = "Current Users";
                        break;
                    case 6:
                        // Running Processes
                        command = "Running Processes";
                        break;
                    default:
                        break;
                }

                // Initialize a threads array to store each thread
                ClientThread threads[] = new ClientThread[requests];

                // Start the clock
                long startTime = System.nanoTime();

                // For loop to run the request however many times
                for (int i = 0; i < requests; i++) {
                    try {
                        // Create a new socket for this thread
                        Socket threadSocket = new Socket(hostName, port);

                        // Create a new ClientThread and store it in threads array
                        threads[i] = new ClientThread(threadSocket, command, i);

                        // Call run() in ClientThread using start()
                        threads[i].start();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }

                // Wait for the last ClientThread to terminate
                threads[requests-1].join();

                // Stop clock and calculate the elapsed time
                long endTime = System.nanoTime();
                long elapsedTime = (endTime - startTime)/1000000;

                // Print elapsed time
                System.out.printf("\nTotal elapsed time: %d ms\n", elapsedTime);

                // Print average operation time
                System.out.printf("Average operation time: %d ms\n", elapsedTime / requests);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

}