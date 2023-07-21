package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFileWriter;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuSaveDialog extends MenuInputDialog{
    public MenuSaveDialog(GUI gui, Menu parent){
        this(gui, parent, Core.project, null);
    }
    public MenuSaveDialog(GUI gui, Menu parent, Runnable onSaved){
        this(gui, parent, Core.project, onSaved);
    }
    public MenuSaveDialog(GUI gui, Menu parent, Project ncpf, Runnable onSaved){
        super(gui, parent, genName(ncpf), "Filename");
        Core.project.designs.clear();
        for(Multiblock m : Core.multiblocks)Core.project.designs.add(m.toDesign());
        addButton("Cancel", true);
        addButton("Save Dialog", (dialog, str) -> {
            try{
                Core.createFileChooser(null, (file) -> {
                    if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                    NCPFFileWriter.write(ncpf, file, NCPFFileWriter.JSON);
                    Core.saved = true;
                    if(onSaved!=null)onSaved.run();
                }, FileFormat.LEGACY_NCPF);
            }catch(IOException ex){
                Core.error("Failed to save file!", ex);
            }
        }, true);
        addButton("Save", (dialog, filename) -> {
            if(filename==null||filename.isEmpty()){
                Core.warning("Invalid filename: "+filename+".ncpf", null);
            }else{
                Core.filename = filename;
                File file = new File(filename+".ncpf");
                if(file.exists()){
                    new MenuMessageDialog(gui, dialog, "File "+filename+".ncpf already exists!\nOverwrite?").addButton("Cancel", true).addButton("Save", (d) -> {
                        save(ncpf, file, filename+".ncpf");
                        if(onSaved!=null)onSaved.run();
                    }, true).open();
                }else{
                    save(ncpf, file, filename+".ncpf");
                    if(onSaved!=null)onSaved.run();
                }
            }
        }).open();
    }
    private void save(Project ncpf, File file, String filename){
        NCPFFileWriter.write(ncpf, file, NCPFFileWriter.JSON);
        new MenuOKMessageDialog(gui, this, "Saved as "+filename).open();
        Core.saved = true;
    }
    private static String genName(Project ncpf){
        String name = Core.filename;
        if(name==null) name = ncpf.metadata.metadata.get("name");
        if(name==null||name.isEmpty()){
            name = "unnamed";
            File file = new File(name+".ncpf");
            int i = 0;
            while(file.exists()){
                name = "unnamed_"+i;
                file = new File(name+".ncpf");
                i++;
            }
        }
        return name;
    }
}