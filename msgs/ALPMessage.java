
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
    private Command command;

    public ALPMessage(Command command, String user, ArrayList<String> args, byte[] payload) {
        this.setPayload(payload);
        this.setArgs(args);
        this.setUser(user);
        this.setCommand(command);
        
    }



    public ALPMessage() {
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

    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }




}


