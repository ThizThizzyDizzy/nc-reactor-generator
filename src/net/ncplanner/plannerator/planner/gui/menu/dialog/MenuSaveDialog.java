package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import net.ncplanner.plannerator.multiblock.configuration.PartialConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
public class MenuSaveDialog extends MenuInputDialog{
    public MenuSaveDialog(GUI gui, Menu parent){
        this(gui, parent, genNCPF(), null);
    }
    public MenuSaveDialog(GUI gui, Menu parent, Runnable onSaved){
        this(gui, parent, genNCPF(), onSaved);
    }
    public MenuSaveDialog(GUI gui, Menu parent, NCPFFile ncpf, Runnable onSaved){
        super(gui, parent, genName(ncpf), "Filename");
        addButton("Cancel", true);
        addButton("Save Dialog", (dialog, str) -> {
            try{
                Core.createFileChooser(null, (file) -> {
                    if(!file.getName().endsWith(".ncpf"))file = new File(file.getAbsolutePath()+".ncpf");
                    FileWriter.write(ncpf, file, FileWriter.NCPF);
                    Core.saved = true;
                    if(onSaved!=null)onSaved.run();
                }, FileFormat.NCPF);
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
    private void save(NCPFFile ncpf, File file, String filename){
        FileWriter.write(ncpf, file, FileWriter.NCPF);
        new MenuOKMessageDialog(gui, this, "Saved as "+filename).open();
        Core.saved = true;
    }
    private static NCPFFile genNCPF(){
        NCPFFile ncpf = new NCPFFile();
        ncpf.configuration = PartialConfiguration.generate(Core.configuration, Core.multiblocks);
        ncpf.multiblocks.addAll(Core.multiblocks);
        ncpf.metadata.putAll(Core.metadata);
        return ncpf;
    }
    private static String genName(NCPFFile ncpf){
        String name = Core.filename;
        if(name==null) name = ncpf.metadata.get("name");
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