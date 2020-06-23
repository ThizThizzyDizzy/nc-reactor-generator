package planner.menu.component;
import org.lwjgl.opengl.GL11;
import planner.multiblock.Block;
import simplelibrary.opengl.ImageStash;
public class MenuComponentEditorBlock extends MenuComponentMinimalistButton{
    private final Block block;
    public MenuComponentEditorBlock(Block block){
        super(0, 0, 0, 0, block.getName(), true, true, .9f);
        this.block = block;
        selectedColor = foregroundColor.darker();
    }
    @Override
    public void render(){
        super.render();
        double border = height/20;
        if(isSelected){
            GL11.glColor4d(selectedColor.getRed()/255f, selectedColor.getGreen()/255f, selectedColor.getBlue()/255f, selectedColor.getAlpha()/510f);
            drawRect(x, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+width, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
        if(isMouseOver){
            GL11.glColor4d(foregroundColor.getRed()/255f, foregroundColor.getGreen()/255f, foregroundColor.getBlue()/255f, foregroundColor.getAlpha()/510f);
            drawRect(x, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+width, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
    }
    @Override
    public void drawText(){
        if(block.getTexture()==null){
            textInset = height/3;
            super.drawText();
        }
        else{
            GL11.glColor4d(1, 1, 1, 1);
            drawRect(x, y, x+width, y+height, ImageStash.instance.allocateAndSetupTexture(block.getTexture()));
        }
    }
}