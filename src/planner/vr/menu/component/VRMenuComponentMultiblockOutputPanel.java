package planner.vr.menu.component;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.Core;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentMultiblockOutputPanel extends VRMenuComponent{
    private final VRMenuEdit editor;
    private double textInset = .025;//2.5cm
    private double textOffset = .001f;//1mm
    public VRMenuComponentMultiblockOutputPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.editor = editor;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(Core.theme.getEditorListBorderColor());
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
    }
    public void drawText(){
        String[] text = editor.getMultiblock().getTooltip().split("\n");
        double textHeight = Math.min(depth, (height-textInset*2)/text.length);
        GL11.glPushMatrix();
        GL11.glTranslated(0, height, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        for(int i = 0; i<text.length; i++){
            String s = text[i];
            Renderer2D.drawCenteredText(textInset, textInset+textHeight*i, width-textInset, textInset+textHeight*(i+1), s);
        }
        GL11.glPopMatrix();
    }
    @Override
    public void tick(){
        super.tick();
    }
}