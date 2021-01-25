package planner.vr.menu.component;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.vr.menu.VRMenuEdit;
import simplelibrary.opengl.Renderer2D;
public class VRMenuComponentMultiblockOutputPanel extends VRMenuComponentTextPanel{
    private final VRMenuEdit editor;
    public VRMenuComponentMultiblockOutputPanel(VRMenuEdit editor, double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz){
        super(x, y, z, width, height, depth, rx, ry, rz, "");
        this.editor = editor;
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        text = editor.getMultiblock().getTooltip();
        super.renderComponent(tdpb);
    }
    @Override//because non-centered text
    public void drawText(){
        String[] txt = this.text.split("\n");
        double textHeight = Math.min(depth, (height-textInset*2)/txt.length);
        GL11.glPushMatrix();
        GL11.glTranslated(0, height, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        for(int i = 0; i<txt.length; i++){
            String s = txt[i];
            Renderer2D.drawText(textInset, textInset+textHeight*i, width-textInset, textInset+textHeight*(i+1), s);
        }
        GL11.glPopMatrix();
    }
}