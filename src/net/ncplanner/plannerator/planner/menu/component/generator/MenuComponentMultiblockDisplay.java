package net.ncplanner.plannerator.planner.menu.component.generator;
import java.util.UUID;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import org.lwjgl.glfw.GLFW;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistScrollable;
import net.ncplanner.plannerator.planner.menu.component.MenuComponentMinimalistTextView;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblockDisplay extends MenuComponent{
    public Multiblock multiblock;
    private int border = 2;
    private int CELL_SIZE = 32;
    private MenuComponentMinimalistTextView textView = add(new MenuComponentMinimalistTextView(0, 0, 0, 0, CELL_SIZE, CELL_SIZE));
    private MenuComponentMinimalistScrollable multiblockView = add(new MenuComponentMinimalistScrollable(x, y, width, height, CELL_SIZE, CELL_SIZE));
    private MenuComponent view = new MenuComponent(0, 0, 0, 0){
        @Override
        public void render(){
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
    public void render(){
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
        drawRect(x, y, x+width, y+height, 0);
        renderer.setColor(Core.theme.getMultiblockDisplayBackgroundColor());
        drawRect(x+border, y+border, x+width-border, y+height-border, 0);
        textView.setText(multiblock.getTooltip(true));
    }
    @Override
    public void renderForeground(){
        Renderer renderer = new Renderer();
        super.renderForeground();
        if(Core.isShiftPressed()){
            renderer.setColor(Core.theme.getMultiblockDisplayBorderColor());
            drawRect(x, y, x+width, y+24, 0);
            renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
            drawCenteredText(x, y+2, x+width, y+22, "Shift-click to save multiblock");
        }
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed&&Core.isShiftPressed()){
            Multiblock mb = multiblock.copy();
            mb.metadata.put("Name", "Generated-"+UUID.randomUUID().toString());
            Core.multiblocks.add(mb);
        }
    }
}