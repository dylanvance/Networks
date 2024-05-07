import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    
    public static void main(String args[]) throws IOException {

        Socket socket = null;
        InputStreamReader inReader = null;
        OutputStreamWriter outWriter = null;
        BufferedReader buffReader = null;
        BufferedWriter buffWriter = null;

        try {
            socket = new Socket("139.62.210.155", 1567);
            inReader = new InputStreamReader(socket.getInputStream());
            outWriter = new OutputStreamWriter(socket.getOutputStream());
            buffReader = new BufferedReader(inReader);
            buffWriter = new BufferedWriter(outWriter);
            Scanner scanner = new Scanner(System.in);
            int input = 0;
            int requests = 0;
            
            while (true) {
                System.out.println("Please enter the number corresponding to the desired command: ");
                System.out.println("1: Date and Time");
                System.out.println("2: Uptime");
                System.out.println("3: Memory Use");
                System.out.println("4: Netstat");
                System.out.println("5: Current Users");
                System.out.println("6: Running Processes");
                System.out.println("7: Exit Program");
    
                input = scanner.nextInt();
    
                if (input == 7) {
					buffWriter.write("END");
					buffWriter.newLine();
					buffWriter.flush();
					scanner.close();
					buffReader.close();
					buffWriter.close();
					socket.close();
					System.exit(0);
                }
    
                System.out.println("Please enter the number of requests");
                System.out.println("Options: 1, 5, 10, 15, 20, 25");
    
                requests = scanner.nextInt();

                System.out.println("Running request(s)...");

				for (int i = 0; i < requests; i++) {
					long startTime = System.nanoTime();
    
					switch(input) {
						case 1:
							// Date and Time
							buffWriter.write("Date and Time");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						case 2:
							// Uptime
							buffWriter.write("Uptime");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						case 3:
							// Memory Use
							buffWriter.write("Memory Use");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						case 4:
							// Netstat
							buffWriter.write("Netstat");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						case 5:
							// Current Users
							buffWriter.write("Current Users");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						case 6:
							// Running Processes
							buffWriter.write("Running Processes");
							buffWriter.newLine();
							buffWriter.flush();
							break;
						default:
							break;
                    }

					StringBuilder output = new StringBuilder();

					// Read first line from server
					String line = buffReader.readLine();

            		while (!line.equals("ENDSTREAM")) {
                		// Append line to output
						output.append(line).append("\n");
						// Read next line
						line = buffReader.readLine();
            		}

					System.out.print(output.toString());

					long endTime = System.nanoTime();
					long elapsedTime = (endTime - startTime)/1000000;
					System.out.print("Elapsed time: ");
					System.out.print(elapsedTime);
					System.out.println("ms.");
				}
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
