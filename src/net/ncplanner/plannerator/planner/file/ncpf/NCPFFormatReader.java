package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.InputStream;
import net.ncplanner.ncpf.structure.NcpfRoot;
public interface NCPFFormatReader{
    public NcpfRoot read(InputStream stream);
}
