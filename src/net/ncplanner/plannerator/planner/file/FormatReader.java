package net.ncplanner.plannerator.planner.file;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.ncpf.Project;
public interface FormatReader{
    public boolean formatMatches(Supplier<InputStream> stream);
    public Project read(Supplier<InputStream> stream, RecoveryHandler recovery);
}