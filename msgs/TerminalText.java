package msgs;

public enum TerminalText {
    BAD_DLT("Invalid message number to delete"),
    BAD_EDT("Invalid message number to edit"),
    BAD_FILE("%s does not exist in thread %s"),
    BAD_SYNTAX("Incorrect syntax for %s"),
    BAD_THREADNAME("Invalid/illegal thread specified"),
    CANT_RMV("Unable to remove thread. Threads can only be removed by owner."),
    CANT_EDT("Unable to edit message. Messages can only be edited by owner."),
    CANT_DLT("Unable to delete message. Messages can only be deleted by owner."),
    CLIENT_CNCT("Client connected"),
    CMD_PROMPT("Enter one of the following commands:(CRT, DLT, DNW, EDT, LST, MSG, RDT, RMV, SHT, UPD, XIT) :"),
    EMPTY_THREAD("Thread %s is empty"),
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
    SRVR_SHT("\nServer shutting down"),
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

    public String getText(Object arg0, Object arg1){
        return String.format(this.text, arg0, arg1);
    }

    public String getText(){
        
        return this.text;
    }




    


}
