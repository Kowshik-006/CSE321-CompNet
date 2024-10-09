import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientInstance extends Thread {
    File file;
    public ClientInstance(String fileName){
        file = new File(".",fileName);
    }

    @Override
    public void run(){
        try{
            Socket clientSocket = new Socket("localhost", 5006);
            OutputStream out = clientSocket.getOutputStream();
            FileInputStream fileIn = new FileInputStream(file);
            String command = "UPLOAD " + file.toPath() + "\n";
            String[] acceptedTypes = {"txt", "jpg", "jpeg", "png", "mp4"};
            boolean accepted = false;
            for(String type : acceptedTypes){
                if(file.getName().endsWith(type)){
                    accepted = true;
                    break;
                }
            }
            out.write(command.getBytes());
            if(!accepted){
                System.out.println("File type not supported: " + file.getName());
                out.flush();
                fileIn.close();
                clientSocket.close();
                return;
            }
            System.out.println("Uploading file: " + file.toPath());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileIn.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("File uploaded: " + file.getName());
            out.flush();
            fileIn.close();
            clientSocket.close();
        }
        catch(IOException e){
            System.out.println("Inside ClientInstance.java");
            System.err.println("Error while uploading file: " + file.getName() + " " + e.getMessage());
        }
    }
}
