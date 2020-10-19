// import java.util.Hashtable;

public enum TerminalText {
    
    SRVR_WAIT("Waiting For clients"),
    USNM_PROMPT("Enter username: "),
    PSWD_PROMPT("Enter password: "),
    CLIENT_CNCT("Client connected"),
    WELCOME("Welcome to the forum"),
    NEW_USER("New User"),
    LOGIN_SUCC("%s Successful Login"),
    PSWD_FAIL("Incorrect Password"),
    USER_CMD("%s Issued %s command"),
    THREAD_CRT("Thread %s created"),
    THREAD_MSG("Message posted to %s thread"),
    THREAD_RDT("Thread %s read"),
    THREAD_EDT("Message has been edited"),
    LIST_THREAD("The list of active threads"),
    NO_THREADS("No threads to list"),
    BAD_EDT("Message cannot be edited"),
    BAD_THREADNAME("Incorrect thread specified"),
    BAD_SYNTAX("Incorrect syntax for %s"),
    FILE_UPD("%s uploaded file %s to thread %s"),
    FILE_DWN("%s downloaded from thread %s"),
    BAD_FILE("%s does not exist in thread %s"),
    CANT_RMV("Thread %s cannot be removed"),
    SRVR_SHT("Server shutting down"),
    USER_EXIT("%s exited");


    private final String text;


    private TerminalText(String text) {
        this.text = text;
    }


    public String getText(Object args){
        
        return String.format(this.text, args);
    }

    public String getText(){
        
        return this.text;
    }




    


}
