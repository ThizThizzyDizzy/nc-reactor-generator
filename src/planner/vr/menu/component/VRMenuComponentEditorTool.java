package planner.vr.menu.component;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.editor.tool.EditorTool;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
public class VRMenuComponentEditorTool extends VRMenuComponent{
    private final EditorTool tool;
    private final VRMenuEdit editor;
    public VRMenuComponentEditorTool(VRMenuEdit editor, double x, double y, double z, double size, EditorTool tool){
        super(x, y, z, size, size, size, 0, 0, 0);
        this.tool = tool;
        this.editor = editor;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(isDeviceOver.isEmpty()?Core.theme.getVRComponentColor(Core.getThemeIndex(this)):Core.theme.getVRDeviceoverComponentColor(Core.getThemeIndex(this)));
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getVRSelectedOutlineColor(Core.getThemeIndex(this)));
        if(editor.getSelectedTool(tool.id)==tool){
            VRCore.drawCubeOutline(.0025, .0025, .0025, width+.0025, height+.0025, depth+.0025, .0025);//2.5mm
        }
        tool.render(0, 0, 0, width, height, depth, Core.getThemeIndex(this));
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        super.keyEvent(device, button, pressed);
        if(pressed){
            if(button==VR.EVRButtonId_k_EButton_SteamVR_Trigger){
                editor.setSelectedTool(tool);
            }
        }
    }
    @Override
    public String getTooltip(int device){
        return tool.getTooltip();
    }
}