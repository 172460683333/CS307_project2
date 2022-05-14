package SUSTech.CS307_project2.Socket;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

@RestController
public class Server {
    private static ServerSocket serverSocket = null;
    private static int portNumber = 8888;

    static {
        try {
            start_server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void start_server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
        System.out.println("Server is OK, is waiting for connect...");
        PrintWriter out = null;
        BufferedReader in = null;
        Socket clientSocket = null;
        try {
            while (true) {
                clientSocket = serverSocket.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                // Wait for input
                if ((inputLine = in.readLine()) != null) {
                    String command = inputLine;
                    String response = "";
                    response = inputLine;
                    if (response.equals("") || response == null) {
                        out.println("good bye");
                        break;
                    }
                    out.println(response);
                }
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        }
    }

    @RequestMapping("http://localhost:8080/test")
    public String test(){
        return "test";
    }

}