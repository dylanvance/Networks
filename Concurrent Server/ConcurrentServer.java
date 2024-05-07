import java.net.ServerSocket;
import java.net.Socket;

public class ConcurrentServer {
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

        // Try to establish the server on the given port
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Connect to a new client. Program will stall here until a new client connects
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Cut off a new thread for the socket
                new ServerThread(socket).start();
                
            }   // Loop back and wait for another client to connect
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
