package thread;

import java.util.Objects;

public class ThreadItem {

    private String user;
    private String message;


    public ThreadItem() {
    }

    public ThreadItem(String message,String user) {
        this.message = message;
        this.user = user;
    }

    public String getMessage() {
        return this.message;
    }

    public String getUser(){
        return this.user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ThreadItem message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ThreadItem)) {
            return false;
        }
        ThreadItem threadItem = (ThreadItem) o;
        return Objects.equals(message, threadItem.message);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message);
    }

    @Override
    public String toString() {
        return "{" +
            " message='" + getMessage() + "'" +
            "}";
    }
    
}