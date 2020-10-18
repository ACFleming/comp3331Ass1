import java.io.*;
import java.net.*;

import msgs.*;

public class Server implements Runnable {

    private static Boolean alive = true;

    public static void main(String[] args) throws NumberFormatException, IOException {
        if(args.length != 2){
            System.err.println("Usage: java Server server_port admin_passwd" );
            System.exit(1);
        }
        

        ServerSocket loginSocket = new ServerSocket(Integer.parseInt(args[0]));
        
        while(alive){
            Socket connected = loginSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connected.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connected.getOutputStream());






        }




    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
    
}