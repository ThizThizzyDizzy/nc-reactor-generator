package net.ncplanner.plannerator.planner.s_tack;
public class StackUnderflowError extends RuntimeException{
    public StackUnderflowError(){
        super();
    }
    public StackUnderflowError(String message){
        super(message);
    }
    public StackUnderflowError(Throwable cause){
        super(cause);
    }
    public StackUnderflowError(String message, Throwable cause){
        super(message, cause);
    }
}