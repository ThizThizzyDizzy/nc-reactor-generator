package net.ncplanner.plannerator.planner.vr.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.vr.menu.VRMenuEdit;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentMultiblockOutputPanel extends VRMenuComponentTextPanel{
    private final VRMenuEdit editor;
    public VRMenuComponentMultiblockOutputPanel(VRMenuEdit editor, float x, float y, float z, float width, float height, float depth, float rx, float ry, float rz){
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