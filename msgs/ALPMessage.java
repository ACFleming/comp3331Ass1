
package msgs;

import java.io.Serializable;
import java.util.ArrayList;

public class ALPMessage implements Serializable{




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
        this.setArgs("arg", 0);
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

    @Override
    public String toString() {
        return this.user +  " " + this.arg0 + " " +  this.arg1 +" " + this.arg2 +" " + this.command.toString();
    }

    public void fromString(String input){
        String[] list = input.split(" ");
        this.setUser(list[0]);
        this.setArgs(list[1], 0);
        this.setArgs(list[2],1);
        this.setArgs(list[3], 2);
        this.setCommand(list[4]);

    }

}


