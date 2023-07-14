package net.ncplanner.plannerator.planner.file.recovery;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.Project;
public interface RecoveryHandler{
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuelLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block recoverUnderhaulSFRBlockLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block recoverOverhaulSFRBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulSFRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block recoverOverhaulMSRBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulMSRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe recoverOverhaulTurbineRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block recoverOverhaulTurbineBlockLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe recoverOverhaulFusionCoolantRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe recoverOverhaulFusionRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block recoverOverhaulFusionBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulFusionBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuel(String name, Float heat, Float power);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block recoverUnderhaulSFRBlock(String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipe(String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block recoverOverhaulSFRBlock(String name);
    public <T extends NCPFElement> T recoverOverhaulSFRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block block, String name);
    public <T extends NCPFElement> T recoverOverhaulSFRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block block, String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block recoverOverhaulMSRBlock(String name);
    public <T extends NCPFElement> T recoverOverhaulMSRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block block, String name);
    public <T extends NCPFElement> T recoverOverhaulMSRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block block, String name);
}