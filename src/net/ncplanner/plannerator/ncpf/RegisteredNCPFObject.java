package net.ncplanner.plannerator.ncpf;
public abstract class RegisteredNCPFObject extends DefinedNCPFObject{
    public final String type;
    public RegisteredNCPFObject(String type){
        this.type = type;
    }
}