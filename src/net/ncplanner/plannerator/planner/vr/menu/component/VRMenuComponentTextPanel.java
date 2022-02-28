package net.ncplanner.plannerator.planner.vr.menu.component;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.vr.VRMenuComponent;
import org.joml.Matrix4f;
import org.lwjgl.openvr.TrackedDevicePose;
public class VRMenuComponentTextPanel extends VRMenuComponent{
    public float textInset = .025f;//2.5fcm
    public float textOffset = .001f;//1mm
    public FormattedText text;
    public int snap = 0;
    public VRMenuComponentTextPanel(float x, float y, float z, float width, float height, float depth, float rx, float ry, float rz, String text){
        super(x, y, z, width, height, depth, rx, ry, rz);
        this.text = new FormattedText(text);
    }
    @Override
    public void renderComponent(Renderer renderer, TrackedDevicePose.Buffer tdpb){
        renderer.setColor(Core.theme.getTextViewBackgroundColor());
        renderer.drawCube(0, 0, 0, width, height, depth, null);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        ArrayList<FormattedText> txt = text.split("\n");
        float textHeight = Math.min(depth, (height-textInset*2)/txt.size());
        renderer.pushModel(new Matrix4f()
                .translate(0, height, depth+textOffset)
                .scale(1, -1, 1));
        for(int i = 0; i<txt.size(); i++){
            FormattedText s = txt.get(i);
            renderer.drawFormattedText(textInset, textInset+textHeight*i, width-textInset, textInset+textHeight*(i+1), s, snap);
        }
        renderer.popModel();
    }
}