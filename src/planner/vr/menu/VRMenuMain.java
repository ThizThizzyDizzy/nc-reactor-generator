package planner.vr.menu;
import java.util.ArrayList;
import multiblock.Multiblock;
import org.lwjgl.openvr.VR;
import planner.Core;
import planner.menu.MenuMain;
import planner.vr.VRGUI;
import planner.vr.VRMenu;
import planner.vr.menu.component.VRMenuComponentButton;
import planner.vr.menu.component.VRMenuComponentMultiblock;
public class VRMenuMain extends VRMenu{
    public VRMenuComponentButton exit = add(new VRMenuComponentButton(-.25, 1.5, -1, .5, .125, .1, 0, 0, 0, "Exit VR", true, false));
    private ArrayList<VRMenuComponentMultiblock> multiblocks = new ArrayList<>();
    private ArrayList<VRMenuComponentButton> multiblockButtons = new ArrayList<>();
    private ArrayList<Multiblock> toAdd = new ArrayList<>();
    public VRMenuMain(VRGUI gui){
        super(gui, null);
        for(int i = 0; i<Core.multiblockTypes.size(); i++){
            Multiblock m = Core.multiblockTypes.get(i);
            VRMenuComponentButton button = new VRMenuComponentButton(-.375/2, 1.25-.1*i, -.75, .375, .075, .05, 0, 0, 0, m.getDefinitionName(), true, false);
            button.addActionListener((e) -> {
                Core.multiblocks.add(m.newInstance());
                toAdd.add(m.newInstance());
            });
            multiblockButtons.add(button);
        }
        exit.addActionListener((e) -> {
            Core.gui.open(new MenuMain(Core.gui));
        });
    }
    @Override
    public void tick(){
        super.tick();
        for(Multiblock m : toAdd){
            multiblocks.add(add(new VRMenuComponentMultiblock(m)));
        }
        toAdd.clear();
    }
    @Override
    public void onGUIOpened(){
        refresh();
    }
    public void refresh(){
        components.removeAll(multiblocks);
        multiblocks.clear();
        for(Multiblock multi : Core.multiblocks){
            VRMenuComponentMultiblock vrc = new VRMenuComponentMultiblock(multi);
            vrc.y = 1.25;
            vrc.z = -1;
            multiblocks.add(add(vrc));
        }
    }
    @Override
    public void keyEvent(int device, int button, boolean pressed){
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