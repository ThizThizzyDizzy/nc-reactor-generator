package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.Core;
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
    public void write(Project ncpf, OutputStream stream){
        ncpf = ncpf.copyTo(Project::new);
        ncpf.makePartial();
        NCPFObject obj = new NCPFObject();
        ncpf.convertToObject(obj);
        trimPlanneratorModules(obj);
        try{
            format.write(obj, stream);
        }catch(IOException ex){
            Core.error("Failed to write NCPF file!", ex);
        }
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