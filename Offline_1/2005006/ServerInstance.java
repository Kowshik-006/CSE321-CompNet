import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader; 
import java.net.Socket;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ServerInstance extends Thread {
    Socket client;
    static final String ROOT = ".";

    public ServerInstance(Socket client){
        this.client = client;
    }

    //overriding the run() method of the Thread class 
    public void run(){
        try{
            BufferedReader in =new BufferedReader(new InputStreamReader(client.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(client.getOutputStream());
            //reading the request from the client
            String request = in.readLine();
            log(request, true);
            if((request != null) && (request.startsWith("GET"))){
                handleGetRequest(request, out);
            }
            else if((request != null) && (request.startsWith("UPLOAD"))){
                // System.out.println("Inside UPLOAD condition checking");
                handleUploadRequest(request, client.getInputStream(),out);
            }
            else{
                sendErrorResponse(out, 400, "Bad Request");
            }

            out.flush();
            client.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void handleGetRequest(String request, BufferedOutputStream out){
        try{
            //extracting the parts of the request
            System.out.println(request);
            String[] parts = request.split(" ");
            if(parts.length != 3){
                sendErrorResponse(out, 400, "Bad Request");
                // System.err.println("Invalid request : " + request);
                return;
            }
            //Path is the second part of the request
            String path = parts[1].trim();
            File file = new File(".", path.substring(1)); // remove the leading '/'
            if (file.exists() && file.isDirectory()) {
                sendDirectoryListing(out, file, path);
            } 
            else if (file.exists() && file.isFile()) {
                sendFile(out, file);
            } 
            else {
                sendErrorResponse(out, 404, "Not Found");
            }

            

        } catch(IOException e){
            System.err.println("Error while processing the request : " + e.getMessage());
        }
    }

    private void sendDirectoryListing(BufferedOutputStream out, File file, String requestPath){
        try{
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.0 200 OK\r\n");
            response.append("Content-Type: text/html\r\n");
            response.append("\r\n");
            response.append("<html><body><h1>Directory listing</h1><hr>");
            response.append("<ul>");
            for (File f : file.listFiles()) {
                String path = requestPath + ((requestPath.endsWith("/"))?"":"/") + f.getName();
                // System.out.println(path);
                if(f.isDirectory()){
                    response.append("<li><b><i><a href = \""+path+"\">"+ f.getName() + "</i></b></li>");
                }
                else{
                    response.append("<li><a href = \"" + path + "\">"+ f.getName() + "</li>");
                }
            }
            response.append("</ul>");
            response.append("<hr></body></html>");
            out.write(response.toString().getBytes());
            log("Sent Directory List", false);
        } catch(IOException e){
            System.err.println("Error while sending the directory listing : " + e.getMessage());
        }
    }

    private void sendFile(BufferedOutputStream out, File file){
        try{
            String[] acceptedTypes = {"txt", "jpg", "jpeg","png"};
            boolean accepted = false;
            for(String type : acceptedTypes){
                if(file.getName().endsWith(type)){
                    accepted = true;
                    break;
                }
            }
            if(!accepted){
                downloadFile(out, file);
                return;
            }
            // StringBuilder response = new StringBuilder();
            // response.append("HTTP/1.0 200 OK\r\n");
            // System.out.println(file.toPath());
            // System.out.println("Type: " +Files.probeContentType(file.toPath()));
            // response.append("Content-type: "+Files.probeContentType(file.toPath())+"\r\n\r\n");
            // out.write(response.toString().getBytes());

            // out.flush();

            // FileInputStream fileIn = new FileInputStream(file);
            // OutputStream output = client.getOutputStream();
            // // chunk of 1024 bytes
            // byte[] buffer = new byte[1024];
            // int bytesRead;
            // // -1 indicates end of file
            // while((bytesRead = fileIn.read(buffer)) != -1){
            //     output.write(buffer, 0, bytesRead);
            // }
            // output.flush();
            // fileIn.close();

            // To show in separate HTML file
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.0 200 OK\r\n");
            response.append("Content-Type: text/html\r\n\r\n");

            String fileType = Files.probeContentType(file.toPath());
            response.append("<html><head><title>File Preview</title></head><body>");

            if (fileType.startsWith("text")) {
                response.append("<h1>Text File: " + file.getName() + "</h1><pre>");
                response.append(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
                response.append("</pre>");
            }
            
            else if (fileType.startsWith("image")) {
                response.append("<h1>Image File: " + file.getName() + "</h1>");
                response.append("<img src=\"data:" + fileType + ";base64," + encodeFileToBase64(file) + "\" alt=\"" + file.getName() + "\" />");
            }

            response.append("</body></html>");

            // Send the HTML response
            out.write(response.toString().getBytes());
            log("Sent file : "+file.getName(), false);
            out.flush();
        }
        catch(IOException e){
            System.err.println("Error while sending the file : " + e.getMessage());
        }
    }

    private void downloadFile(BufferedOutputStream out, File file) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.0 200 OK\r\n");
        response.append("Content-Type: application/octet-stream\r\n");
        response.append("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n\r\n");
        out.write(response.toString().getBytes());
        log("Sent File : " +file.getName(), false);
        out.flush();

        OutputStream output = client.getOutputStream();
        FileInputStream fileInput = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fileInput.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        fileInput.close();
    }

    private void handleUploadRequest(String request, InputStream in, BufferedOutputStream out) throws IOException {
        System.out.println("Inside handleUploadRequest");
        String[] parts = request.split(" ");
        if (parts.length < 2) {
            sendErrorResponse(out, 400, "Bad Request");
            return;
        }

        // Extract filename from the request
        String fileName = parts[1].trim();
        String[] acceptedTypes = {"txt", "jpg", "jpeg", "png", "mp4"};
        boolean accepted = false;
        for (String type : acceptedTypes) {
            if (fileName.endsWith(type)) {
                accepted = true;
                break;
            }
        }
        if (!accepted) {
            System.out.println("File type not supported: " + fileName);
            return;
        }
        System.out.println("Filename : " + fileName);
        File file = new File("./upload" , fileName.substring(1)); // remove the leading '/'
        System.out.println("Uploading file : " + file.toPath());

        // Prepare to receive file data
        try {
            byte[] buffer = new byte[1024];
            int bytesRead;
            // To write in the file
            // BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(file));
            FileOutputStream fileOutput = new FileOutputStream(file);
            // Read the file data in chunks and write to the file
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
            }
            fileOutput.flush();
            System.out.println("File uploaded successfully: " + file.getName());
            // Send success response to the client
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.0 200 OK\r\n");
            System.out.println("Type: " +Files.probeContentType(file.toPath()));
            response.append("Content-Type: "+Files.probeContentType(file.toPath())+"\r\n\r\n");
            out.write(response.toString().getBytes());
            log("Uploaded file : " + fileName, false);
            out.close();
        }
        catch(IOException e){
            System.err.println("Error while uploading the file : " + e.getMessage());
        }

    }

    private String encodeFileToBase64(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private void sendErrorResponse(BufferedOutputStream out, int statusCode, String message) throws IOException {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.0 " + statusCode + " " + message + "\r\n\r\n");
        response.append("<html><body><h1>" + statusCode + " " + message + "</h1></body></html>");
        out.write(response.toString().getBytes());
        log("Error " + statusCode , false);
    }

    public synchronized void log(String message, boolean isRequest){
        try(FileOutputStream logFile = new FileOutputStream("logMessages.log",true)){
            if(isRequest){
                message = "Request : " + message + "\n";
            }
            else{
                message = "Response : " + message + "\n";
            }
            logFile.write(message.getBytes());
        }
        catch(IOException e){
            System.err.println("Error while writing to log file : " + e.getMessage());
        }
    }

}
