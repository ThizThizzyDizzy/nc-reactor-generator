package net.ncplanner.plannerator.planner.module;
import net.ncplanner.plannerator.planner.Task;
public class InternalModule extends Module{
    public InternalModule(){
        super("_internal", "DEBUG");
    }
    @Override
    public String getDisplayName(){
        return "INTERNAL";
    }
    @Override
    public String getDescription(){
        return "Internal module for setup/testing purposes. Do not use.";
    }
    @Override
    public void addConfigurations(Task t){
        addConfigurationTask(t, "INTERNAL", "configurations/internal.ncpf.json", null, null);
        runTasks();
    }
}
