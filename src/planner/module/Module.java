package planner.module;
import generator.Priority;
import java.util.ArrayList;
import multiblock.Multiblock;
import planner.Core;
import planner.editor.suggestion.Suggestor;
public abstract class Module<T>{
    private boolean active;
    public Module(){
        this(false);
    }
    public Module(boolean defaultActive){
        active = defaultActive;
    }
    public final void activate(){
        active = true;
        onActivated();
        Core.refreshModules();
    }
    public final void deactivate(){
        active = false;
        onDeactivated();
        Core.refreshModules();
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
    public T calculateMultiblock(Multiblock m){
        return null;
    }
    public String getTooltip(Multiblock m, T o){
        return null;
    }
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
    public void getGenerationPriorities(Multiblock multiblock, ArrayList<Priority> priorities){}
    public void getSuggestors(Multiblock multiblock, ArrayList<Suggestor> suggestors){}
    public void addMultiblockTypes(ArrayList<Multiblock> multiblockTypes){}
    public void setActive(boolean active){
        if(active)activate();
        else deactivate();
    }
    public void addTutorials(){}
    public void addConfigurations(){}
}