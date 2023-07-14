package net.ncplanner.plannerator.planner.file;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import java.io.InputStream;
import net.ncplanner.plannerator.ncpf.NCPFFile;
public interface FormatReader{
    public boolean formatMatches(InputStream stream);
    public NCPFFile read(InputStream stream, RecoveryHandler recovery);
}