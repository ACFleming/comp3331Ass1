import java.net.*;
import java.nio.CharBuffer;
import java.io.*;

public class Client  {
    
    public static String input = new String();
    public static String output = new String();

    public static void main (String[] args) throws NumberFormatException, UnknownHostException, IOException{
        if(args.length != 2){ 
            System.err.println("Usage: java Client server_IP server_port");
            System.exit(1);
        }

        Boolean alive = true;
        
        Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
        BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in_from_server = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        BufferedWriter out_to_server = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        while(alive){
            
            
            System.out.println("Start");
            
            
            
            System.out.print(in_from_server.read());
            if(in_from_server.ready() ){
                CharBuffer.allocate(512);
                in_from_server.read(target);
                input = target.flip().toString();
                System.out.print("#" + input);
            }

            
            
            
            out_to_server.write(in_from_user.readLine());
            out_to_server.newLine();


            System.out.println("End");


        }

        clientSocket.close();

    }







}
