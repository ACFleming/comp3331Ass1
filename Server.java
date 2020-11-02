import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import msgs.*;





public class Server extends Thread {

    private static Boolean shutdown = false;
    private static Hashtable<String,String> user_pass;
    private static List<Server> server_list;

    private boolean alive;
    private Socket socket;
    private BufferedReader in_from_client;
    private DataOutputStream out_to_client;
    private ALPMessage msg_out = new ALPMessage();
    private ALPMessage msg_in = new ALPMessage();


    public Server(Socket s) throws IOException {
        this.socket = s;
        out_to_client = new DataOutputStream(socket.getOutputStream());
        out_to_client.flush();
        in_from_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        if(args.length != 2){
            System.err.println("Usage: java Server server_port admin_passwd" );
            System.exit(1);
        }
        user_pass = new Hashtable<String,String>();
        server_list = new ArrayList<Server>();
        ServerSocket sock = new ServerSocket(Integer.parseInt(args[0]));
        while(!shutdown){
            System.out.println(TerminalText.SRVR_WAIT.getText());
            Socket connected = sock.accept();
            Server svr = new Server(connected);
            server_list.add(svr);
            svr.start();
        }
        for(Server s: server_list){
            s.interrupt();
        }
        
    }

    @Override
    public void run() {

        try {
            List<String> credentials = readInText("./credentials.txt");
            String[] retval;
            Iterator<String> it = credentials.iterator();
            while(it.hasNext()){
                retval = it.next().split(" ");
                user_pass.put(retval[0], retval[1]);
            }

            System.out.println(TerminalText.CLIENT_CNCT.getText());


            
            
            boolean logged_in = false;
            while(!logged_in){
                // read(in_from_client,msg_in);
                read(in_from_client,msg_in);
                // System.out.println("MSGIN:" +msg_in);
                if(user_pass.containsKey(msg_in.getUser())){
                    System.out.println("Need Password");
                    System.out.println(msg_in.getUser());
                    msg_out.setCommand("NEED_PASSWORD");
                    send(out_to_client,msg_out);                    
                    read(in_from_client,msg_in);
                    // read(in_from_client,msg_in);

                    // System.out.println("Username is: " + msg_in.getUser());

                    if(user_pass.get(msg_in.getUser()).equals(msg_in.getArgs(0))){
                        System.out.println("Correct Password");
                        logged_in = true;
                        msg_out.setCommand("LOGIN_COMPLETE");
                        send(out_to_client,msg_out);


                    }else{
                        System.out.println("Wrong Password");
                        msg_out.setCommand("LOGIN_FAil");
                        send(out_to_client,msg_out);
                    }
                }else{
                    System.out.println("New User");
                    msg_out.setCommand("NEW_USER");
                    send(out_to_client,msg_out);
                }
            }
            System.out.println("LOGGED IN");
            // String username = "error";
            // boolean logged_in = false;
            // while(!logged_in){
                
            //     out_to_client.write(TerminalText.USNM_PROMPT.getText());
            //     username = in_from_client.readLine();
            //     if(user_pass.containsKey(username)){
            //         System.out.println("VALID USERNAME");
            //         out_to_client.write(TerminalText.PSWD_PROMPT.getText());
            //         if(user_pass.get(username).equals(in_from_client.readLine())){
            //             logged_in = true;
            //         }else{
            //             out_to_client.write(TerminalText.PSWD_FAIL.getText());
            //             System.out.println(TerminalText.PSWD_FAIL.getText());
            //         }
            //     }else{
            //         System.out.println(TerminalText.NEW_USER.getText());
            //         out_to_client.write(TerminalText.NEW_PSWD.getText(username));
            //         credentials.add(username + " " + in_from_client.readLine());
            //         writeOutString("./credentials.txt", credentials);
            //         logged_in = true;

            //     }
            // }                        
            // System.out.println(TerminalText.LOGIN_SUCC.getText(username));
            // out_to_client.write(TerminalText.WELCOME.getText());
            // in_from_client.readLine();


            // String cmd = "";
            // while(!cmd.equals("quit")){
            //     out_to_client.write(TerminalText.CMD_PROMPT.getText());
            //     cmd = in_from_client.readLine();

            // }
            // out_to_client.write("DONE");
            // System.out.println("DONE");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }






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

    private static void writeOutString(String pathname, List<String> text) throws IOException {
        File file = new File(pathname);
        FileWriter fw = new FileWriter(file);
    
        Iterator<String> it = text.iterator();
        while(it.hasNext()){
            fw.write(it.next() + "\n");
        }
        fw.flush();
        fw.close();
        
    }


    public static void send(DataOutputStream out,ALPMessage msg) throws IOException {
        System.out.println("MSGOUT:" +msg);
        
        out.writeBytes(msg.toString() + "\n");
        out.flush();
    }


    public static void read(BufferedReader in, ALPMessage msg) throws ClassNotFoundException, IOException {
        String temp = in.readLine();
        msg.fromString(temp);
        System.out.println("MSGIN:" +msg);
        

    }
}