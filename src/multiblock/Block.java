package multiblock;
import java.awt.Color;
import java.awt.image.BufferedImage;
import planner.Core;
import simplelibrary.Queue;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
import planner.menu.MenuEdit;
public abstract class Block extends MultiblockBit{
    public int x;
    public int y;
    public int z;
    private BufferedImage grayscaleTexture = null;
    public Block(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract Block newInstance(int x, int y, int z);
    public abstract void copyProperties(Block other);
    public abstract BufferedImage getBaseTexture();
    public abstract BufferedImage getTexture();
    private BufferedImage getGrayscaleTexture(){
        if(grayscaleTexture!=null)return grayscaleTexture;
        BufferedImage img = getTexture();
        if(img==null)return null;
        BufferedImage grayscale = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for(int x = 0; x<img.getWidth(); x++){
            for(int y = 0; y<img.getHeight(); y++){
                Color c = new Color(img.getRGB(x, y));
                float[] hsb = new float[3];
                Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                hsb[1]*=.25f;
                c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                grayscale.setRGB(x, y, c.getRGB());
            }
        }
        return grayscaleTexture = grayscale;
    }
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
    public void renderGrayscale(double x, double y, double width, double height, boolean renderOverlay){
        renderGrayscale(x, y, width, height, renderOverlay, 1);
    }
    public void renderGrayscale(double x, double y, double width, double height, boolean renderOverlay, float alpha){
        if(getGrayscaleTexture()==null){
            Core.applyColor(Core.theme.getTextColor());
            String text = getName();
            double textLength = FontManager.getLengthForStringWithHeight(text, height);
            double scale = Math.min(1, (width)/textLength);
            double textHeight = (int)((height)*scale)-4;
            drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
        }else{
            Core.applyWhite(alpha);
            drawRect(x, y, x+width, y+height, Core.getTexture(getGrayscaleTexture()));
        }
        if(renderOverlay)renderOverlay(x,y,width,height);
    }
    public abstract void renderOverlay(double x, double y, double width, double height);
    public void drawCircle(double x, double y, double width, double height, Color color){
        Core.applyColor(color);
        Renderer2D.drawRect(x, y, x+width, y+height, MenuEdit.sourceCircle);
        Core.applyWhite();
    }
    public void drawOutline(double x, double y, double width, double height, double inset, Color color){
        Core.applyColor(color);
        Renderer2D.drawRect(x, y, x+width, y+height, MenuEdit.outlineSquare);
        Core.applyWhite();
//        Core.applyColor(color);
//        inset*=Math.min(width, height);
//        drawRect(x+inset, y+inset, x+width-inset, y+inset+height/16, 0);
//        drawRect(x+inset, y+height-inset-height/16, x+width-inset, y+height-inset, 0);
//        drawRect(x+inset, y+inset+height/16, x+inset+width/16, y+height-inset-height/16, 0);
//        drawRect(x+width-inset-height/16, y+inset+height/16, x+width-inset, y+height-inset-height/16, 0);
    }
    public Block copy(int x, int y, int z){
        Block b = newInstance(x, y, z);
        copyProperties(b);
        return b;
    }
    public abstract boolean isCasing();
    public abstract boolean hasRules();
    public abstract boolean calculateRules(Multiblock multiblock);
    public abstract boolean matches(Block template);
    public abstract boolean requires(Block other, Multiblock mb);
    public abstract boolean canGroup();
    public abstract boolean canBeQuickReplaced();
    public boolean defaultEnabled(){
        return true;
    }
}