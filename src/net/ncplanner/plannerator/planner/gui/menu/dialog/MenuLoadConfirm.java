package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.Arrays;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class MenuLoadConfirm extends MenuDialog{
    boolean requireConfirm = false;
    public MenuLoadConfirm(GUI gui, Menu parent, Project project, Runnable onLoad){
        super(gui, parent);
        textBox.addText("");
        if(project.isConfigEmpty()){
            requireConfirm = true;
            textBox.addText("\nConfiguration is Empty!");
        }else{
            project.withConfiguration(UnderhaulSFRConfiguration::new, (config)->{
                if(config.settings==null){
                    requireConfirm = true;
                    textBox.addText("\nUnderhaul SFR Configuration is incomplete!");
                }
            });
            project.withConfiguration(OverhaulSFRConfiguration::new, (config)->{
                if(config.settings==null){
                    requireConfirm = true;
                    textBox.addText("\nOverhaul SFR Configuration is incomplete!");
                }
            });
            project.withConfiguration(OverhaulMSRConfiguration::new, (config)->{
                if(config.settings==null){
                    requireConfirm = true;
                    textBox.addText("\nOverhaul MSR Configuration is incomplete!");
                }
            });
            project.withConfiguration(OverhaulTurbineConfiguration::new, (config)->{
                if(config.settings==null){
                    requireConfirm = true;
                    textBox.addText("\nOverhaul Turbine Configuration is incomplete!");
                }
            });
            project.withConfiguration(OverhaulFusionConfiguration::new, (config)->{
                if(config.settings==null){
                    requireConfirm = true;
                    textBox.addText("\nOverhaul Fusion Configuration is incomplete!");
                }
            });
        }
        textBox.addText("\nImport instead?");
        addButton("Load Anyway", () -> {
            Core.multiblocks.clear();
            Core.saved = true;
            Core.project = project;//just overwrite the whole thing, it's fine
            for(Design d : project.designs){
                if(d instanceof MultiblockDesign){
                    Core.multiblocks.add(((MultiblockDesign)d).toMultiblock());
                }
            }
            onLoad.run();
        }, true);
        addButton("Import", () -> {
            new MenuImportConfirm(gui, parent, Arrays.asList(project), onLoad).open();
        }, true);
        addButton("Cancel");
    }
    @Override
    public void onOpened(){
        super.onOpened();
        if(!requireConfirm)buttons.get(0).runActions();
    }
}