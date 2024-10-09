import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    static final int PORT = 5006;
    public static void main(String[] args) {
        ServerSocket server = null;
        try{
            server = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while(true){
                Socket client = server.accept();
                System.out.println("Client connected");

                //creating a new thread for the client
                Thread serverInstance = new ServerInstance(client);
                serverInstance.start();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
            try{
                if(server != null){
                    server.close();
                }
            }
            catch(IOException e){
                System.err.println("Exception when closing server socket : " + e.getMessage());
            }            
        }
    }
}