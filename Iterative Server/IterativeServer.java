import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IterativeServer {
    public static void main(String[] args) {
        int port = 1567; //temp port number
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for a client to connect
		System.out.println("Client connected.");
                handleClientRequest(clientSocket); // Handle the client request
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

    }

    private static void handleClientRequest(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        String request = "";
        String output = "";
        boolean disconnected = false;

        while (clientSocket.isConnected() && !disconnected) {	        
            request = reader.readLine(); // Read input from the client

            if (request.equals("END")) {
                disconnected = true;	// Client is ending communication
                System.out.println("Disconnecting.");
            }

            if (!disconnected) {

                System.out.println("Client: " + request);

                // Perform and store operation
                output = performOperation(request);

                // Send the output back to the client
                writer.write(output);
                writer.newLine();
                writer.flush();

                // Tells client that the last line of data has been read
                writer.write("ENDSTREAM");
                writer.newLine();
                writer.flush();

                System.out.println("Operation finished.");
            }
        }
	
        System.out.println("Client disconnected.");
        reader.close();
        writer.close();
        clientSocket.close();
    }

    private static String performOperation(String request) {
	    System.out.println("Performing operation.");
        switch (request) {
                case "Date and Time":
                    return getCurrentDateTime();
                case "Uptime":
                    return getUptime();
                case "Memory Use":
                    return getMemoryUsage();
                case "Netstat":
                    return getNetstat();
                case "Current Users":
                    return getCurrentUsers();
                case "Running Processes":
                    return getRunningProcesses();
                default:
                    return "Invalid request";
            }
        }

    private static String getCurrentDateTime() {
	    LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Current Date and Time: " + now.format(formatter);
    }

    private static String getUptime() {
        try {
            Process uptimeProcess = Runtime.getRuntime().exec("uptime");
            BufferedReader reader = new BufferedReader(new InputStreamReader(uptimeProcess.getInputStream()));
            String output = reader.readLine();
            reader.close();
            return "Uptime: " + output;
        } catch (IOException e) {
            return "Error getting uptime";
        }
    }

    private static String getMemoryUsage() {
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        return String.format("Memory Usage: %d MB used, %d MB free", bytesToMB(usedMemory), bytesToMB(freeMemory));
    }

    private static String getNetstat() {
        try {
            Process netstatProcess = Runtime.getRuntime().exec("netstat");
            BufferedReader reader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
            return "Netstat:\n" + output.toString();
        } catch (IOException e) {
            return "Error getting netstat";
        }
    }

    private static String getCurrentUsers() {
        try {
            Process whoProcess = Runtime.getRuntime().exec("who");
            BufferedReader reader = new BufferedReader(new InputStreamReader(whoProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
            return "Current Users:\n" + output.toString();
        } catch (IOException e) {
            return "Error getting current users";
        }
    }

    private static String getRunningProcesses() {
        try {
            Process psProcess = Runtime.getRuntime().exec("ps aux");
            BufferedReader reader = new BufferedReader(new InputStreamReader(psProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
            return "Running Processes:\n" + output.toString();
        } catch (IOException e) {
            return "Error getting running processes";
        }
    }

    private static long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
