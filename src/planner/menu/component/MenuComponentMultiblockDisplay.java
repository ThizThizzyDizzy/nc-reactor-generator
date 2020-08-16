package planner.menu.component;
import java.util.UUID;
import multiblock.Block;
import multiblock.Multiblock;
import planner.Core;
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
            for(int x = 0; x<multiblock.getX(); x++){
                for(int y = 0; y<multiblock.getY(); y++){
                    for(int z = 0; z<multiblock.getZ(); z++){
                        Block block = multiblock.getBlock(x, y, z);
                        if(block!=null){
                            block.render(this.x+x*CELL_SIZE, this.y+y*(multiblock.getZ()+1)*CELL_SIZE+z*CELL_SIZE, CELL_SIZE, CELL_SIZE, true);
                        }
                    }
                }
            }
        }
    };
    public MenuComponentMultiblockDisplay(Multiblock multiblock){
        super(0,0,0,0);
        this.multiblock = multiblock;
        multiblockView.add(view);
    }
    @Override
    public void render(){
        view.width = multiblock.getX()*CELL_SIZE;
        view.height = (multiblock.getZ()+1)*multiblock.getY()*CELL_SIZE;
        textView.width = width;
        textView.height = height/4;
        multiblockView.width = width;
        multiblockView.height = height-textView.height;
        multiblockView.y = textView.height;
        Core.applyColor(Core.theme.getDarkButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getButtonColor());
        drawRect(x+border, y+border, x+width-border, y+height-border, 0);
        textView.setText(multiblock.getTooltip());
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
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(button==0&&pressed&&Core.isShiftPressed()){
            Multiblock mb = multiblock.copy();
            mb.metadata.put("Name", "Generated-"+UUID.randomUUID().toString());
            Core.multiblocks.add(mb);
        }
    }
}