package net.ncplanner.plannerator.planner.dssl.token;
import static net.ncplanner.plannerator.planner.dssl.token.Helpers.*;
public class ArrayOrFieldAccessToken extends ESSLToken{
    public ArrayOrFieldAccessToken(){
        super(exprArrayOrFieldAccess);
    }
    public String identifier;
    public Object[] accesses;
    @Override
    public Token newInstance(){
        return new ArrayOrFieldAccessToken();
    }
    @Override
    public void load() {
        String[] splt = text.split("[\\[\\.]");
        identifier = splt[0];
        accesses = new Object[splt.length-1];
        for(int i = 0; i<accesses.length; i++){
            String s = splt[i+1];
            if(s.endsWith("]")){
                s = s.substring(0, s.length()-1);
                if(s.matches(digit+"+"))accesses[i] = Long.parseLong(s);
                else accesses[i] = s;
            }else{
                accesses[i] = s;//string literal
            }
        }
    }
    @Override
    public String compile(){
        String s = identifier;
        for(Object o : accesses)s+=" "+o.toString()+" get";
        return s;
    }
}