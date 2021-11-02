package net.ncplanner.plannerator.multiblock;
public class FluidStack{
    public final String name;
    private final String displayName;
    public double amount;
    public FluidStack(String name, String displayName, double amount){
        this.name = name;
        this.displayName = displayName;
        this.amount = amount;
    }
    public String getDisplayName(){
        return displayName==null?name:displayName;
    }
}