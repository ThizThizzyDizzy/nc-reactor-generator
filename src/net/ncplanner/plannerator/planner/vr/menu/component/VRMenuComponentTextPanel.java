package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.openvr.TrackedDevicePose;
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
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        renderer.drawCube(0, 0, 0, width, height, depth, 0);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<FormattedText> txt = text.split("\n");
        double textHeight = Math.min(depth, (height-textInset*2)/txt.size());
        GL11.glPushMatrix();
        GL11.glTranslated(0, height, depth+textOffset);
        GL11.glScaled(1, -1, 1);
        for(int i = 0; i<txt.size(); i++){
            FormattedText s = txt.get(i);
            renderer.drawFormattedText(textInset, textInset+textHeight*i, width-textInset, textInset+textHeight*(i+1), s, snap);
        }
        GL11.glPopMatrix();
    }
}