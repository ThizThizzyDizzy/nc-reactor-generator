package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.IOException;
import java.io.OutputStream;
import net.ncplanner.ncpf.structure.NcpfRoot;
public interface NCPFFormatWriter{
    public void write(NcpfRoot ncpf, OutputStream stream) throws IOException;
    public String getExtension();
}
