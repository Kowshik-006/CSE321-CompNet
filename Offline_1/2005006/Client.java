import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>();
        try (Scanner input = new Scanner(System.in)) {
            while(true){
                files.clear();
                System.out.println("Enter the files you want to upload separated by a space : ");
                while(true){
                    String fileName = input.next();
                    if(fileName.equals("/")){
                        break;
                    }
                    files.add(fileName);
                }
                for(String filename : files){
                    Thread clientInstance = new ClientInstance(filename);
                    clientInstance.start();
                }
            }
        }
    }
}
