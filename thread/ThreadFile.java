package thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;




public class ThreadFile {

    public static String thread_split_key = "phlydgfl2345ifgu7nndgsdiodh7to8vgoy9ots8f7";

    private String user;
    private String threadname;
    private String pathname;
    private int msg_number;
    List<ThreadItem> contents;
    List<String> files;


    public ThreadFile() {
    }

    public ThreadFile(String user, String threadname) {
        this.user = user;
        this.threadname = threadname;
        this.pathname = "./" + threadname;
        this.contents = new ArrayList<ThreadItem>();
        this.files = new ArrayList<String>();
        this.msg_number = 1;
        addItem(user,user);
    }

    public void addMessage(String message, String user){
        String line = String.valueOf(msg_number) + " " + user + ": " + message;
        contents.add(new ThreadMessage(line,user,msg_number));
        msg_number++;
    }

    // public void setMessage(String message,int number){
    //     contents.set(new ThreadMessage(message,);

    // }

    public void addFile(String filename){
        files.add(filename);
    }

    public List<String> getFileList(){
        return this.files;
    }

    public List<String> readThread(){
        List<String> read = new ArrayList<String>();
        for(int i = 1; i < contents.size(); i++){
            read.add(contents.get(i).getMessage());

        }
        return read;
    }

    public int deleteMessage(String user, int number){
        ThreadMessage msg = getMessage(number);
        if(msg == null || !msg.getUser().equals(user)){
            return -1;
        }
        int index = contents.indexOf(msg);
        contents.remove(index);
        this.msg_number--;
        for(int i = index; i < contents.size(); i++){
            ThreadItem item = contents.get(i);
            if(item instanceof ThreadMessage){
                msg = (ThreadMessage)item;
                msg.setMessage_number(msg.getMessage_number()-1);
                String textline = msg.getMessage();

                
                textline = textline.replaceFirst("\\d+", String.valueOf(msg.getMessage_number()));
                
                msg.setMessage(textline);



            }
            
        }
        return 0;
    }

    public int editMessage(String user, String message, int number){
        ThreadItem edit = getMessage(number);
        if(edit == null || !edit.getUser().equals(user)){
            return -1;
        }
        String[] separated = edit.getMessage().split(":");
        edit.setMessage(separated[0] + ": " +  message);
        return 0;

    }

    public void addItem(String message, String user){
        contents.add(new ThreadItem(message,user));
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



    public ThreadMessage getMessage(int number){
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
