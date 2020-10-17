public class Signal {
    private byte[] payload;
    private String[] args;
    private String user;

    public Signal(byte[] payload, String[] args, String user) {
        this.setPayload(payload);
        this.setArgs(args);
        this.setUser(user);
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

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }




}


