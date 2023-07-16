package net.ncplanner.plannerator.planner.file.recovery;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
public class NonRecoveryHandler extends RecoveryModeHandler{
    @Override
    protected <T extends NCPFElement> T recoverFallbackID(String type, int idx, List<? extends NCPFElement> list, List<? extends NCPFElement> fallbackList, boolean allowNull){
        if(idx>=0&&idx<list.size())return (T)list.get(idx);
        throw new IllegalArgumentException("Invalid "+type+" index: "+idx+"!");
    }
    @Override
    protected <T extends NCPFElement> T recoverFallbackName(String type, String name, List<T> list, Function<String, Boolean> nameProcessor, Supplier<T> recoverFunc, boolean allowNull){
        if(nameProcessor==null)nameProcessor = (nam)->{
            return nam.equalsIgnoreCase(name);
        };
        for(T t : list){
            for(String legacy : t.getModule(DisplayNamesModule::new).legacyNames){
                if(nameProcessor.apply(legacy))return t;
            }
        }
        throw new IllegalArgumentException("Invalid "+type+" name: "+name+"!");
    }
}