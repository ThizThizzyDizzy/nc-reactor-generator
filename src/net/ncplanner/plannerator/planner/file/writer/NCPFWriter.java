package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFormatWriter;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFWriter extends FormatWriter{
    public static NCPFFormatWriter format;
    @Override
    public FileFormat getFileFormat(){
        return null;
    }
    @Override
    public String[] getExtensions(){
        return new String[]{format.getExtension()};
    }
    @Override
    public void write(Project ncpf, OutputStream stream){
        net.ncplanner.ncpf.structure.NcpfRoot root = prepare(ncpf);
        try{
            format.write(root, stream);
        }catch(IOException ex){
            throw new java.io.UncheckedIOException("Failed to write NCPF file!", ex);
        }
    }
    public net.ncplanner.ncpf.structure.NcpfRoot prepare(Project project){
        Project copy = project.copyTo(Project::new);
        copy.makePartial();
        NCPFObject obj = new NCPFObject();
        copy.convertToObject(obj);
        trimPlanneratorModules(obj);
        return net.ncplanner.plannerator.planner.file.ncpf.NcpfBridge.toNcpfRoot(obj);
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
    private void trimPlanneratorModules(NCPFObject ncpf){
        if(ncpf.containsKey("modules")){
            if(trimModules(ncpf.getNCPFObject("modules")))ncpf.remove("modules");
        }
        ncpf.forEach((key, val) -> {
            if(val instanceof NCPFObject){
                trimPlanneratorModules((NCPFObject)val);
            }
            if(val instanceof NCPFList){
                trimPlanneratorModules((NCPFList)val);
            }
        });
    }
    private void trimPlanneratorModules(NCPFList ncpf){
        ncpf.forEach((val) -> {
            if(val instanceof NCPFObject){
                trimPlanneratorModules((NCPFObject)val);
            }
            if(val instanceof NCPFList){
                trimPlanneratorModules((NCPFList)val);
            }
        });
    }
    //returns true if it's empty
    private boolean trimModules(NCPFObject ncpf){
        for(Iterator<String> it = ncpf.keySet().iterator(); it.hasNext();){
            String key = it.next();
            if(!key.startsWith("ncpf:"))it.remove();
        }
        return ncpf.isEmpty();
    }
}