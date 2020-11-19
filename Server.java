import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import msgs.*;
import thread.ThreadFile;
import thread.ThreadItem;

public class Server extends Thread {

    private static Boolean shutdown = false;
    private static Hashtable<String, String> user_pass;
    private static List<Server> server_list;
    private static List<ThreadFile> threads;
    private static List<String> users;
    private static String admin_pass;
    static ReentrantLock syncLock = new ReentrantLock();

    public static String cred_path_name = "./credentials.txt";

    private boolean alive;
    private Socket socket;
    private ObjectInputStream in_from_client_object;
    private ObjectOutputStream out_to_client_object;
    private ALPMessage msg_out = new ALPMessage();
    private ALPMessage msg_in = new ALPMessage();

    public Server(Socket s) throws IOException {
        this.socket = s;

        out_to_client_object = new ObjectOutputStream(socket.getOutputStream());
        out_to_client_object.flush();
        System.out.println("HERE");
        in_from_client_object = new ObjectInputStream(socket.getInputStream());
        System.out.println("HERE");
        if (out_to_client_object == null || in_from_client_object == null) {
            System.out.println("NULL");
        }

    }

    public static void main(String[] args) throws NumberFormatException, IOException {
        if (args.length != 2) {
            System.err.println("Usage: java Server server_port admin_passwd");
            System.exit(1);
        }
        user_pass = new Hashtable<String, String>();
        server_list = new ArrayList<Server>();
        users = new ArrayList<String>();
        threads = new ArrayList<ThreadFile>();

        ServerSocket sock = new ServerSocket(Integer.parseInt(args[0]));
        admin_pass = args[1];
        while (!shutdown) {
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

            //Read credentials
            List<String> credentials = readInText(cred_path_name);
            String[] retval;
            Iterator<String> it = credentials.iterator();
            while (it.hasNext()) {
                retval = it.next().split(" ");
                user_pass.put(retval[0], retval[1]);
            }

            System.out.println(TerminalText.CLIENT_CNCT.getText());

            boolean logged_in = false;
            syncLock.lock();
            try {
                while (!logged_in) {
                
                    //interruptible read
                    try {
                        ALPMessage.readObject(in_from_client_object, msg_in);
                    } catch (SocketTimeoutException e) {
                        System.out.println("BLOCKING");
                        continue;
                    }
    
                    //If shutdown
                    if (msg_in.getCommand().equals(Command.SHT.toString())) {
                        msg_out.setCommand(Command.SHT);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
                        System.exit(1);
                    }
    
                    //If user already logged in
                    if (users.contains(msg_in.getUser())) {
                        msg_out.setCommand(Command.ERROR);
                        msg_out.setArgs(TerminalText.USER_LOGGED.getText(), 0);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
    
                    //if user already exists
                    } else if (user_pass.containsKey(msg_in.getUser())) {
                        System.out.println("Need Password");
                        System.out.println(msg_in.getUser());
                        msg_out.setCommand(Command.NEED_PASSWORD);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
                        ALPMessage.readObject(in_from_client_object, msg_in);
                        if (user_pass.get(msg_in.getUser()).equals(msg_in.getArgs(0))) {
                            System.out.println("Correct Password");
                            logged_in = true;
                            users.add(msg_in.getUser());
                            msg_out.setCommand(Command.LOGIN_COMPLETE);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        } else {
                            System.out.println("Wrong Password");
                            msg_out.setCommand(Command.LOGIN_FAIL);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
    
                    //if new
                    } else {
                        System.out.println("New User");
                        msg_out.setCommand(Command.NEW_USER);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
                        ALPMessage.readObject(in_from_client_object, msg_in);
                        credentials.add(msg_in.getUser() + " " + msg_in.getArgs(0));
                        user_pass.put(msg_in.getUser(), msg_in.getArgs(0));
                        writeOutString(cred_path_name, credentials);
                        logged_in = true;
                        users.add(msg_in.getUser());
                        msg_out.setCommand(Command.LOGIN_COMPLETE);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
    
                    }
    
                }
            } finally{
                syncLock.unlock();
            }

            






            // LOGGED IN




            System.out.println("LOGGED IN");
            while (logged_in) {

                try {
                    ALPMessage.readObject(in_from_client_object, msg_in);
                } catch (SocketTimeoutException e) {
                    continue;
                }

                System.out.println("GOT MESSAGE");
                syncLock.lock();
                try {
                    if (msg_in.getCommand().equals(Command.CRT.toString())) {

                        String threadname = msg_in.getArgs(0);
                        String pathname = "./" + threadname;
    
                        File new_thread = new File(pathname);
                        if (new_thread.createNewFile()) {
                            System.out.println("New thread at: " + pathname);
                            List<String> text = new ArrayList<String>();
                            text.add(msg_in.getUser());
                            writeOutString(pathname, text);
                            msg_out.setCommand(Command.SUCCESS);
                            threads.add(new ThreadFile(msg_in.getUser(), threadname));
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        } else {
                            System.out.println("Existing thread at: " + pathname);
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.FILE_EXIST.getText(pathname), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
    
                        }
    
                        // RMV threadname
                    } else if (msg_in.getCommand().equals(Command.RMV.toString())) {
    
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
                            
                            ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                            if(selected_thread.getUser().equals(msg_in.getUser())){
                                List<String> files = selected_thread.getFileList();
                                for (int i = 0; i < files.size(); i++) {
                                    File remove_file = new File("./" + msg_in.getArgs(0) + "-" + files.get(i));
                                    remove_file.delete();
                                }
                                File remove_thread = new File(selected_thread.getPathname());
                                remove_thread.delete();
                                threads.remove(selected_thread);
                                msg_out.setCommand(Command.SUCCESS);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }else{
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.CANT_RMV.getText(),0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }


                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
    
    
                        // MSG threadname message
                    } else if (msg_in.getCommand().equals(Command.MSG.toString())) {
    
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads
                                    .get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                            selected_thread.addMessage(msg_in.getArgs(1), msg_in.getUser());
    
                            writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                            msg_out.setCommand(Command.SUCCESS);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
    
                        // LST
                    } else if (msg_in.getCommand().equals(Command.LST.toString())) {
                        msg_out.setCommand(Command.LST);
                        msg_out.setArgs(String.valueOf(threads.size()), 0);
                        List<String> threadnames = new ArrayList<String>();
                        for (ThreadFile th : Server.threads) {
                            threadnames.add(th.getThreadname());
                        }
                        msg_out.setArgs(String.join(ThreadFile.thread_split_key, threadnames), 1);
                        ALPMessage.sendObject(out_to_client_object, msg_out);
    
                        // EDT threadname messagenumber message
                    } else if (msg_in.getCommand().equals(Command.EDT.toString())) {
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
    
                            System.out.println(msg_in.getArgs(2));
                            int result = selected_thread.editMessage(msg_in.getUser(), msg_in.getArgs(2),
                            Integer.parseInt(msg_in.getArgs(1)));
                            if (result == -1) {
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.BAD_EDT.getText(), 0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }else if(result == -2){
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.CANT_EDT.getText(), 0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            } else {
    
    
                                writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                                msg_out.setCommand(Command.SUCCESS);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
                    } else if (msg_in.getCommand().equals(Command.DLT.toString())) {
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads
                                    .get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
    
                            int result = selected_thread.deleteMessage(msg_in.getUser(),Integer.parseInt(msg_in.getArgs(1)));
                            if (result == -1) {
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.BAD_DLT.getText(), 0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }else if(result == -2){
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.CANT_DLT.getText(), 0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            
                            } else {
    
    
                                writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                                msg_out.setCommand(Command.SUCCESS);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
    
                        // RDT
                    } else if (msg_in.getCommand().equals(Command.RDT.toString())) {
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads
                                    .get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
    
    
                            List<String> read = selected_thread.readThread();
                            msg_out.setCommand(Command.RDT);
                            msg_out.setArgs(String.valueOf(read.size()), 0);
                            msg_out.setArgs(String.join(ThreadFile.thread_split_key, read), 1);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
    
    
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
                    } else if (msg_in.getCommand().equals(Command.UPD.toString())) {
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads.get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                            String pathname = "./" + msg_in.getArgs(0) + "-" + msg_in.getArgs(1);
    
    
                            Files.write(Paths.get(pathname), msg_in.getPayload());
                            selected_thread.addFile(msg_in.getArgs(1));
                            selected_thread.addItem(msg_in.getUser() + " uploaded " + msg_in.getArgs(1), msg_in.getUser());
    
    
                            writeOutString(selected_thread.getPathname(), selected_thread.getMessages());
                            msg_out.setCommand(Command.SUCCESS);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
                    } else if (msg_in.getCommand().equals(Command.DWN.toString())) {
                        if (threads.contains(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0)))) {
    
                            ThreadFile selected_thread = threads
                                    .get(threads.indexOf(new ThreadFile(msg_in.getUser(), msg_in.getArgs(0))));
                            if (selected_thread.getFileList().contains(msg_in.getArgs(1))) {
                                String pathname = "./" + msg_in.getArgs(0) + "-" + msg_in.getArgs(1);
                                msg_out.setCommand(Command.DWN);
                                msg_out.setArgs(msg_in.getArgs(1), 0);
                                msg_out.setPayload(Files.readAllBytes(Paths.get(pathname)));
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            } else {
                                msg_out.setCommand(Command.ERROR);
                                msg_out.setArgs(TerminalText.BAD_FILE.getText(msg_in.getArgs(1), msg_in.getArgs(0)), 0);
                                ALPMessage.sendObject(out_to_client_object, msg_out);
                            }
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.BAD_THREADNAME.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
                    } else if (msg_in.getCommand().equals(Command.XIT.toString())) {
                        logged_in = false;
                        
                        users.remove(msg_in.getUser());
    
    
    
                    } else if (msg_in.getCommand().equals(Command.SHT.toString())) {
                        System.out.println(admin_pass);
                        if (msg_in.getArgs(0).equals(admin_pass)) {
                            msg_out.setCommand(Command.SHT);
                            Iterator<ThreadFile> th_it = threads.iterator();
                            while(th_it.hasNext()){
                                ThreadFile selected_thread = th_it.next();
                                Iterator<String> file_it = selected_thread.getFileList().iterator();
                                while(file_it.hasNext()){
                                    String pathname = "./" + selected_thread.getThreadname() + "-" + file_it.next();
                                    System.out.println("FILE PATH NAME " + pathname);
                                    File remove_file = new File(pathname);
                                    remove_file.delete();
                                    file_it.remove();
                                }
                                File remove_file = new File(selected_thread.getPathname());
                                remove_file.delete();
                                th_it.remove();
                            }
                            File remove_credentials = new File(cred_path_name);
                            remove_credentials.delete();
    
    
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                            shutdown = true;
                            logged_in = false;
    
                        } else {
                            msg_out.setCommand(Command.ERROR);
                            msg_out.setArgs(TerminalText.PSWD_FAIL.getText(), 0);
                            ALPMessage.sendObject(out_to_client_object, msg_out);
                        }
                    }
    
                } finally {
                    syncLock.unlock();
                }

                // BIG IF SWITCH STATEMENT
                // CRT threadname

            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("SOCKET ERROR");
        } catch (EOFException e) {
            System.out.println("EOF ERROR");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(syncLock.isHeldByCurrentThread()){
                syncLock.unlock();
            }
            
        }
        

        
        System.out.println("THREAD DEAD");

        try {
            socket.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if(shutdown){
            msg_out.setCommand(Command.SHT);
            try {
                ALPMessage.sendObject(out_to_client_object, msg_out);
            } catch (IOException e) {
                
                
            }
            System.exit(1);
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