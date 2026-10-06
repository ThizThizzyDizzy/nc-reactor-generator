package net.ncplanner.plannerator.planner.file.recovery;
import net.ncplanner.ncpf.runtime.RuntimeNcpf;
import net.ncplanner.ncpf.structure.element.NcpfElement;
public interface RecoveryHandler{
    public default NcpfElement recoverUnderhaulSFRFuelLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverUnderhaulSFRBlockLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRCoolantRecipeLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRBlockLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulMSRBlockLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulMSRBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulTurbineRecipeLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulTurbineBlockLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulFusionCoolantRecipeLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulFusionRecipeLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulFusionBlockLegacyNCPF(RuntimeNcpf ncpf, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulFusionBlockRecipeLegacyNCPF(RuntimeNcpf ncpf, NcpfElement block, int id) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverUnderhaulSFRFuel(String name, Float heat, Float power) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverUnderhaulSFRBlock(String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRCoolantRecipe(String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRBlock(String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRFuel(NcpfElement block, String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulSFRBlockRecipe(NcpfElement block, String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulMSRBlock(String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulMSRFuel(NcpfElement block, String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public default NcpfElement recoverOverhaulMSRBlockRecipe(NcpfElement block, String name) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
}