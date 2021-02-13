package planner.vr.menu.component;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.vr.menu.VRMenuEdit;
public class VRMenuComponentMultiblockOutputPanel extends VRMenuComponentTextPanel{
    private final VRMenuEdit editor;
    public VRMenuComponentMultiblockOutputPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz, "");
        this.editor = editor;
        snap = -1;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        text = editor.getMultiblock().getTooltip();
        super.renderComponent(tdpb);
    }
}