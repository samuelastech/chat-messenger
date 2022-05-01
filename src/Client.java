import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        }catch(IOException error){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Sends messages to our ClientHandler
     */
    public void sendMessage(){
        try{
           bufferedWriter.write(username);
           bufferedWriter.newLine();
           bufferedWriter.flush();

           Scanner scanner = new Scanner(System.in);
           while(socket.isConnected()){
               String message = scanner.nextLine();
               bufferedWriter.write(username + ": " + message);
               bufferedWriter.newLine();
               bufferedWriter.flush();
           }
        }catch(IOException error){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Listen to messages from the server
     */
    public void listenForMessage(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                String messageFromGroupChat;
                try{
                    while(socket.isConnected()){
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }
                }catch(IOException error){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name for the secretary group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 3000);
        Client client = new Client(socket, username);

        // Separated thread. They'll be able to run at the same time
        client.listenForMessage();
        client.sendMessage();
    }
}
