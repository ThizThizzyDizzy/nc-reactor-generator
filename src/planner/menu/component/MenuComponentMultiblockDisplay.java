package planner.menu.component;
import multiblock.Multiblock;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMultiblockDisplay extends MenuComponent{
    public Multiblock multiblock;
    private int border = 2;
    private MenuComponentMinimalistTextView textView = add(new MenuComponentMinimalistTextView(0, 0, 0, 0, 16, 16));
    public MenuComponentMultiblockDisplay(Multiblock multiblock){
        super(0,0,0,0);
        this.multiblock = multiblock;
    }
    @Override
    public void render(){
        textView.width = width;
        textView.height = height;
        Core.applyColor(Core.theme.getDarkButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getButtonColor());
        drawRect(x+border, y+border, x+width-border, y+height-border, 0);
        textView.setText(multiblock.getTooltip());
    }
}