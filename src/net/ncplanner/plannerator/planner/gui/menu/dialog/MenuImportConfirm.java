package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.List;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class MenuImportConfirm extends MenuDialog{
    public MenuImportConfirm(GUI gui, Menu parent, List<Project> projects, Runnable onImport){
        super(gui, parent);
        int total = 0;
        for(Project p : projects)total+=p.designs.size();
        textBox.addText(total+" Designs:");
        for(Project p : projects){
            for(Design d : p.designs){
                textBox.addText("\n"+d.definition.type);
                if(!p.isConfigEmpty())textBox.addText(" (Conversion required)");
            }
        }
        addButton("Import", () -> {
            for(Project p : projects){
                for(Design d : p.designs){
                    if(d instanceof MultiblockDesign){
                        if(!p.isConfigEmpty())d.file = Core.project;
                        MultiblockDesign design = (MultiblockDesign)d; 
                        if(!p.isConfigEmpty())((MultiblockDesign)d).convertElements();
                        Core.multiblocks.add(design.toMultiblock());
                    }
                }
            }
            onImport.run();
        }, true);
        addButton("Cancel");
    }
    @Override
    public void onOpened(){
        super.onOpened();
        buttons.get(0).runActions();//automatically press import since this menu isn't otherwise very useful
    }
}