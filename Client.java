import java.net.*;

import msgs.*;

import java.io.*;

public class Client  {
    
    public static ALPMessage msg_out = new ALPMessage();
    public static ALPMessage msg_in = new ALPMessage();

    public static void main (String[] args) throws NumberFormatException, UnknownHostException, IOException,
            ClassNotFoundException {
        if(args.length != 2){ 
            System.err.println("Usage: java Client server_IP server_port");
            System.exit(1);
        }

        Boolean alive = true;
        
        Socket client_socket = new Socket(args[0], Integer.parseInt(args[1]));
        BufferedReader in_from_user = new BufferedReader(new InputStreamReader(System.in));
        ObjectOutputStream out_to_server = new ObjectOutputStream(client_socket.getOutputStream());
        out_to_server.flush();
        ObjectInputStream in_from_server = new ObjectInputStream(client_socket.getInputStream());
        

        while(alive){
            

            //LOGIN
            boolean logged_in = false;
            while(!logged_in){
                System.out.print(TerminalText.USNM_PROMPT.getText());
                msg_out.setCommand(Command.LOGIN_USERNAME);
                msg_out.setUser(in_from_user.readLine());
                send(out_to_server,msg_out);
                read(in_from_server,msg_in);
                if(msg_in.getCommand() == Command.NEED_PASSWORD || msg_in.getCommand() == Command.NEW_USER){
                    System.out.print(TerminalText.PSWD_PROMPT.getText());
                    msg_out.setCommand(Command.LOGIN_PASSWORD);
                    msg_out.setArgs(in_from_user.readLine(), 0);
                    send(out_to_server,msg_out);
                    read(in_from_server,msg_in);

                }
                if(msg_in.getCommand() == Command.LOGIN_COMPLETE){
                    System.out.println(TerminalText.WELCOME.getText());
                    logged_in = true;
                }
                if(msg_in.getCommand() == Command.LOGIN_FAIL){
                    System.out.println(TerminalText.PSWD_FAIL);
                }
            }




        }
        client_socket.close();

    }


    
    public static void send(ObjectOutputStream out,ALPMessage msg_out) throws IOException {
        out.writeObject(msg_out);
        out.flush();
    }


    public static void read(ObjectInputStream in, ALPMessage msg) throws ClassNotFoundException, IOException {
        ALPMessage temp =  (ALPMessage)in.readObject();
        msg.setArgs(temp.getArgs());
        msg.setUser(temp.getUser());
        msg.setCommand(temp.getCommand());
        msg.setPayload(temp.getPayload());
        

    }

    public static ALPMessage read(ObjectInputStream in) throws ClassNotFoundException, IOException {
        return (ALPMessage)in.readObject();
    }




}
