package net.ncplanner.plannerator.planner.file;
import java.io.OutputStream;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.ncpf.Project;
public abstract class FormatWriter{
    public abstract FileFormat getFileFormat();
//    public abstract String getName();
//    public abstract String[] getExtensions();
    public abstract void write(Project ncpf, OutputStream stream);
//    public String getDesc(){
//        String ext = "";
//        for(String e : getExtensions())ext+=", ."+e;
//        return getName()+(ext.isEmpty()?"":(" ("+ext.substring(2)+")"));
//    }
    public abstract boolean isMultiblockSupported(Multiblock multi);
    public void openExportSettings(Project ncpf, Runnable onExport){
        onExport.run();
    }
}