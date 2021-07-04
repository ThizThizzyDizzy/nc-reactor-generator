package planner.vr.menu.component;
import multiblock.Block;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
public class VRMenuComponentEditorListBlock extends VRMenuComponent{
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
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        VRCore.drawCube(0, 0, 0, width, height, depth, Core.getTexture(block.getTexture()));
        Core.applyColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedBlock(id).isEqual(block)){
            VRCore.drawCubeOutline(-.0025, -.0025, -.0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                editor.selectedBlock.put(id, blockID);
            }
        }
    }
    @Override
    public String getTooltip(int device){
        return block.getListTooltip();
    }
}