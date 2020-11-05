package msgs;

public enum TerminalText {
    
    BAD_EDT("Message cannot be edited"),
    BAD_FILE("%s does not exist in thread %s"),
    BAD_SYNTAX("Incorrect syntax for %s"),
    BAD_THREADNAME("Invalid/illegal thread specified"),
    CANT_RMV("Thread %s cannot be removed"),
    CLIENT_CNCT("Client connected"),
    CMD_PROMPT("Enter one of the following commands:(CRT, LST, MSG) :"),
    FILE_DWN("%s downloaded from thread %s"),
    FILE_EXIST("File %s already exists"),
    FILE_UPD("%s uploaded file %s to thread %s"),
    INV_CHAR("Invalid character(s) in username/password"),
    INV_CMD("Invalid command"),
    LIST_THREAD("The list of active threads"),
    LOGIN_SUCC("%s Successful Login"),
    NEW_PSWD("Enter the new password for %s: "),
    NEW_USER("New User"),
    NO_THREADS("No threads to list"),
    PSWD_FAIL("Incorrect Password"),
    PSWD_PROMPT("Enter password: "),
    SRVR_SHT("Server shutting down"),
    SRVR_WAIT("Waiting For clients"),
    THREAD_CRT("Thread %s created"),
    THREAD_EDT("Message has been edited"),
    THREAD_MSG("Message posted to %s thread"),
    THREAD_RDT("Thread %s read"),
    USER_CMD("%s Issued %s command"),
    USER_EXIT("%s exited"),
    USER_LOGGED("User is already logged in"),
    USNM_PROMPT("Enter username: "),
    WELCOME("Welcome to the forum");


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
