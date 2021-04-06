package planner.menu.component.generator;
import java.util.UUID;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.Multiblock;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentMinimalistTextView;
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
            BoundingBox bbox = multiblock.getBoundingBox();
            multiblock.forEachPosition((x, y, z) -> {
                Block block = multiblock.getBlock(x, y, z);
                if(block!=null){
                    block.render(this.x+x*CELL_SIZE, this.y+y*(bbox.getDepth()+1)*CELL_SIZE+z*CELL_SIZE, CELL_SIZE, CELL_SIZE, true, multiblock);
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
        BoundingBox bbox = multiblock.getBoundingBox();
        view.width = bbox.getWidth()*CELL_SIZE;
        view.height = (bbox.getDepth()+1)*bbox.getHeight()*CELL_SIZE;
        textView.width = width;
        textView.height = height/4;
        multiblockView.width = width;
        multiblockView.height = height-textView.height;
        multiblockView.y = textView.height;
        Core.applyColor(Core.theme.getDarkButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getButtonColor());
        drawRect(x+border, y+border, x+width-border, y+height-border, 0);
        textView.setText(multiblock.getTooltip(true));
    }
    @Override
    public void renderForeground(){
        super.renderForeground();
        if(Core.isShiftPressed()){
            Core.applyColor(Core.theme.getDarkButtonColor());
            drawRect(x, y, x+width, y+24, 0);
            Core.applyColor(Core.theme.getTextColor());
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