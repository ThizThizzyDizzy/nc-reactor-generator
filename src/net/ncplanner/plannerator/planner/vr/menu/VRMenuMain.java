package net.ncplanner.plannerator.planner.vr.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.menu.MenuMain;
import net.ncplanner.plannerator.planner.vr.VRGUI;
import net.ncplanner.plannerator.planner.vr.VRMenu;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentButton;
import net.ncplanner.plannerator.planner.vr.menu.component.VRMenuComponentMultiblock;
import org.lwjgl.openvr.TrackedDevicePose;
import org.lwjgl.openvr.VR;
public class VRMenuMain extends VRMenu{
    public VRMenuComponentButton exit = add(new VRMenuComponentButton(-.25f, 1.75f, -1, .5f, .125f, .1f, 0, 0, 0, "Exit VR", true, false));
    private ArrayList<VRMenuComponentMultiblock> multiblocks = new ArrayList<>();
    private ArrayList<VRMenuComponentButton> multiblockButtons = new ArrayList<>();
    private ArrayList<Multiblock> toAdd = new ArrayList<>();
    public Multiblock opening = null;
    private float openProgress = 0;
    private static final int openTime = 10;
    public VRMenuMain(VRGUI gui){
        super(gui, null);
        for(int i = 0; i<Core.multiblockTypes.size(); i++){
            Multiblock m = Core.multiblockTypes.get(i);
            VRMenuComponentButton button = new VRMenuComponentButton(-.375f/2, 1.25f-.1f*i, -.75f, .375f, .075f, .05f, 0, 0, 0, m.getDefinitionName(), true, false);
            button.setTooltip(m.getDescriptionTooltip());
            button.addActionListener(() -> {
                Multiblock multi = m.newInstance();
                Core.multiblocks.add(multi);
                toAdd.add(multi);
            });
            multiblockButtons.add(button);
        }
        exit.addActionListener(() -> {
            Core.gui.open(new MenuMain(Core.gui));
        });
    }
    @Override
    public void render(Renderer renderer, TrackedDevicePose.Buffer tdpb, double deltaTime){
        for(Multiblock m : toAdd){
            multiblocks.add(add(new VRMenuComponentMultiblock(this, m)));
        }
        toAdd.clear();
        if(opening!=null){
            openProgress+=deltaTime*20;
            if(openProgress>=openTime){
                gui.open(new VRMenuEdit(gui, opening));
            }
        }
        if(opening!=null){
            float progress = Math.min(openProgress/openTime,1);
            for(VRMenuComponentMultiblock mb : multiblocks){
                mb.scale = 1-progress;
            }
        }
        super.render(renderer, tdpb, deltaTime);
    }
    @Override
    public void onOpened(){
        refresh();
    }
    public void refresh(){
        components.removeAll(multiblocks);
        multiblocks.clear();
        for(Multiblock multi : Core.multiblocks){
            VRMenuComponentMultiblock vrc = new VRMenuComponentMultiblock(this, multi);
            vrc.boost = 140;
            vrc.y = 1.25f;
            vrc.z = -1;
            multiblocks.add(add(vrc));
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
        if(opening!=null)return;
        super.keyEvent(device, button, pressed);
        if(button==VR.EVRButtonId_k_EButton_IndexController_A){
            if(pressed){
                for(VRMenuComponentButton b : multiblockButtons){
                    add(b);
                }
            }else{
                components.removeAll(multiblockButtons);
            }
        }
    }
}