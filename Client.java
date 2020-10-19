import java.net.*;



import java.io.*;

public class Client  {
    

    public static void main (String[] args) throws NumberFormatException, UnknownHostException, IOException{
        if(args.length != 2){ 
            System.err.println("Usage: java Client server_IP server_port");
            System.exit(1);
        }

        Boolean alive = true;
        
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));

        while(alive){
            
            
            BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader in_from_server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream out_to_server = new DataOutputStream(clientSocket.getOutputStream());
            
            System.out.print("#" +in_from_server.readLine());
            
            
            out_to_server.writeBytes(in_from_user.readLine()+"\n");



        }

        clientSocket.close();

    }







}
