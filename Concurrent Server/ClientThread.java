import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
    
    private Socket socket;
    private String command;
    private int threadNum;
    private StringBuilder output = new StringBuilder();
    private long elapsedTime;
    private boolean finished = false;

    public ClientThread(Socket socket, String command, int threadNum) {
        this.socket = socket;
        this.command = command;
        this.threadNum = threadNum;
    }

    public void run() {
        try {
            // Instantiate buffered readers and writers from the socket streams
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            // Write command to server
            writer.write(this.command);
            writer.newLine();
            writer.flush();

            // Start the clock
            long startTime = System.nanoTime();
            
            // Read first line from server
            String line = reader.readLine();

            // Loop used for reading multiple lines of output from the server
            // Will stop once the "ENDSTREAM" flag has been read
            while (!line.equals("ENDSTREAM")) {
                // Append line to output
                this.output.append(line).append("\n");
                // Read next line
                line = reader.readLine();
            }

            long endTime = System.nanoTime();
            this.elapsedTime = (endTime - startTime)/1000000;

            // Print the command output and elapsed time to the user
            System.out.printf("Thread %d: ", this.threadNum);
            System.out.print(this.output.toString());
            System.out.printf("Time: %d ms\n", this.elapsedTime);

            // Disconnect this socket from the server by sending the END flag
            writer.write("END");
			writer.newLine();
			writer.flush();

            // Close everything up
            reader.close();
			writer.close();
			socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
