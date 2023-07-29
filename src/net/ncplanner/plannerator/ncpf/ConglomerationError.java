package net.ncplanner.plannerator.ncpf;
public class ConglomerationError extends RuntimeException{
    public ConglomerationError(String message, Throwable cause){
        super(message, cause);
    }
    public ConglomerationError(String message){
        super(message);
    }
}