package net.ncplanner.plannerator.planner.vr.menu.component;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentMultiblockOutputPanel extends VRMenuComponentTextPanel{
    private final VRMenuEdit editor;
    public VRMenuComponentMultiblockOutputPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz, "");
        this.editor = editor;
        snap = -1;
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        text = editor.getMultiblock().getFullTooltip();
        super.renderComponent(renderer, tdpb);
    }
}