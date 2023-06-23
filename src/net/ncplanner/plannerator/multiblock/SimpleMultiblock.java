package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.ppe.PostProcessingEffect;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
import net.ncplanner.plannerator.multiblock.generator.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
public abstract class SimpleMultiblock<T extends SimpleBlock> extends Multiblock<T>{
    public SimpleMultiblock(Configuration configuration, int... dimensions){
        super(configuration, dimensions);
    }
    @Override
    public int getMultiblockID(){
        return -1;//not for NCPF
    }
    @Override
    public void convertTo(Configuration to) throws MissingConfigurationEntryException{}
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public void addGeneratorSettings(SingleColumnList multiblockSettings){}
    @Override
    public void getGenerationPriorities(ArrayList<Priority> priorities){}
    @Override
    public void getGenerationPriorityPresets(ArrayList<Priority> priorities, ArrayList<Priority.Preset> presets){}
    @Override
    public void getSymmetries(ArrayList<Symmetry> symmetries){}
    @Override
    public void getPostProcessingEffects(ArrayList<PostProcessingEffect> postProcessingEffects){}
    @Override
    public Multiblock<T> blankCopy(){
        return newInstance(configuration, dimensions);
    }
    @Override
    protected int doCount(Object o){
        return -1;
    }
    @Override
    public String getGeneralName(){
        return getDefinitionName();
    }
    @Override
    protected boolean isCompatible(Multiblock<T> other){
        return other.getDefinitionName()==getDefinitionName();
    }
    @Override
    protected void getFluidOutputs(ArrayList<FluidStack> outputs){}
    @Override
    protected void getExtraParts(ArrayList<PartCount> parts){}
    @Override
    public void openVRResizeMenu(VRGUI gui, VRMenuEdit editor){}
    @Override
    public void getSuggestors(ArrayList<Suggestor> suggestors) {}
    @Override
    public String getPreviewTexture(){
        return null;
    }
    @Override
    public <T extends LiteMultiblock> T compile(){
        return null;
    }
}