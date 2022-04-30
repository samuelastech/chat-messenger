import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    private ServerSocket serverSocket;

    /**
     * Receive the income communication
     * @param serverSocket socket object to communicate
     */
    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    /**
     * Keeps the server running until the socket is closed
     */
    public void startServer(){
        try{
            // Isn't closed
            // Wait a client to connect
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new inspector has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch(IOException error){
            closeServerSocket();
        }
    }

    /**
     * Closes the server if an error occurs
     */
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch(IOException error){
            error.printStackTrace();
        }
    }
}
