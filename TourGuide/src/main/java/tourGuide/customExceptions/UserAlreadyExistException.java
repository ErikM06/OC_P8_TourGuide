package tourGuide.customExceptions;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException (String msg){
        super(msg);
    }
}
