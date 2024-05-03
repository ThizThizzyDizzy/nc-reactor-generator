package net.ncplanner.plannerator.planner.module;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
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
        t.addSubtask("INTERNAL");
        addConfiguration(new Configuration(FileReader.read(() -> {
            return Core.getInputStream("configurations/internal.ncpf.json");
        })));
        t.getCurrentSubtask().finish();
    }
}