package net.ncplanner.plannerator.ncpf;
@Deprecated
public abstract class RegisteredNCPFObject extends DefinedNCPFObject{
    public final String type;
    public RegisteredNCPFObject(String type){
        this.type = type;
    }
}