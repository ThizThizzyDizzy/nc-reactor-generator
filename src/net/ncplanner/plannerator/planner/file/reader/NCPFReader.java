package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFileReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFReader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> provider){
        return true;//no clue actually, but this is the last one in the list, and this is the only way to let it load it
    }
    @Override
    public Project read(Supplier<InputStream> provider, RecoveryHandler recovery){
        return NCPFFileReader.read(provider);
    }
}