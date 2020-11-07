package thread;

import java.util.Objects;

public class ThreadMessage extends ThreadItem {

    private int message_number;

    public ThreadMessage() {
    }

    public ThreadMessage(String message, String user, int message_number){
        super(message,user);
        setMessage_number(message_number);
    }

    public int getMessage_number() {
        return this.message_number;
    }

    public void setMessage_number(int message_number) {
        this.message_number = message_number;
    }

    public ThreadMessage message_number(int message_number) {
        this.message_number = message_number;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ThreadMessage)) {
            return false;
        }
        ThreadMessage threadMessage = (ThreadMessage) o;
        return message_number == threadMessage.message_number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message_number);
    }

    @Override
    public String toString() {
        return "{" +
            " message_number='" + getMessage_number() + "'" +
            "}";
    }
    
}