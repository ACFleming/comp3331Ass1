import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ThreadFile {

    private String user;
    private String threadname;
    private String pathname;
    private int msg_number;
    List<ThreadItem> contents;


    public ThreadFile() {
    }

    public ThreadFile(String user, String threadname) {
        this.user = user;
        this.threadname = threadname;
        this.pathname = "./" + threadname + ".txt";
        this.contents = new ArrayList<ThreadItem>();
        this.msg_number = 1;
        addItem(user);
    }

    public void addMessage(String message, String user){
        String line = String.valueOf(msg_number) + " " + user + ": " + message;
        contents.add(new ThreadMessage(line,msg_number));
        msg_number++;
    }

    // public void setMessage(String message,int number){
    //     contents.set(new ThreadMessage(message,);

    // }

    public void addItem(String message){
        contents.add(new ThreadItem(message));
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getThreadname() {
        return this.threadname;
    }

    public void setThreadname(String threadname) {
        this.threadname = threadname;
    }

    public String getPathname() {
        return this.pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public List<ThreadItem> getContents() {
        return this.contents;
    }

    public List<String> getMessages(){
        ArrayList<String> messages = new ArrayList<String>();
        for(ThreadItem item : this.contents){
            messages.add(item.getMessage());
        }
        return messages;
    }



    public void setContents(List<ThreadItem> contents) {
        this.contents = contents;
    }



    public ThreadItem getMessage(int number){
        for(ThreadItem item : this.contents){
            if(item instanceof ThreadMessage ){
                ThreadMessage msg = (ThreadMessage)item;
                if(msg.getMessage_number() == number){
                    return msg;
                }
            }
        }
        return null;
    }



    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ThreadFile)) {
            return false;
        }
        ThreadFile threadFile = (ThreadFile) o;
        return Objects.equals(threadname, threadFile.threadname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, threadname, contents);
    }

    @Override
    public String toString() {
        return "{" +
            " user='" + getUser() + "'" +
            ", threadname='" + getThreadname() + "'" +
            ", contents='" + getContents() + "'" +
            "}";
    }
}
