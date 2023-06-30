package net.ncplanner.plannerator.planner.file.recovery;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.configuration.ThingWithLegacyNames;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
public class NonRecoveryHandler implements RecoveryHandler{
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel recoverUnderhaulSFRFuelNCPF(LegacyNCPFFile ncpf, int id) {
        return recoverFallbackID("fuel", id, ncpf.configuration.underhaul.fissionSFR.allFuels, Core.configuration.underhaul.fissionSFR.allFuels, false);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block recoverUnderhaulSFRBlockNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("block", id, ncpf.configuration.underhaul.fissionSFR.allBlocks, Core.configuration.underhaul.fissionSFR.allBlocks, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recoverOverhaulSFRCoolantRecipeNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("coolant recipe", id, ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes, Core.configuration.overhaul.fissionSFR.allCoolantRecipes, false);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block recoverOverhaulSFRBlockNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("block", id, ncpf.configuration.overhaul.fissionSFR.allBlocks, Core.configuration.overhaul.fissionSFR.allBlocks, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRBlockRecipeNCPF(LegacyNCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, int id) {
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe> fallbackList = null;
        try{
            fallbackList = Core.configuration.overhaul.fissionSFR.convert(block).allRecipes;
        }catch(MissingConfigurationEntryException ex){}
        return recoverFallbackID(block.fuelCell?"fuel":"recipe", id, block.allRecipes, fallbackList, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block recoverOverhaulMSRBlockNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("block", id, ncpf.configuration.overhaul.fissionMSR.allBlocks, Core.configuration.overhaul.fissionMSR.allBlocks, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRBlockRecipeNCPF(LegacyNCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, int id) {
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe> fallbackList = null;
        try{
            fallbackList = Core.configuration.overhaul.fissionMSR.convert(block).allRecipes;
        }catch(MissingConfigurationEntryException ex){}
        return recoverFallbackID(block.fuelVessel?"fuel":"recipe", id, block.allRecipes, fallbackList, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe recoverOverhaulTurbineRecipeNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("recipe", id, ncpf.configuration.overhaul.turbine.allRecipes, Core.configuration.overhaul.turbine.allRecipes, false);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block recoverOverhaulTurbineBlockNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("block", id, ncpf.configuration.overhaul.turbine.allBlocks, Core.configuration.overhaul.turbine.allBlocks, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe recoverOverhaulFusionCoolantRecipeNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("coolant recipe", id, ncpf.configuration.overhaul.fusion.allCoolantRecipes, Core.configuration.overhaul.fusion.allCoolantRecipes, false);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block recoverOverhaulFusionBlockNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("block", id, ncpf.configuration.overhaul.fusion.allBlocks, Core.configuration.overhaul.fusion.allBlocks, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe recoverOverhaulFusionBlockRecipeNCPF(LegacyNCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block block, int id) {
        ArrayList<net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe> fallbackList = null;
        try{
            fallbackList = Core.configuration.overhaul.fusion.convert(block).allRecipes;
        }catch(MissingConfigurationEntryException ex){}
        return recoverFallbackID("recipe", id, block.allRecipes, fallbackList, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe recoverOverhaulFusionRecipeNCPF(LegacyNCPFFile ncpf, int id){
        return recoverFallbackID("recipe", id, ncpf.configuration.overhaul.fusion.allRecipes, Core.configuration.overhaul.fusion.allRecipes, false);
    }
    private <T> T recoverFallbackID(String type, int idx, ArrayList<T> list, ArrayList<T> fallbackList, boolean allowNull){
        if(idx>=0&&idx<list.size())return list.get(idx);
        throw new IllegalArgumentException("Invalid "+type+" index: "+idx+"!");
    }
    private <T extends ThingWithLegacyNames> T recoverFallbackName(String type, String name, ArrayList<T> list, Function<String, Boolean> nameProcessor, Supplier<T> recoverFunc, boolean allowNull){
        if(nameProcessor==null)nameProcessor = (nam)->{
            return nam.equalsIgnoreCase(name);
        };
        for(T t : list){
            for(String legacy : t.getLegacyNames()){
                if(nameProcessor.apply(legacy))return t;
            }
        }
        throw new IllegalArgumentException("Invalid "+type+" name: "+name+"!");
    }
    private String cap(String s){
        return s.substring(0, 1).toUpperCase(Locale.ROOT)+s.substring(1);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel recoverUnderhaulSFRFuel(String name, Float heat, Float power){
        return recoverFallbackName("fuel", name, Core.configuration.underhaul.fissionSFR.allFuels, null, () -> {
            for(net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel f : Core.configuration.underhaul.fissionSFR.allFuels){
                if(f.heat==heat&&f.power==power)return f;
            }
            return null;
        }, false);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block recoverUnderhaulSFRBlock(String name){
        return recoverFallbackName("block", name, Core.configuration.underhaul.fissionSFR.allBlocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), "cooler", " ").equalsIgnoreCase(StringUtil.superRemove(name, " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recoverOverhaulSFRCoolantRecipe(String name){
        return recoverFallbackName("recipe", name, Core.configuration.overhaul.fissionSFR.allCoolantRecipes, null, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block recoverOverhaulSFRBlock(String name){
        return recoverFallbackName("block", name, Core.configuration.overhaul.fissionSFR.allBlocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "heatsink", "liquid", "moderator", "reflector", "neutronshield", "shield").equalsIgnoreCase(StringUtil.superRemove(StringUtil.toLowerCase(name), " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRFuel(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, String name){
        return recoverFallbackName("fuel", name, block.allRecipes, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4), " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Oxide", " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Nitride", " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+"-Zirconium Alloy", " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRBlockRecipe(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, String name){
        return recoverFallbackName("recipe", name, block.allRecipes, null, null, true);
    }
    
    
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block recoverOverhaulMSRBlock(String name){
        return recoverFallbackName("block", name, Core.configuration.overhaul.fissionMSR.allBlocks, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "coolant", "heater", "liquid", "moderator", "reflector", "neutronshield", "shield").equalsIgnoreCase(StringUtil.superReplace(StringUtil.toLowerCase(name), "water", "standard", " ", ""));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRFuel(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, String name){
        return recoverFallbackName("fuel", name, block.allRecipes, (nam) -> {
            return StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4), " "))
                    ||StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(name.substring(4)+" Fluoride", " "));
        }, null, true);
    }
    @Override
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRBlockRecipe(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, String name){
        return recoverFallbackName("recipe", name, block.allRecipes, null, null, true);
    }
}