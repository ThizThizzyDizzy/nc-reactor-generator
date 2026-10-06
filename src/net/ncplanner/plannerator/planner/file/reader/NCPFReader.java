package net.ncplanner.plannerator.planner.file.reader;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.runtime.RuntimeNcpf;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class NCPFReader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> provider){
        return true;//no clue actually, but this is the last one in the list, and this is the only way to let it load it
    }
    @Override
    public RuntimeNcpf read(Supplier<InputStream> provider, RecoveryHandler recovery, File fileContext){
        return NcpfJsonConverter.parseJson(provider.get()).toRuntime();
    }
}