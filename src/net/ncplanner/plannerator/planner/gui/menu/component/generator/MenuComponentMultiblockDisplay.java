package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import java.util.UUID;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import net.ncplanner.plannerator.planner.gui.menu.component.Scrollable;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import static org.lwjgl.glfw.GLFW.*;
public class MenuComponentMultiblockDisplay extends Component{
    public Multiblock multiblock;
    private int border = 2;
    private int CELL_SIZE = 32;
    private TextView textView = add(new TextView(0, 0, 0, 0, CELL_SIZE, CELL_SIZE));
    private Scrollable multiblockView = add(new Scrollable(x, y, width, height, CELL_SIZE, CELL_SIZE));
    private Component view = new Component(0, 0, 0, 0){
        @Override
        public void draw(double deltaTime){
            Renderer renderer = new Renderer();
            BoundingBox bbox = multiblock.getBoundingBox();
            multiblock.forEachPosition((x, y, z) -> {
                Block block = multiblock.getBlock(x, y, z);
                if(block!=null){
                    block.render(renderer, this.x+x*CELL_SIZE, this.y+y*(bbox.getDepth()+1)*CELL_SIZE+z*CELL_SIZE, CELL_SIZE, CELL_SIZE, true, multiblock);
                }
            });
        }
    };
    public MenuComponentMultiblockDisplay(Multiblock multiblock){
        super(0,0,0,0);
        this.multiblock = multiblock;
        multiblockView.add(view);
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        BoundingBox bbox = multiblock.getBoundingBox();
        view.width = bbox.getWidth()*CELL_SIZE;
        view.height = (bbox.getDepth()+1)*bbox.getHeight()*CELL_SIZE;
        textView.width = width;
        textView.height = height/4;
        multiblockView.width = width;
        multiblockView.height = height-textView.height;
        multiblockView.y = textView.height;
        renderer.setColor(Core.theme.getMultiblockDisplayBorderColor());
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getMultiblockDisplayBackgroundColor());
        renderer.fillRect(x+border, y+border, x+width-border, y+height-border);
        textView.setText(multiblock.getTooltip(true));
    }
    @Override
    public void drawForeground(double deltaTime){
        Renderer renderer = new Renderer();
        super.drawForeground(deltaTime);
        if(Core.isShiftPressed()){
            renderer.setColor(Core.theme.getMultiblockDisplayBorderColor());
            renderer.fillRect(x, y, x+width, y+24);
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            renderer.drawCenteredText(x, y+2, x+width, y+22, "Shift-click to save multiblock");
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS&&Core.isShiftPressed()){
            Multiblock mb = multiblock.copy();
            mb.metadata.put("Name", "Generated-"+UUID.randomUUID().toString());
            Core.multiblocks.add(mb);
        }
    }
}