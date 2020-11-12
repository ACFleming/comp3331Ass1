import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import msgs.*;
import thread.ThreadFile;






public class Server extends Thread {

    private static Boolean shutdown = false;
    private static Hashtable<String,String> user_pass;
    private static List<Server> server_list;
    private static List<ThreadFile> threads;
    private static List<String> users;

    public static String cred_path_name = "./credentials.txt";

    private boolean alive;
    private Socket socket;
    private BufferedReader in_from_client;
    private DataOutputStream out_to_client;
    private ObjectInputStream in_from_client_object;
    private ObjectOutputStream out_to_client_object;
    private ALPMessage msg_out = new ALPMessage();
    private ALPMessage msg_in = new ALPMessage();


    public Server(Socket s) throws IOException {
        this.socket = s;

        in_from_client = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out_to_client_object = new ObjectOutputStream(socket.getOutputStream());
        out_to_client_object.flush();
        System.out.println("HERE");
        in_from_client_object = new ObjectInputStream(socket.getInputStream());
        System.out.println("HERE");
        if(out_to_client_object == null || in_from_client_object == null){
            System.out.println("NULL");
        }
        
    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        if(args.length != 2){
            System.err.println("Usage: java Server server_port admin_passwd" );
            System.exit(1);
        }
        user_pass = new Hashtable<String,String>();
        server_list = new ArrayList<Server>();
        users = new ArrayList<String>();
        threads = new ArrayList<ThreadFile>();

        ServerSocket sock = new ServerSocket(Integer.parseInt(args[0]));
        while(!shutdown){
            System.out.println(TerminalText.SRVR_WAIT.getText());
            Socket connected = sock.accept();
            Server svr = new Server(connected);
            server_list.add(svr);
            svr.start();

            
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
                ALPMessage.readObject(in_from_client_object,msg_in);
                if(users.contains(msg_in.getUser())){
                    msg_out.setCommand(Command.ERROR);
                    msg_out.setArgs(TerminalText.USER_LOGGED.getText(),0);
                    ALPMessage.sendObject(out_to_client_object,msg_out);
                }else if(user_pass.containsKey(msg_in.getUser())){
                    System.out.println("Need Password");
                    System.out.println(msg_in.getUser());
                    msg_out.setCommand(Command.NEED_PASSWORD);
                    ALPMessage.sendObject(out_to_client_object,msg_out);
                    ALPMessage.readObject(in_from_client_object,msg_in);
                    if(user_pass.get(msg_in.getUser()).equals(msg_in.getArgs(0))){
                        System.out.println("Correct Password");
                        logged_in = true;
                        users.add(msg_in.getUser());
                        msg_out.setCommand(Command.LOGIN_COMPLETE);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }else{
                        System.out.println("Wrong Password");
                        msg_out.setCommand(Command.LOGIN_FAIL);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }else{
                    System.out.println("New User");
                    msg_out.setCommand(Command.NEW_USER);
                    ALPMessage.sendObject(out_to_client_object,msg_out);
                    ALPMessage.readObject(in_from_client_object,msg_in);
                    credentials.add(msg_in.getUser()+ " " + msg_in.getArgs(0));
                    user_pass.put(msg_in.getUser(), msg_in.getArgs(0));
                    writeOutString(cred_path_name, credentials);
                    logged_in = true;
                    users.add(msg_in.getUser());
                    msg_out.setCommand(Command.LOGIN_COMPLETE);
                    ALPMessage.sendObject(out_to_client_object,msg_out);


                }
            }

            System.out.println("LOGGED IN");
            while(logged_in){
                ALPMessage.readObject(in_from_client_object,msg_in);
                System.out.println("GOT MESSAGE");


                //BIG IF SWITCH STATEMENT
                //CRT threadname
                if(msg_in.getCommand().equals(Command.CRT.toString())){
                    
                    String threadname = msg_in.getArgs(0);
                    String pathname = "./" + threadname + ".txt";
                    
                    File new_thread = new File(pathname);
                    if(new_thread.createNewFile()){
                        System.out.println("New thread at: " + pathname);
                        List<String> text = new ArrayList<String>();
                        text.add(msg_in.getUser());
                        writeOutString(pathname, text);
                        msg_out.setCommand(Command.SUCCESS);
                        threads.add(new ThreadFile(msg_in.getUser(), threadname));
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }else{
                        System.out.println("Existing thread at: " + pathname);
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.FILE_EXIST.getText(pathname), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);

                    }
                }else if(msg_in.getCommand().equals(Command.RMV.toString())){
                    ThreadFile remove = new ThreadFile(null, msg_in.getArgs(0));
                    
                    if(threads.indexOf(remove) == -1){
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }else{
                        File remove_thread = new File(threads.get(threads.indexOf(remove)).getPathname());
                        remove_thread.delete();
                        threads.remove(remove);
                        msg_out.setCommand(Command.SUCCESS);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }

                
                //MSG threadname message
                }else if(msg_in.getCommand().equals(Command.MSG.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                        
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        // String pathname = "./" + msg_in.getArgs(0) + ".txt";
                        // List<String> file_contents = readInText(pathname);
                        selected_thread.addMessage(msg_in.getArgs(1),msg_in.getUser());
                        // file_contents.add(msg_in.getUser() +": " + msg_in.getArgs(1));
                        
                        writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                        msg_out.setCommand(Command.SUCCESS);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                
                // LST
                }else if(msg_in.getCommand().equals(Command.LST.toString())){
                    msg_out.setCommand(Command.LST);
                    msg_out.setArgs(String.valueOf(threads.size()),0);
                    List<String> threadnames = new ArrayList<String>();
                    for(ThreadFile th : Server.threads){
                        threadnames.add(th.getThreadname());
                    }
                    msg_out.setArgs(String.join(ThreadFile.thread_split_key,threadnames),1);
                    ALPMessage.sendObject(out_to_client_object,msg_out);
                
                // EDT threadname messagenumber message
                }else if(msg_in.getCommand().equals(Command.EDT.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                        
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        
                        // String pathname = "./" + msg_in.getArgs(0) + ".txt";
                        // List<String> file_contents = readInText(pathname);
                        System.out.println(msg_in.getArgs(2));
                        if(selected_thread.editMessage(msg_in.getUser(), msg_in.getArgs(2), Integer.parseInt(msg_in.getArgs(1)))==-1){
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_EDT.getText(),0);
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }else{
                        // file_contents.add(msg_in.getUser() +": " + msg_in.getArgs(1));
                        
                            writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                            msg_out.setCommand(Command.SUCCESS);
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }else if(msg_in.getCommand().equals(Command.DLT.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                        
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        
                        // String pathname = "./" + msg_in.getArgs(0) + ".txt";
                        // List<String> file_contents = readInText(pathname);

                        if(selected_thread.deleteMessage(msg_in.getUser(),Integer.parseInt(msg_in.getArgs(1)))==-1){
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_DLT.getText(),0);
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }else{
                        // file_contents.add(msg_in.getUser() +": " + msg_in.getArgs(1));
                        
                            writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                            msg_out.setCommand(Command.SUCCESS);
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }else if(msg_in.getCommand().equals(Command.RDT.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                        
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        
                        // // String pathname = "./" + msg_in.getArgs(0) + ".txt";
                        // // List<String> file_contents = readInText(pathname);

                        List<String> read = selected_thread.readThread();
                        msg_out.setCommand(Command.RDT);
                        msg_out.setArgs(String.valueOf(read.size()),0);
                        msg_out.setArgs(String.join(ThreadFile.thread_split_key,read),1);
                        ALPMessage.sendObject(out_to_client_object,msg_out);


                        // if(selected_thread.deleteMessage(msg_in.getUser(),Integer.parseInt(msg_in.getArgs(1)))==-1){
                        //     msg_out.setCommand(Command.ERROR);
                        //     msg_out.setArgs(TerminalText.BAD_EDT.getText(),0);
                        //     ALPMessage.sendObject(out_to_client_object,msg_out);
                        // }else{
                        // // file_contents.add(msg_in.getUser() +": " + msg_in.getArgs(1));
                        
                        //     writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                        //     msg_out.setCommand(Command.SUCCESS);
                        //     ALPMessage.sendObject(out_to_client_object,msg_out);
                        // }
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }else if(msg_in.getCommand().equals(Command.UPD.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                            
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        String pathname = "./" + msg_in.getArgs(0) + "-" + msg_in.getArgs(1);
                        // List<String> file_contents = readInText(pathname);
                        
                        Files.write(Paths.get(pathname),msg_in.getPayload());
                        selected_thread.addFile(msg_in.getArgs(1));
                        selected_thread.addItem(msg_in.getUser() + " uploaded " + msg_in.getArgs(1),msg_in.getUser());
                        // file_contents.add(msg_in.getUser() +": " + msg_in.getArgs(1));
                        
                        writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                        msg_out.setCommand(Command.SUCCESS);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }else if(msg_in.getCommand().equals(Command.DWN.toString())){
                    if(threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))){
                            
                        ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                        if(selected_thread.getFileList().contains(msg_in.getArgs(1))){
                            String pathname = "./" + msg_in.getArgs(0) + "-" + msg_in.getArgs(1);
                            msg_out.setCommand(Command.DWN);
                            msg_out.setArgs(msg_in.getArgs(1),0);
                            msg_out.setPayload(Files.readAllBytes(Paths.get(pathname)));
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }else{
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_FILE.getText(msg_in.getArgs(0),msg_in.getArgs(1)), 0);
                            ALPMessage.sendObject(out_to_client_object,msg_out);
                        }
                    }else{
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object,msg_out);
                    }
                }
                
            }


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e){
            System.out.println("SOCKET ERROR");
        } catch (EOFException e){
            System.out.println("EOF ERROR");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }






    }


    
// from https://techblogstation.com/java/read-text-file-in-java/

    public static void number_lines(List<String> message_lines){
        int msg_counter = 1;
        Iterator<String> it = message_lines.iterator();
        
        for(int i = 0; i < message_lines.size(); i++) {
            String line = message_lines.get(i);
            //i.e its contains a : (not file upload) and is not a 1 word line (not a username)
            if(line.contains(":") && line.split(" ").length > 1){
                line = Integer.toString(msg_counter) + " " + line;
                msg_counter++;
                message_lines.set(i, line);

            }
        }
    }




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



}