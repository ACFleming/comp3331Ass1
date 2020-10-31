import java.net.*;



import java.io.*;

public class Client  {
    
    public static byte[] input = new byte[512];
    public static byte[] output = new byte[512];

    public static void main (String[] args) throws NumberFormatException, UnknownHostException, IOException{
        if(args.length != 2){ 
            System.err.println("Usage: java Client server_IP server_port");
            System.exit(1);
        }

        Boolean alive = true;
        
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
        BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));
        DataInputStream in_from_server = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out_to_server = new DataOutputStream(clientSocket.getOutputStream());

        while(alive){
            
            
            System.out.println("Start");
            
            
            System.out.print(input);
            
            in_from_user.
            write(in_from_user.readLine().getBytes());


            System.out.println("End");


        }

        clientSocket.close();

    }







}
