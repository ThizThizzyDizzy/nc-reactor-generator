package net.ncplanner.plannerator.planner.module;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.LiteUnderhaulSFR;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.ClearInvalidMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.random.RandomBlockMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.underhaulSFR.mutators.random.RandomFuelMutator;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.Configuration;
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
    public void addConfigurations(Task task){
        task.addSubtask("PO3");
        task.addSubtask("E2E");
        addConfiguration(new Configuration(FileReader.read(() -> Core.getInputStream("configurations/project_ozone_3.ncpf.json"))).addAlternative("PO3"), "https://www.curseforge.com/minecraft/modpacks/project-ozone-3-a-new-way-forward", "TheCazadorSniper");
        task.getCurrentSubtask().finish();
        addConfiguration(new Configuration(FileReader.read(() -> Core.getInputStream("configurations/enigmatica_2_expert.ncpf.json"))).addAlternative("E2E"), "https://www.curseforge.com/minecraft/modpacks/enigmatica2expert", "NillerMedDild");
        task.getCurrentSubtask().finish();
    }
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> activeModeratorOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block>("Active Moderator", "Highlights active moderators with a green outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block, Multiblock<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> multiblock){
            if(block.isActive()&&block.isModerator()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineActive());
            }
        }
    };
    private final EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> activeCoolerRecipeOverlay = new EditorOverlay<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block>("Active Cooler Recipes", "Shows the chosen recipe on active coolers", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block, Multiblock<net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block> multiblock){
            net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block b = (net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block)block;
            if(b.recipe!=null&&b.template.getBlockRecipes().size()>1){
                renderer.setWhite(.75f);
                renderer.drawImage(b.recipe.getDisplayTexture(), x+width*.125f, y+height*.125f, x+width*.875f, y+height*.875f);
            }
        }
    };
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList overlays){
        if(multiblock instanceof UnderhaulSFR){
            overlays.add(activeModeratorOverlay);
            overlays.add(activeCoolerRecipeOverlay);
        }
    }
    @Override
    public void getGenerators(LiteMultiblock multiblock, ArrayList<Supplier<InputStream>> generators){
        if(multiblock instanceof LiteUnderhaulSFR){
            generators.add(()-> Core.getInputStream("configurations/generators/underhaul_sfr/output.ncpf.json"));
            generators.add(()-> Core.getInputStream("configurations/generators/underhaul_sfr/efficiency.ncpf.json"));
        }
    }
}