package net.ncplanner.plannerator.planner.file.recovery;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.Project;
public interface RecoveryHandler{
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuelLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement recoverUnderhaulSFRBlockLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement recoverOverhaulSFRBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulSFRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement recoverOverhaulMSRBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulMSRBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe recoverOverhaulTurbineRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement recoverOverhaulTurbineBlockLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe recoverOverhaulFusionCoolantRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe recoverOverhaulFusionRecipeLegacyNCPF(Project ncpf, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement recoverOverhaulFusionBlockLegacyNCPF(Project ncpf, int id);
    public <T extends NCPFElement> T recoverOverhaulFusionBlockRecipeLegacyNCPF(Project ncpf, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement block, int id);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel recoverUnderhaulSFRFuel(String name, Float heat, Float power);
    public net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement recoverUnderhaulSFRBlock(String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recoverOverhaulSFRCoolantRecipe(String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement recoverOverhaulSFRBlock(String name);
    public <T extends NCPFElement> T recoverOverhaulSFRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, String name);
    public <T extends NCPFElement> T recoverOverhaulSFRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block, String name);
    public net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement recoverOverhaulMSRBlock(String name);
    public <T extends NCPFElement> T recoverOverhaulMSRFuel(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, String name);
    public <T extends NCPFElement> T recoverOverhaulMSRBlockRecipe(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block, String name);
}