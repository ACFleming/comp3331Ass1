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
        DataOutputStream out_to_server = new DataOutputStream(client_socket.getOutputStream());
        out_to_server.flush();
        BufferedReader in_from_server = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
        

        while(alive){
            

            //LOGIN
            boolean logged_in = false;
            while(!logged_in){
                System.out.print(TerminalText.USNM_PROMPT.getText());
                msg_out.setCommand(Command.LOGIN_USERNAME);
                msg_out.setUser(in_from_user.readLine());
                if(msg_out.getUser().isBlank()){
                    System.out.println(TerminalText.INV_CHAR.getText());
                    continue;
                }

                send(out_to_server,msg_out);
                read(in_from_server,msg_in);
                if(msg_in.getCommand().equals(Command.NEED_PASSWORD.toString()) || msg_in.getCommand().equals(Command.NEW_USER.toString())){
                    
                    String pass;
                    do {
                        System.out.print(TerminalText.PSWD_PROMPT.getText());
                        
                        pass = in_from_user.readLine();
                        if(!pass.isBlank() ||  !msg_in.getCommand().equals(Command.NEW_USER.toString())){
                            break;
                        }
                        System.out.println(TerminalText.INV_CHAR.getText());
                    } while (true);
                    
                    msg_out.setCommand(Command.LOGIN_PASSWORD);
                    msg_out.setArgs(pass, 0);
                    
                    send(out_to_server,msg_out);
                    read(in_from_server,msg_in);

                }
                if(msg_in.getCommand().equals(Command.LOGIN_COMPLETE.toString())){
                    System.out.println(TerminalText.WELCOME.getText());
                    logged_in = true;
                    break;
                }else if(msg_in.getCommand().equals(Command.LOGIN_FAIL.toString())){
                    System.out.println(TerminalText.PSWD_FAIL);
                }else if (msg_in.getCommand().equals(Command.ERROR.toString())){
                    System.out.println(msg_in.getArgs(0));
                }
            }
            while(logged_in){
                System.out.println(TerminalText.CMD_PROMPT.getText());
                String[] cmd_args = in_from_user.readLine().split(" ");
                System.out.print(cmd_args[0]);
                if(cmd_args[0].equals(Command.CRT.toString())){
                    msg_out.setCommand(Command.CRT);
                    msg_out.setArgs(cmd_args[1], 0);
                    send(out_to_server,msg_out);
                }else{
                    System.out.println(TerminalText.INV_CMD.getText());
                }
                read(in_from_server,msg_in);
                if(msg_in.getCommand().equals(Command.ERROR.toString())){
                    System.out.println(msg_in.getArgs(0));
                }
            }
            
            alive = false;



        }
        client_socket.close();

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
