package net.ncplanner.plannerator.planner.dssl.object;
public class StackMacro extends StackVariable{
    public StackMacro(String name, StackMethod value){
        super(name, value);
    }
    @Override
    public StackMacro duplicateVariable(){
        return new StackMacro(name, duplicate().asMethod());
    }
}