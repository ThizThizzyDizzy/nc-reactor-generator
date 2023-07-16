package net.ncplanner.plannerator.planner.file;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import java.io.InputStream;
import net.ncplanner.plannerator.planner.ncpf.Project;
public interface FormatReader{
    public boolean formatMatches(InputStream stream);
    public Project read(InputStream stream, RecoveryHandler recovery);
}