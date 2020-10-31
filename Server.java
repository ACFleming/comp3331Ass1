import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;





public class Server extends Thread {

    private static Boolean shutdown = false;
    private static Hashtable<String,String> user_pass;
    private static List<Server> server_list;

    private boolean alive;
    private Socket socket;
    private BufferedReader in_from_client;
    private BufferedWriter out_to_client;

    public Server(Socket s) throws IOException {
        this.socket = s;
        in_from_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out_to_client = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
        // TODO Auto-generated method stub
        try {
            List<String> credentials = readInText("./credentials.txt");
            String[] retval;
            Iterator<String> it = credentials.iterator();
            while(it.hasNext()){
                retval = it.next().split(" ");
                user_pass.put(retval[0], retval[1]);
            }

            System.out.println(TerminalText.CLIENT_CNCT.getText());

            out_to_client.write("Test1");
            out_to_client.flush();
            out_to_client.write("Test2");
            out_to_client.newLine();    

            String username = "error";
            boolean logged_in = false;
            while(!logged_in){
                out_to_client.write(TerminalText.USNM_PROMPT.getText());
                username = in_from_client.readLine();
                if(user_pass.containsKey(username)){
                    System.out.println("VALID USERNAME");
                    out_to_client.write(TerminalText.PSWD_PROMPT.getText());
                    if(user_pass.get(username).equals(in_from_client.readLine())){
                        logged_in = true;
                    }else{
                        out_to_client.write(TerminalText.PSWD_FAIL.getText());
                        System.out.println(TerminalText.PSWD_FAIL.getText());
                    }
                }else{
                    System.out.println(TerminalText.NEW_USER.getText());
                    out_to_client.write(TerminalText.NEW_PSWD.getText(username));
                    credentials.add(username + " " + in_from_client.readLine());
                    writeOutString("./credentials.txt", credentials);
                    logged_in = true;

                }
            }                        
            System.out.println(TerminalText.LOGIN_SUCC.getText(username));
            out_to_client.write(TerminalText.WELCOME.getText());
            in_from_client.readLine();


            String cmd = "";
            while(!cmd.equals("quit")){
                out_to_client.write(TerminalText.CMD_PROMPT.getText());
                cmd = in_from_client.readLine();

            }
            out_to_client.write("DONE");
            System.out.println("DONE");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
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


}