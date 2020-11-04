import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import msgs.*;





public class Server extends Thread {

    private static Boolean shutdown = false;
    private static Hashtable<String,String> user_pass;
    private static List<Server> server_list;

    public static String cred_path_name = "./credentials.txt";

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
            List<String> credentials = readInText(cred_path_name);
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
                    msg_out.setCommand(Command.NEED_PASSWORD);
                    send(out_to_client,msg_out);                    
                    read(in_from_client,msg_in);
                    // read(in_from_client,msg_in);
                    // System.out.println("Username is: " + msg_in.getUser());
                    if(user_pass.get(msg_in.getUser()).equals(msg_in.getArgs(0))){
                        System.out.println("Correct Password");
                        logged_in = true;
                        msg_out.setCommand(Command.LOGIN_COMPLETE);
                        send(out_to_client,msg_out);


                    }else{
                        System.out.println("Wrong Password");
                        msg_out.setCommand(Command.LOGIN_FAIL);
                        send(out_to_client,msg_out);
                    }
                }else{
                    System.out.println("New User");
                    msg_out.setCommand(Command.NEW_USER);
                    send(out_to_client,msg_out);
                    read(in_from_client,msg_in);
                    credentials.add(msg_in.getUser()+ " " + msg_in.getArgs(0));
                    user_pass.put(msg_in.getUser(), msg_in.getArgs(0));
                    writeOutString(cred_path_name, credentials);
                    logged_in = true;
                    msg_out.setCommand(Command.LOGIN_COMPLETE);
                    send(out_to_client,msg_out);


                }
            }
            String pathname;
            System.out.println("LOGGED IN");
            while(logged_in){
                read(in_from_client,msg_in);
                System.out.println("GOT MESSAGE");
                if(msg_in.getCommand().equals(Command.CRT.toString())){
                    pathname = "./" + msg_in.getArgs(0) + ".txt";
                    File new_file = new File(pathname);
                    if(new_file.createNewFile()){
                        System.out.println("New file at: " + pathname);
                        List<String> text = new ArrayList<String>();
                        text.add("NEWFILE");
                        writeOutString(pathname, text);
                        msg_out.setCommand(Command.SUCCESS);
                        send(out_to_client,msg_out);
                    }else{
                        System.out.println("Existing file at: " + pathname);
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.FILE_EXIST.getText(pathname), 0);
                        send(out_to_client,msg_out);

                    }
                }
            }


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
            String line = sc.nextLine();
            if(!line.isBlank()){
                text.add(line);
            }
            
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