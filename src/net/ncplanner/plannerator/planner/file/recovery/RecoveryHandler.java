package net.ncplanner.plannerator.planner.file.recovery;
import net.ncplanner.plannerator.planner.file.NCPFFile;
public interface RecoveryHandler{
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel recoverUnderhaulSFRFuelNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block recoverUnderhaulSFRBlockNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recoverOverhaulSFRCoolantRecipeNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block recoverOverhaulSFRBlockNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRBlockRecipeNCPF(NCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block recoverOverhaulMSRBlockNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRBlockRecipeNCPF(NCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe recoverOverhaulTurbineRecipeNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Block recoverOverhaulTurbineBlockNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.CoolantRecipe recoverOverhaulFusionCoolantRecipeNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe recoverOverhaulFusionRecipeNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block recoverOverhaulFusionBlockNCPF(NCPFFile ncpf, int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe recoverOverhaulFusionBlockRecipeNCPF(NCPFFile ncpf, net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Block block, int id);
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel recoverUnderhaulSFRFuel(String name, Float heat, Float power);
    public net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block recoverUnderhaulSFRBlock(String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe recoverOverhaulSFRCoolantRecipe(String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block recoverOverhaulSFRBlock(String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRFuel(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe recoverOverhaulSFRBlockRecipe(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block, String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block recoverOverhaulMSRBlock(String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRFuel(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, String name);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe recoverOverhaulMSRBlockRecipe(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block, String name);
}