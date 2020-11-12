import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import msgs.*;
import thread.ThreadFile;

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

        

        ObjectOutputStream out_to_server_object = new ObjectOutputStream(client_socket.getOutputStream());
        out_to_server_object.flush();
        System.out.println("HERE");
        client_socket.getOutputStream().flush();
        ObjectInputStream in_from_server_object = new ObjectInputStream(client_socket.getInputStream());
        System.out.println("HERE");


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

                ALPMessage.sendObject(out_to_server_object, msg_out);
                ALPMessage.readObject(in_from_server_object, msg_in);
                //ALPMessage.sendObject(out_to_server_object, msg_out);
                // ALPMessage.readObject(in_from_server_object, msg_in);
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
                    
                    ALPMessage.sendObject(out_to_server_object, msg_out);
                    ALPMessage.readObject(in_from_server_object, msg_in);
                    if(msg_in.getCommand().equals(Command.LOGIN_COMPLETE.toString())){
                        System.out.println(TerminalText.WELCOME.getText());
                        logged_in = true;
                        break;
                    }else if(msg_in.getCommand().equals(Command.LOGIN_FAIL.toString())){
                        System.out.println(TerminalText.PSWD_FAIL);
                    }

                }else if (msg_in.getCommand().equals(Command.ERROR.toString())){
                    System.out.println(msg_in.getArgs(0));
                }
            }
            String message = "";
            Boolean waiting = false;
            while(logged_in){
                System.out.println(TerminalText.CMD_PROMPT.getText());
                List<String> cmd_input =  Arrays.asList(in_from_user.readLine().split(" "));

                //Commands

                //CRT threadname
                String command = cmd_input.get(0);
                if(command.equals(Command.CRT.toString())){
                    if(cmd_input.size() == 2){
                        msg_out.setCommand(Command.CRT);
                        msg_out.setArgs(cmd_input.get(1), 0);
                        
                        ALPMessage.sendObject(out_to_server_object, msg_out);
                        waiting = true;
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText());
                    }
        
                // RMV threadname
                }else if(command.equals(Command.RMV.toString())){
                    if(cmd_input.size() == 2){

                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.RMV);
                            msg_out.setArgs(cmd_input.get(1), 0);
                            
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }

                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText());
                    }
                
                
                // MSG threadname message
                }else if(command.equals(Command.MSG.toString())){
                    if(cmd_input.size() >= 2){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.MSG);
                            msg_out.setArgs(cmd_input.get(1),0);
                            msg_out.setArgs(String.join(" ", cmd_input.subList(2, cmd_input.size())),1);
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }
                // LST
                }else if(command.equals(Command.LST.toString())){

                    if(cmd_input.size() == 1){
                        msg_out.setCommand(Command.LST);
                        ALPMessage.sendObject(out_to_server_object, msg_out);
                        waiting = true;
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }


                // EDT threadtitle messagenumber message
                }else if(command.equals(Command.EDT.toString())){
                    if(cmd_input.size() >= 3){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.EDT);
                            msg_out.setArgs(cmd_input.get(1),0);
                            msg_out.setArgs(cmd_input.get(2),1);
                            msg_out.setArgs(String.join(" ", cmd_input.subList(3, cmd_input.size())),2);
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }
                
                // DLT threadtitle messagenumber
                }else if(command.equals(Command.DLT.toString())){
                    if(cmd_input.size() == 3){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.DLT);
                            msg_out.setArgs(cmd_input.get(1),0);
                            msg_out.setArgs(cmd_input.get(2),1);
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }
                    


                // RDT threadtitle
                }else if(command.equals(Command.RDT.toString())){
                    if(cmd_input.size() == 2){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.RDT);
                            msg_out.setArgs(cmd_input.get(1),0);
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }

                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }


                // UPD threadtitle filename
                }else if(command.equals(Command.UPD.toString())){
                    if(cmd_input.size() == 3){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.UPD);
                            msg_out.setArgs(cmd_input.get(1),0);
                            msg_out.setArgs(cmd_input.get(2),1);
                            msg_out.setPayload(Files.readAllBytes(Paths.get(cmd_input.get(2))));
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }
                
                
                // DWN threadtitle filename
                }else if(command.equals(Command.DWN.toString())){
                    if(cmd_input.size() == 3){
                        if(cmd_input.get(1).equals("credentials")){
                            System.out.println(TerminalText.BAD_THREADNAME.getText());
                        }else{
                            msg_out.setCommand(Command.DWN);
                            msg_out.setArgs(cmd_input.get(1),0);
                            msg_out.setArgs(cmd_input.get(2),1);
                            ALPMessage.sendObject(out_to_server_object, msg_out);
                            waiting = true;
                        }
                    }else{
                        System.out.println(TerminalText.BAD_SYNTAX.getText(cmd_input.get(0)));
                    }





                }else{
                    System.out.println(TerminalText.INV_CMD.getText());                    
                }

                
                //Responses
                if(waiting){
                    ALPMessage.readObject(in_from_server_object, msg_in);
                    System.out.println(msg_in.getCommand().toString());
                    if(msg_in.getCommand().equals(Command.ERROR.toString())){
                        System.out.println(msg_in.getArgs(0));
                    }else if (msg_in.getCommand().equals(Command.LST.toString())){
                        int num_threads = Integer.parseInt(msg_in.getArgs(0));
                        if(num_threads == 0){
                            System.out.println(TerminalText.NO_THREADS.getText());
                        }else{
                            System.out.println(TerminalText.LIST_THREAD.getText());
                            String[] threads = msg_in.getArgs(1).split(ThreadFile.thread_split_key);
                            for(int i = 0; i < num_threads;i++){
                                System.out.println(threads[i]);
    
                            }
                        }

                    }else if(msg_in.getCommand().equals(Command.RDT.toString())){
                        int num_lines = Integer.parseInt(msg_in.getArgs(0));
                        if(num_lines == 0){
                            System.out.println(TerminalText.EMPTY_THREAD.getText(cmd_input.get(1)));
                        }else{

                            String[] lines = msg_in.getArgs(1).split(ThreadFile.thread_split_key);
                            for(int i = 0; i < num_lines;i++){
                                System.out.println(lines[i]);
    
                            }
                        }
                    }else if(msg_in.getCommand().equals(Command.DWN.toString())){
                        System.out.println("DOWNLOAD");
                        Files.write(Paths.get(msg_in.getArgs(0)),msg_in.getPayload());
                    }
                }
                waiting = false;

            }
            
            alive = false;



        }
        client_socket.close();

    }


    





}
