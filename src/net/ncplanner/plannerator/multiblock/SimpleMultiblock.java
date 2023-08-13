package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestor;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
public abstract class SimpleMultiblock<T extends SimpleBlock> extends Multiblock<T>{
    public SimpleMultiblock(NCPFConfigurationContainer configuration, int... dimensions){
        super(configuration, dimensions);
    }
    @Override
    public boolean validate(){
        return false;
    }
    @Override
    public Multiblock<T> blankCopy(){
        return newInstance(configuration, dimensions);
    }
    @Override
    public String getGeneralName(){
        return getDefinitionName();
    }
    @Override
    protected boolean isCompatible(Multiblock<T> other){
        return other.getDefinitionName().equals(getDefinitionName());
    }
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