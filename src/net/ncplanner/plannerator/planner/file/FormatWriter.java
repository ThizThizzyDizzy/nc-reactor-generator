package net.ncplanner.plannerator.planner.file;
import java.io.OutputStream;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.ncpf.Project;
public abstract class FormatWriter{
    public abstract FileFormat getFileFormat();
    public abstract void write(Project ncpf, OutputStream stream);
    public abstract boolean isMultiblockSupported(Multiblock multi);
    public void openExportSettings(Project ncpf, Runnable onExport){
        onExport.run();
    }
}