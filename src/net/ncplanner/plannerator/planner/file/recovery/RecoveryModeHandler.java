package net.ncplanner.plannerator.planner.file.recovery;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuMessageDialog;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.module.LegacyNamesModule;
public class RecoveryModeHandler implements RecoveryHandler{
    HashMap<String, Integer> fallbackChoices = new HashMap<>();
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuelLegacyNCPF(Project ncpf, int id) {
        return recoverFallbackID("fuel", id, ncpf.getConfiguration(UnderhaulSFRConfiguration::new).fuels, Core.project.getConfiguration(UnderhaulSFRConfiguration::new).fuels, false);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement recoverUnderhaulSFRBlockLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("block", id, ncpf.getConfiguration(UnderhaulSFRConfiguration::new).blocks, Core.project.getConfiguration(UnderhaulSFRConfiguration::new).blocks, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipeLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("coolant recipe", id, ncpf.getConfiguration(OverhaulSFRConfiguration::new).coolantRecipes, Core.project.getConfiguration(OverhaulSFRConfiguration::new).coolantRecipes, false);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement recoverOverhaulSFRBlockLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("block", id, ncpf.getConfiguration(OverhaulSFRConfiguration::new).blocks, Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks, true);
    }
    @Override
    public <T extends NCPFElement> T recoverOverhaulSFRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, int id){
        List<T> list = null;
        List<T> fallbackList = null;
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement fallback = Core.project.getConfiguration(OverhaulSFRConfiguration::new).getElement(block.definition);
        if(block.parent!=null)block = block.parent;
        if(fallback!=null&&fallback.parent!=null)fallback = fallback.parent;
        if(block.fuelCell!=null){
            list = (List<T>)block.fuels;
            if(fallback!=null)fallbackList = (List<T>)fallback.fuels;
        }
        if(block.irradiator!=null){
            list = (List<T>)block.irradiatorRecipes;
            if(fallback!=null)fallbackList = (List<T>)fallback.irradiatorRecipes;
        }
        return recoverFallbackID(block.fuelCell!=null?"fuel":"recipe", id, list, fallbackList, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement recoverOverhaulMSRBlockLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("block", id, ncpf.getConfiguration(OverhaulMSRConfiguration::new).blocks, Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks, true);
    }
    @Override
    public <T extends NCPFElement> T recoverOverhaulMSRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, int id) {
        List<T> list = null;
        List<T> fallbackList = null;
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement fallback = Core.project.getConfiguration(OverhaulMSRConfiguration::new).getElement(block.definition);
        if(block.parent!=null)block = block.parent;
        if(fallback!=null&&fallback.parent!=null)fallback = fallback.parent;
        if(block.fuelVessel!=null){
            list = (List<T>)block.fuels;
            if(fallback!=null)fallbackList = (List<T>)fallback.fuels;
        }
        if(block.irradiator!=null){
            list = (List<T>)block.irradiatorRecipes;
            if(fallback!=null)fallbackList = (List<T>)fallback.irradiatorRecipes;
        }
        if(block.heater!=null){
            list = (List<T>)block.heaterRecipes;
            if(fallback!=null)fallbackList = (List<T>)fallback.heaterRecipes;
        }
        return recoverFallbackID(block.fuelVessel!=null?"fuel":"recipe", id, list, fallbackList, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe recoverOverhaulTurbineRecipeLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("recipe", id, ncpf.getConfiguration(OverhaulTurbineConfiguration::new).recipes, Core.project.getConfiguration(OverhaulTurbineConfiguration::new).recipes, false);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement recoverOverhaulTurbineBlockLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("block", id, ncpf.getConfiguration(OverhaulTurbineConfiguration::new).blocks, Core.project.getConfiguration(OverhaulTurbineConfiguration::new).blocks, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe recoverOverhaulFusionCoolantRecipeLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("coolant recipe", id, ncpf.getConfiguration(OverhaulFusionConfiguration::new).coolantRecipes, Core.project.getConfiguration(OverhaulFusionConfiguration::new).coolantRecipes, false);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement recoverOverhaulFusionBlockLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("block", id, ncpf.getConfiguration(OverhaulFusionConfiguration::new).blocks, Core.project.getConfiguration(OverhaulFusionConfiguration::new).blocks, true);
    }
    @Override
    public <T extends NCPFElement> T recoverOverhaulFusionBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement block, int id) {
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement fallback = Core.project.getConfiguration(OverhaulFusionConfiguration::new).getElement(block.definition);
        return recoverFallbackID("recipe", id, block.breedingBlanketRecipes, fallback==null?null:fallback.breedingBlanketRecipes, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe recoverOverhaulFusionRecipeLegacyNCPF(Project ncpf, int id){
        return recoverFallbackID("recipe", id, ncpf.getConfiguration(OverhaulFusionConfiguration::new).recipes, Core.project.getConfiguration(OverhaulFusionConfiguration::new).recipes, false);
    }
    protected <T extends NCPFElement> T recoverFallbackID(String type, int idx, List<? extends NCPFElement> list, List<? extends NCPFElement> fallbackList, boolean allowNull){
        if(idx>=0&&idx<list.size())return (T)list.get(idx);
        String fallback = fallbackList!=null&&idx>=0&&idx<fallbackList.size()?fallbackList.get(idx).toString():null;
        String fallbackChoice = type+"~"+idx+"~"+fallback;
        Integer result = fallbackChoices.get(fallbackChoice);
        if(result==null){
            MenuMessageDialog dialog = new MenuMessageDialog(cap(type)+" index invalid: "+idx+"/"+list.size()+"!"+"\nReset to "+list.get(0).toString()+"?"+(fallback==null?"":"\nRecover with "+fallback+" from fallback configuration?"));
            if(allowNull)dialog.addButton("Remove");
            dialog.addButton("Reset");
            if(fallback!=null)dialog.addButton("Recover");
            dialog.addButton("Ignore");
            result = dialog.openAsync();
        }
        fallbackChoices.put(fallbackChoice, result);
        if(!allowNull)result++;
        if(fallback==null&&result>1)result++;
        switch(result){
            case 0:
                return null;
            case 1:
                return (T)list.get(0);
            case 2:
                return (T)fallbackList.get(idx);
        }
        return (T)list.get(idx);//gonna crash, but that's what you get for clicking ignore
    }
    protected <T extends NCPFElement> T recoverFallbackName(String type, String name, List<T> list, Function<String, Boolean> nameProcessor, Supplier<T> recoverFunc, boolean allowNull){
        if(nameProcessor==null)nameProcessor = (nam)->{
            return nam.equalsIgnoreCase(name);
        };
        for(T t : list){
            LegacyNamesModule module = t.getModule(LegacyNamesModule::new);
            if(module!=null){
                for(String legacy : module.legacyNames){
                    if(nameProcessor.apply(legacy))return t;
                }
            }
        }
        T fallback = recoverFunc==null?null:recoverFunc.get();
        MenuMessageDialog dialog = new MenuMessageDialog(cap(type)+" name invalid: "+name+"!"+"\nReset to "+list.get(0).toString()+"?"+(fallback==null?"":"\nRecover as "+fallback+"?"));
        if(allowNull)dialog.addButton("Remove");
        dialog.addButton("Reset");
        if(fallback!=null)dialog.addButton("Recover");
        
        int result = dialog.openAsync();
        if(!allowNull)result++;
        if(fallback==null&&result>1)result++;
        switch(result){
            case 0:
                return null;
            case 1:
                return list.get(0);
            case 2:
                return fallback;
        }
        return null;//should never happen
    }
    private String cap(String s){
        return s.substring(0, 1).toUpperCase(Locale.ROOT)+s.substring(1);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuel(String name, Float heat, Float power){
        return recoverFallbackName("fuel", name, Core.project.getConfiguration(UnderhaulSFRConfiguration::new).fuels, null, () -> {
            for(net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel f : Core.project.getConfiguration(UnderhaulSFRConfiguration::new).fuels){
                if(f.stats.heat==heat&&f.stats.power==power)return f;
            }
            return null;
        }, false);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement recoverUnderhaulSFRBlock(String name){
        return recoverFallbackName("block", name, Core.project.getConfiguration(UnderhaulSFRConfiguration::new).blocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), "cooler", " ").equalsIgnoreCase(StringUtil.superRemove(name, " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipe(String name){
        return recoverFallbackName("recipe", name, Core.project.getConfiguration(OverhaulSFRConfiguration::new).coolantRecipes, null, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement recoverOverhaulSFRBlock(String name){
        return recoverFallbackName("block", name, Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "heatsink", "liquid", "moderator", "reflector", "neutronshield", "shield").equalsIgnoreCase(StringUtil.superRemove(StringUtil.toLowerCase(name), " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel recoverOverhaulSFRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, String name){
        return recoverFallbackName("fuel", name, block.fuels, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4), " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Oxide", " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Nitride", " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+"-Zirconium Alloy", " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe recoverOverhaulSFRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, String name){
        return recoverFallbackName("recipe", name, block.irradiatorRecipes, null, null, true);
    }
    
    
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement recoverOverhaulMSRBlock(String name){
        return recoverFallbackName("block", name, Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "coolant", "heater", "liquid", "moderator", "reflector", "neutronshield", "shield").equalsIgnoreCase(StringUtil.superReplace(StringUtil.toLowerCase(name), "water", "standard", " ", ""));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel recoverOverhaulMSRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, String name){
        return recoverFallbackName("fuel", name, block.fuels, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4), " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Fluoride", " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe recoverOverhaulMSRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, String name){
        return recoverFallbackName("recipe", name, block.irradiatorRecipes, null, null, true);//don't care about heater recipes
    }
}