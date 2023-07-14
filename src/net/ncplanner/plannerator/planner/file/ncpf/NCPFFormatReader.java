package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.InputStream;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public interface NCPFFormatReader{
    public NCPFObject read(InputStream stream);
}