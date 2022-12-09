package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class UnderhaulModule extends Module{
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
    public void addTutorials(){
        Tutorial.addTutorials("Underhaul",
                TutorialFileReader.read("tutorials/underhaul/sfr.ncpt"));
    }
    @Override
    public void addConfigurations(){
        addConfiguration(FileReader.read(() -> {
            return Core.getInputStream("configurations/po3.ncpf");
        }).configuration.addAlternative("PO3"));
        addConfiguration(FileReader.read(() -> {
            return Core.getInputStream("configurations/e2e.ncpf");
        }).configuration.addAlternative("E2E"));
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