package net.ncplanner.plannerator.planner.file;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import java.io.InputStream;
public interface FormatReader{
    public boolean formatMatches(InputStream stream);
    public LegacyNCPFFile read(InputStream stream, RecoveryHandler recovery);
}