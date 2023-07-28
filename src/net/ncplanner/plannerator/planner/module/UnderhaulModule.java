package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.ClearInvalidMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random.RandomBlockMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr.mutators.random.RandomFuelMutator;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.configuration.NCPFUnderhaulSFRConfiguration;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.module.UnderhaulSFRSettingsModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.*;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class UnderhaulModule extends Module<Object>{
    public UnderhaulModule(){
        super("underhaul", true);
    }
    @Override
    public String getDisplayName(){
        return "Underhaul";
    }
    @Override
    public String getDescription(){
        return "All the base NuclearCraft multiblocks";
    }
    @Override
    public void addMultiblockTypes(ArrayList multiblockTypes){
        multiblockTypes.add(new UnderhaulSFR());
    }
    @Override
    public void registerNCPF(){
        registerNCPFConfiguration(NCPFUnderhaulSFRConfiguration::new);
        registerNCPFConfiguration(UnderhaulSFRConfiguration::new);
        registerNCPFDesign(NCPFUnderhaulSFRDesign::new, UnderhaulSFRDesign::new);
        registerNCPFModule(UnderhaulSFRSettingsModule::new);
        registerNCPFModule(FuelStatsModule::new);
        registerNCPFModule(CoolerModule::new);
        registerNCPFModule(ActiveCoolerModule::new);
        registerNCPFModule(FuelCellModule::new);
        registerNCPFModule(ModeratorModule::new);
        registerNCPFModule(CasingModule::new);
        registerNCPFModule(ControllerModule::new);
        
        registerMutator(RandomBlockMutator::new);
        registerMutator(RandomFuelMutator::new);
        registerMutator(ClearInvalidMutator::new);
    }
    @Override
    public void addTutorials(){
        Tutorial.addTutorials("Underhaul",
                TutorialFileReader.read("tutorials/underhaul/sfr.ncpt"));
    }
    @Override
    public void addConfigurations(){
//        addConfiguration(FileReader.read(() -> {
//            return Core.getInputStream("configurations/po3.ncpf");
//        }).configuration);
//        addConfiguration(FileReader.read(() -> {
//            return Core.getInputStream("configurations/e2e.ncpf");
//        }).configuration);
    }
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> activeModeratorOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block>("Active Moderator", "Highlights active moderators with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block, Multiblock<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> multiblock){
            if(block.isActive()&&block.isModerator()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList overlays){
        if(multiblock instanceof UnderhaulSFR)overlays.add(activeModeratorOverlay);
    }
}