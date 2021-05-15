package planner.vr.menu.component;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
import planner.Core;
import planner.FormattedText;
import planner.vr.VRCore;
import planner.vr.VRMenuComponent;
public class VRMenuComponentTextPanel extends VRMenuComponent{
    public double textInset = .025;//2.5cm
    public double textOffset = .001f;//1mm
    public FormattedText text;
    public int snap = 0;
    public VRMenuComponentTextPanel(double x, double y, double z, double width, double height, double depth, double rx, double ry, double rz, String text){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.text = new FormattedText(text);
    }
    @Override
    public void renderComponent(TrackedDevicePose.Buffer tdpb){
        Core.applyColor(Core.theme.getTextViewBackgroundColor());
        VRCore.drawCube(0, 0, 0, width, height, depth, 0);
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        ArrayList<FormattedText> txt = text.split("\n");
        double textHeight = Math.min(depth, (height-textInset*2)/txt.size());
        GL11.glPushMatrix();
        GL11.glTranslated(0, height, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        for(int i = 0; i<txt.size(); i++){
            FormattedText s = txt.get(i);
            Core.drawFormattedText(textInset, textInset+textHeight*i, width-textInset, textInset+textHeight*(i+1), s, snap);
        }
        GL11.glPopMatrix();
    }
}