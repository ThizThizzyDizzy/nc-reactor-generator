package net.ncplanner.plannerator.planner.module;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFBlockTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFFluidTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFItemTagElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
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
    public void registerNCPF(){
        registerNCPFElement("legacy_block", NCPFLegacyBlockElement::new);
        registerNCPFElement("legacy_item", NCPFLegacyItemElement::new);
        registerNCPFElement("legacy_fluid", NCPFLegacyFluidElement::new);
        registerNCPFElement("oredict", NCPFOredictElement::new);
        registerNCPFElement("block", NCPFBlockElement::new);
        registerNCPFElement("item", NCPFItemElement::new);
        registerNCPFElement("fluid", NCPFFluidElement::new);
        registerNCPFElement("block_tag", NCPFBlockTagElement::new);
        registerNCPFElement("item_tag", NCPFItemTagElement::new);
        registerNCPFElement("fluid_tag", NCPFFluidTagElement::new);
        registerNCPFModule(NCPFBlockRecipesModule::new);

        registerNCPFModule(MetadataModule::new);
        registerNCPFModule(ConfigurationMetadataModule::new);
        registerNCPFModule(DisplayNamesModule::new);
        registerNCPFModule(TextureModule::new);
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