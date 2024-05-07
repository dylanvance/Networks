import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerThread extends Thread {

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            // Instantiate buffered readers and writers from the socket streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Variables used in reading and writing input and output
            String request = "";
            String output = "";

            // Flag used when client disconnects to break loop
            boolean disconnected = false;

            // Loop that runs until client disconnects
            while (!disconnected) {
                // Read input from client. Program will stall here until it receives data
                request = reader.readLine();

                // Check if disconnect message is sent
                if (request.equals("END")) {
                    // Set disconnected flag to true
                    disconnected = true;
                }

                // Only run if client is not disconnected
                if (!disconnected) {
                    // Send the request off to performOperation method and retrieve the output
                    output = performOperation(request);

                    // Write output back to client
                    writer.write(output);
                    writer.newLine();
                    writer.flush();

                    // Send message to tell client that data is done being sent
                    writer.write("ENDSTREAM");
                    writer.newLine();
                    writer.flush();
                }
            }
            System.out.println("Client disconnected");
            // Close everything after client disconnects
            reader.close();
            writer.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static String performOperation(String request) {
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
