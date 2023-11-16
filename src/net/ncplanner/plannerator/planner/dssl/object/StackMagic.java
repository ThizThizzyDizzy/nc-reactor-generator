package net.ncplanner.plannerator.planner.dssl.object;
public class StackMagic extends StackVariable{
    public StackMagic(String name, StackMethod value){
        super(name, value);
    }
    @Override
    public StackVariable duplicateVariable(){
        return new StackMagic(name, duplicate().asMethod());
    }
}