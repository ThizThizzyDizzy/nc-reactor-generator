package net.ncplanner.plannerator.planner.file.recovery;
import net.ncplanner.ncpf.runtime.RuntimeNcpf;
import net.ncplanner.ncpf.structure.element.NcpfElement;
public interface RecoveryHandler{
    public NcpfElement recoverUnderhaulSFRFuelLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverUnderhaulSFRBlockLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulSFRCoolantRecipeLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulSFRBlockLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulSFRBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id);
    public NcpfElement recoverOverhaulMSRBlockLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulMSRBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id);
    public NcpfElement recoverOverhaulTurbineRecipeLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulTurbineBlockLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulFusionCoolantRecipeLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulFusionRecipeLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulFusionBlockLegacyNCPF(RuntimeNcpf ncpf, int id);
    public NcpfElement recoverOverhaulFusionBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id);
    public NcpfElement recoverUnderhaulSFRFuel(String name, Float heat, Float power);
    public NcpfElement recoverUnderhaulSFRBlock(String name);
    public NcpfElement recoverOverhaulSFRCoolantRecipe(String name);
    public NcpfElement recoverOverhaulSFRBlock(String name);
    public NcpfElement recoverOverhaulSFRFuel(NcpfElement block, String name);
    public NcpfElement recoverOverhaulSFRBlockRecipe(NcpfElement block, String name);
    public NcpfElement recoverOverhaulMSRBlock(String name);
    public NcpfElement recoverOverhaulMSRFuel(NcpfElement block, String name);
    public NcpfElement recoverOverhaulMSRBlockRecipe(NcpfElement block, String name);
}