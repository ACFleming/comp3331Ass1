
package msgs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ALPMessage implements Serializable{


    private static String split_key = "::";

    private static final long serialVersionUID = 480180711443453936L;
    private byte[] payload;
    private String arg0;
    private String arg1;
    private String arg2;

    private String user;
    private String command;

    public ALPMessage(String command, String user, ArrayList<String> args, byte[] payload) {
        this.setPayload(payload);
        this.setArgs(args);
        this.setUser(user);
        this.setCommand(command);
        
    }



    public ALPMessage() {
        this.setPayload(null);
        this.setArgs("arg0", 0);
        this.setArgs("arg1", 1);;
        this.setArgs("arg2", 2);;
        this.setUser("Internal");
        this.setCommand("Unused");
    }
    



	public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public ArrayList<String> getArgs() {
        ArrayList<String> args = new ArrayList<String>();
        args.add(arg0);
        args.add(arg1);
        args.add(arg2);
        return args;
    }

    public String getArgs(int index){
        switch (index) {
            case 0:
                return arg0;
            case 1:
                return arg1;
            case 2:
                return arg2;
            default:
                return "ERROR";
        }
    }

    public void setArgs(ArrayList<String> args) {
        this.arg0 = args.get(0);
        this.arg1 = args.get(1);
        this.arg2 = args.get(2);
    }

    public void setArgs(String arg, int index){
        switch (index) {
            case 0:
                arg0 = arg;
                break;
            case 1:
                arg1 = arg;
                break;
            case 2:
                arg2 = arg;
                break;
            default:
                break;
        }
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setCommand(Command cmd){
        this.command = cmd.toString();
    }

    @Override
    public String toString() {
        return this.user +  split_key + this.arg0 + split_key +  this.arg1 +split_key + this.arg2 +split_key + this.command.toString();
    }

    public void fromString(String input){
        String[] list = input.split(split_key);
        this.setUser(list[0]);
        this.setArgs(list[1], 0);
        this.setArgs(list[2],1);
        this.setArgs(list[3], 2);
        this.setCommand(list[4]);

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

    public static void sendBytes(DataOutputStream out, ALPMessage msg) throws IOException {
        out.write(msg.getPayload());
    }

    public static void readBytes(DataInputStream in, ALPMessage msg) throws IOException {
        byte[] b = new byte[1024];
        in.readFully(b);
        msg.setPayload(b);


    }


}


