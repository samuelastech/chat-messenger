import java.io.IOException;
import java.net.ServerSocket;

public class App{
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3000);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
