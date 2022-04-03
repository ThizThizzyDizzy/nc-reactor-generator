package net.ncplanner.plannerator.planner.dssl.token;
import net.ncplanner.plannerator.planner.dssl.Script;
import net.ncplanner.plannerator.planner.dssl.object.StackFloat;
import net.ncplanner.plannerator.planner.dssl.object.StackInt;
import net.ncplanner.plannerator.planner.dssl.object.StackObject;
import net.ncplanner.plannerator.planner.dssl.object.StackVariable;
public class IncrementToken extends Token{
    public IncrementToken(){
        super("\\+\\+");
    }
    @Override
    public Token newInstance(){
        return new IncrementToken();
    }
    @Override
    public void run(Script script){
        StackVariable var = script.variables.get(script.pop().asLabel().getValue());
        if(var.getBaseType()==StackObject.Type.INT)var.setValue(new StackInt(var.asInt().getValue()+1));
        else if(var.getBaseType()==StackObject.Type.FLOAT)var.setValue(new StackFloat(var.asFloat().getValue()+1));
        else throw new IllegalArgumentException("Cannot increment "+var.getBaseType().toString()+"! (not a number!)");
    }
}