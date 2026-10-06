package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.ncpf.runtime.RuntimeNcpf;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public interface FormatReader{
    public boolean formatMatches(Supplier<InputStream> stream);
    public default RuntimeNcpf read(Supplier<InputStream> stream, RecoveryHandler recovery, File fileContext) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
}