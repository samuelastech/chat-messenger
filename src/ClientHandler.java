import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    // Group chat
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    // Establish a connection: Client -> Server
    private Socket socket;

    // Read and write messages
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private String clientUserName;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this); // An instance of this class
            broadcastMessage("SERVER: " + clientUserName + " has entered the chat!");
        }catch(IOException error){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run(){
        String messageFromClient;

        while(socket.isConnected()){
            try{
                // Each thread will hook here
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch(IOException error){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * Broadcasts the message to every single client connected
     * @param message the message to send
     */
    public void broadcastMessage(String message){
        for(ClientHandler clientHandler: clientHandlers){
            try{
                if(!clientHandler.clientUserName.equals(clientUserName)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(IOException error){
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Signal that the user has left the chat
     */
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUserName + "has left the chat!");
    }

    /**
     * Closes down our connection and streams -- the user is leaving
     * @param socket connection to the server and client
     * @param bufferedReader output
     * @param bufferedWriter input
     */
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        removeClientHandler();

        try{
           if(bufferedReader != null){
               bufferedReader.close();
           }

            if(bufferedWriter != null){
                bufferedWriter.close();
            }

            if(socket != null){
                socket.close();
            }
        }catch(IOException error){
            error.printStackTrace();
        }
    }
}
