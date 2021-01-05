package planner.editor.module;
import generator.Priority;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.editor.suggestion.Suggestor;
public abstract class Module<T>{
    private boolean active = false;
    public void activate(){
        active = true;
        onActivated();
    }
    public void deactivate(){
        active = false;
        onDeactivated();
    }
    public boolean isActive(){
        return active;
    }
    protected void onActivated(){}
    protected void onDeactivated(){}
    public abstract String getName();
    public abstract String getDescription();
    /**
     * Calculate this module for a specified multiblock
     * @param m the multiblock to calculate
     * @return a String to add to the tooltip, or `null` if there is none
     */
    public abstract T calculateMultiblock(Multiblock m);
    public abstract String getTooltip(Multiblock m, T o);
    protected String percent(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = (Math.round(n*fac*100)/(double)Math.round(fac));
        return (digits==0?Math.round(d):d)+"%";
    }
    protected String round(double n, int digits){
        double fac = Math.pow(10, digits);
        double d = Math.round(n*fac)/(double)Math.round(fac);
        return (digits==0?Math.round(d):d)+"";
    }
    public abstract void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities);
    public abstract void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors);
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
}