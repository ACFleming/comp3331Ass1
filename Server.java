import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import msgs.*;

public class Server implements Runnable {

    private static Boolean alive = true;
    private static Hashtable<String,String> user_pass;

    public static void main(String[] args) throws NumberFormatException, IOException {
        if(args.length != 2){
            System.err.println("Usage: java Server server_port admin_passwd" );
            System.exit(1);
        }
        user_pass = new Hashtable<String,String>();
        

        ServerSocket login_socket = new ServerSocket(Integer.parseInt(args[0]));



        //Load Username and Passwords into Hashtable
        List<String> credentials = readInText("./credentials.txt");
        String[] retval;
        Iterator<String> it = credentials.iterator();
        while(it.hasNext()){
            retval = it.next().split(" ");
            user_pass.put(retval[0], retval[1]);
        }
        BufferedReader in_from_client;
        DataOutputStream out_to_client;


        
        while(alive){
            System.out.println(TerminalText.SRVR_WAIT.getText());
            Socket connected = login_socket.accept();
            System.out.println(TerminalText.CLIENT_CNCT.getText());
            in_from_client = new BufferedReader(new InputStreamReader(connected.getInputStream()));
            out_to_client = new DataOutputStream(connected.getOutputStream());
            

            //Login
            out_to_client.writeBytes(TerminalText.USNM_PROMPT.getText()+"\n");
            String username = in_from_client.readLine();
            if(user_pass.containsKey(username)){
                out_to_client.writeBytes(TerminalText.PSWD_PROMPT.getText()+"\n");
                if(user_pass.get(username).equals(in_from_client.readLine())){
                    out_to_client.writeBytes(TerminalText.WELCOME.getText()+"\n");
                    System.out.println(TerminalText.LOGIN_SUCC.getText(username));
                }
            }else{
                out_to_client.writeBytes(TerminalText.NEW_USER.getText()+"\n");
            }
            





        }




    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
    
// from https://techblogstation.com/java/read-text-file-in-java/

    private static List<String> readInText(String pathname) throws FileNotFoundException{
        File file = new File(pathname);
        Scanner sc = new Scanner(file);
        List<String> text = new ArrayList<String>();
        while(sc.hasNextLine()){
            text.add(sc.nextLine());
        }
        sc.close();
        return text;
    }

    


}