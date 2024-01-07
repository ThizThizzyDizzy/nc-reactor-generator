package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFileWriter;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFormatWriter;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.ToggleBox;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class MenuSaveDialog extends MenuInputDialog{
    private final Component panel;
    private final ArrayList<ToggleBox> formatButtons = new ArrayList<>();
    public MenuSaveDialog(GUI gui, Menu parent){
        this(gui, parent, Core.project, null);
    }
    public MenuSaveDialog(GUI gui, Menu parent, Runnable onSaved){
        this(gui, parent, Core.project, onSaved);
    }
    private NCPFFormatWriter selectedFormat = NCPFFileWriter.formats.get(0);
    public MenuSaveDialog(GUI gui, Menu parent, Project ncpf, Runnable onSaved){
        super(gui, parent, genName(ncpf), "Filename");
        setContent(panel = new Component(0, 0, 384, 64+32));
        panel.add(inputField);
        float wide = 384f/NCPFFileWriter.formats.size();
        for(int i = 0; i<NCPFFileWriter.formats.size(); i++){
            NCPFFormatWriter format = NCPFFileWriter.formats.get(i);
            ToggleBox box = panel.add(new ToggleBox(i*wide, 64, wide, 32, getExtension(format)));
            box.isToggledOn = i==0;
            box.onChange((isOn) -> {//basically onClick
                for(ToggleBox b : formatButtons)b.isToggledOn = false;
                box.isToggledOn = true;
                selectedFormat = format;
            });
        }
        Core.project.designs.clear();
        for(Multiblock m : Core.multiblocks)Core.project.designs.add((Design)m.toDesign());
        addButton("Cancel", true);
        addButton("Save Dialog", (dialog, str) -> {
            try{
                Core.createFileChooser(null, (file) -> {
                    if(!file.getName().endsWith(getExtension()))file = new File(file.getAbsolutePath()+getExtension());
                    NCPFFileWriter.write(ncpf, file, selectedFormat);
                    Core.saved = true;
                    if(onSaved!=null)onSaved.run();
                }, new String[]{"ncpf."+selectedFormat.getExtension()});
            }catch(IOException ex){
                Core.error("Failed to save file!", ex);
            }
        }, true);
        addButton("Save", (dialog, filename) -> {
            if(filename==null||filename.isEmpty()){
                Core.warning("Invalid filename: "+filename+getExtension(), null);
            }else{
                Core.filename = filename;
                File file = new File(filename+getExtension());
                if(file.exists()){
                    new MenuMessageDialog(gui, dialog, "File "+filename+getExtension()+" already exists!\nOverwrite?").addButton("Cancel", true).addButton("Save", (d) -> {
                        save(ncpf, file, filename+getExtension());
                        if(onSaved!=null)onSaved.run();
                    }, true).open();
                }else{
                    save(ncpf, file, filename+getExtension());
                    if(onSaved!=null)onSaved.run();
                }
            }
        }, true);
    }
    private void save(Project ncpf, File file, String filename){
        NCPFFileWriter.write(ncpf, file, selectedFormat);
        new MenuOKMessageDialog(gui, this, "Saved as "+filename).open();
        Core.saved = true;
    }
    private static String genName(Project ncpf){
        String name = Core.filename;
        if(name==null) name = ncpf.metadata.metadata.get("name");
        if(name==null||name.isEmpty()){
            name = "unnamed";
            File file = new File(name+getExtension(NCPFFileWriter.formats.get(0)));
            int i = 0;
            while(file.exists()){
                name = "unnamed_"+i;
                file = new File(name+getExtension(NCPFFileWriter.formats.get(0)));
                i++;
            }
        }
        return name;
    }
    private String getExtension(){
        return getExtension(selectedFormat);
    }
    private static String getExtension(NCPFFormatWriter format){
        return ".ncpf."+format.getExtension();
    }
}