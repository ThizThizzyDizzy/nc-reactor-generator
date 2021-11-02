package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.Pinnable;
import net.ncplanner.plannerator.planner.vr.VRCore;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
public class VRMenuComponentEditorListBlock extends VRMenuComponent implements Pinnable{
    private final VRMenuEdit editor;
    private final Block block;
    private final int id;
    private final int blockID;
    public VRMenuComponentEditorListBlock(VRMenuEdit editor, int id, double x, double y, double z, double size, Block block, int blockID){
        super(x, y, z, size, size, size, 0, 0, 0);
        this.editor = editor;
        this.block = block;
        this.id = id;
        this.blockID = blockID;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        renderer.drawCube(0, 0, 0, width, height, depth, Core.getTexture(block.getTexture()));
        renderer.setColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedBlock(id).isEqual(block)){
            renderer.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
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