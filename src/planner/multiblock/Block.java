package planner.multiblock;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.Queue;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.ImageStash;
public abstract class Block extends MultiblockBit{
    public int x;
    public int y;
    public int z;
    public Block(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract Block newInstance(int x, int y, int z);
    public abstract void copyProperties(Block other);
    public abstract BufferedImage getBaseTexture();
    public abstract BufferedImage getTexture();
    public abstract String getName();
    public abstract void clearData();
    public abstract boolean isActive();
    public <T extends Block> Queue<T> getAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null)adjacent.enqueue(b);
        }
        return adjacent;
    }
    public <T extends Block> Queue<T> getActiveAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null&&b.isActive())adjacent.enqueue(b);
        }
        return adjacent;
    }
    public abstract String getTooltip();
    public abstract String getListTooltip();
    public void render(double x, double y, double width, double height, boolean renderOverlay){
        render(x, y, width, height, renderOverlay, 1);
    }
    public void render(double x, double y, double width, double height, boolean renderOverlay, float alpha){
        if(getTexture()==null){
            Core.applyColor(Core.theme.getTextColor());
            String text = getName();
            double textLength = FontManager.getLengthForStringWithHeight(text, height);
            double scale = Math.min(1, (width)/textLength);
            double textHeight = (int)((height)*scale)-4;
            drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
        }else{
            Core.applyWhite(alpha);
            drawRect(x, y, x+width, y+height, Core.getTexture(getTexture()));
        }
        if(renderOverlay)renderOverlay(x,y,width,height);
    }
    public abstract void renderOverlay(double x, double y, double width, double height);
    public void drawOutline(double x, double y, double width, double height, double inset, Color color){
        Core.applyColor(color);
        inset*=Math.min(width, height);
        drawRect(x+inset, y+inset, x+width-inset, y+inset+height/16, 0);
        drawRect(x+inset, y+height-inset-height/16, x+width-inset, y+height-inset, 0);
        drawRect(x+inset, y+inset+height/16, x+inset+width/16, y+height-inset-height/16, 0);
        drawRect(x+width-inset-height/16, y+inset+height/16, x+width-inset, y+height-inset-height/16, 0);
    }
    public void drawCircle(double x, double y, double innerRadius, double outerRadius, Color color){
        Core.applyColor(color);
        int resolution = (int)(2*Math.PI*outerRadius);//an extra *2 to account for wavy surface?
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        for(int i = 0; i<resolution; i++){
            double inX = x+Math.cos(Math.toRadians(angle-90))*innerRadius;
            double inY = y+Math.sin(Math.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
            double outX = x+Math.cos(Math.toRadians(angle-90))*outerRadius;
            double outY = y+Math.sin(Math.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            angle+=(360d/resolution);
            if(angle>=360)angle-=360;
            outX = x+Math.cos(Math.toRadians(angle-90))*outerRadius;
            outY = y+Math.sin(Math.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            inX = x+Math.cos(Math.toRadians(angle-90))*innerRadius;
            inY = y+Math.sin(Math.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
        }
        GL11.glEnd();
    }
    public Block copy(int x, int y, int z){
        Block b = newInstance(x, y, z);
        copyProperties(b);
        return b;
    }
    public abstract boolean hasRules();
    public abstract boolean calculateRules(Multiblock multiblock);
    public abstract boolean matches(Block template);
    public abstract boolean requires(Block other, Multiblock mb);
    public abstract boolean canGroup();
}