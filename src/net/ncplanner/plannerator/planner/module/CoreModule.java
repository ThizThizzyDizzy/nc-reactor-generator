package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.tutorial.Tutorial;
import net.ncplanner.plannerator.planner.tutorial.TutorialFileReader;
public class CoreModule<T> extends Module<T>{
    public CoreModule(){
        super("core", true);
    }
    @Override
    public String getDisplayName(){
        return "Core";
    }
    @Override
    public String getDescription(){
        return "Contains general features appliccable to all multiblocks";
    }
    @Override
    public void addTutorials(){
        Tutorial.addTutorials("Planner",
                TutorialFileReader.read("tutorials/planner/basics.ncpt"),
                TutorialFileReader.read("tutorials/planner/editing.ncpt"));
    }
    private final EditorOverlay invalidBlocksOverlay = new EditorOverlay("Invalid Blocks", "Highlights invalid blocks with a red outline", true){
        @Override
        public void render(Renderer renderer, float x, float y, float width, float height, Block block, Multiblock multiblock){
            if(!block.isValid()){
                block.drawOutline(renderer, x, y, width, height, Core.theme.getBlockColorOutlineInvalid());
            }
        }
    };
    @Override
    public void getEditorOverlays(Multiblock multiblock, ArrayList<EditorOverlay> overlays){
        overlays.add(invalidBlocksOverlay);
    }
}