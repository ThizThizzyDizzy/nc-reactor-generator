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
        addConfigurationTask(t, "INTERNAL", "configurations/internal.ncpf.json");
        addConfigurationTask(t, "Nuclearcraft+Mods", "configurations/nuclearcraft+mods.ncpf.json");
        addConfigurationTask(t, "Nuclearcraft+Mods+Moar", "configurations/nuclearcraft+mods+moar.ncpf.json");
        runTasks();
    }
}
