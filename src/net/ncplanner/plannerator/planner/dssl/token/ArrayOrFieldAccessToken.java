package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackCollection;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackString;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
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
                accesses[i] = new StackString(s);
            }
        }
    }
    @Override
    public void run(Script script){
        StackVariable var = script.variables.get(identifier);
        if(var==null)throw new NullPointerException("S'tack variable "+text+" does not exist!");
        StackObject value = var.getValue();
        for(Object o : accesses){
            if(o instanceof Long){
                value = value.asCollection().get(new StackInt((long)o));
            }else if(o instanceof String){
                String str = (String)o;
                StackVariable key = script.variables.get(str);
                value = value.asCollection().get(key);
            }else if(o instanceof StackString){
                value = value.asCollection().get((StackString)o);
            }
        }
        script.push(value);
    }
    @Override
    public String compile(){
        String s = identifier;
        for(Object o : accesses)s+=" "+o.toString()+" get";
        return s;
    }
}