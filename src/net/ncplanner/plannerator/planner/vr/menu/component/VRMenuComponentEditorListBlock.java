package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuComponentEditorListBlock extends VRMenuComponent implements Pinnable{
    private final VRMenuEdit editor;
    private final Block block;
    private final int id;
    private final int blockID;
    public VRMenuComponentEditorListBlock(VRMenuEdit editor, int id, float x, float y, float z, float size, Block block, int blockID){
        super(x, y, z, size, size, size, 0, 0, 0);
        this.editor = editor;
        this.block = block;
        this.id = id;
        this.blockID = blockID;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        renderer.drawCube(0, 0, 0, width, height, depth, block.getTexture());
        renderer.setColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedBlock(id).isEqual(block)){
            renderer.drawCubeOutline(-.0025f, -.0025f, -.0025f, width+.0025f, height+.0025f, depth+.0025f, .0025f);//2.5fmm
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                if(editor.isShiftPressed(device))Pinnable.togglePin(this);
                else editor.selectedBlock.put(id, blockID);
            }
        }
    }
    @Override
    public String getTooltip(int device){
        return block.getListTooltip();
    }
    @Override
    public ArrayList<String> getSearchableNames(){
        return block.getSearchableNames();
    }
    @Override
    public ArrayList<String> getSimpleSearchableNames(){
        return block.getSimpleSearchableNames();
    }
    @Override
    public String getPinnedName(){
        return block.getPinnedName();
    }
}