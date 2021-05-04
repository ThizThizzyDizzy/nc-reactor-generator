package multiblock;
import java.util.Locale;
import java.util.function.Function;
import multiblock.configuration.Configuration;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import planner.menu.component.Searchable;
import planner.vr.VRCore;
import simplelibrary.Queue;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public abstract class Block extends MultiblockBit implements Searchable{
    protected Configuration configuration;
    public int x;
    public int y;
    public int z;
    private Image grayscaleTexture = null;
    public Block(Configuration configuration, int x, int y, int z){
        this.configuration = configuration;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract Block newInstance(int x, int y, int z);
    public abstract void copyProperties(Block other);
    public abstract Image getBaseTexture();
    public abstract Image getTexture();
    private Image getGrayscaleTexture(){
        if(grayscaleTexture!=null)return grayscaleTexture;
        Image img = getTexture();
        if(img==null)return null;
        Image grayscale = new Image(img.getWidth(), img.getHeight());
        for(int x = 0; x<img.getWidth(); x++){
            for(int y = 0; y<img.getHeight(); y++){
                Color c = img.getColor(x, y);
                grayscale.setColor(x, y, Color.fromHSB(c.getHue(), c.getSaturation()*.25f, c.getBrightness(), c.getAlpha()));
            }
        }
        return grayscaleTexture = grayscale;
    }
    public abstract String getName();
    public abstract void clearData();
    public <T extends Block> Queue<T> getAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            if(!multiblock.contains(x+direction.x, y+direction.y, z+direction.z))continue;
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null)adjacent.enqueue(b);
        }
        return adjacent;
    }
    public <T extends Block> Queue<T> getActiveAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            if(!multiblock.contains(x+direction.x, y+direction.y, z+direction.z))continue;
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null&&b.isActive())adjacent.enqueue(b);
        }
        return adjacent;
    }
    public abstract String getTooltip(Multiblock multiblock);
    public abstract String getListTooltip();
    public void render(double x, double y, double width, double height, boolean renderOverlay, Multiblock multiblock){
        render(x, y, width, height, renderOverlay, 1, multiblock);
    }
    public void render(double x, double y, double width, double height, boolean renderOverlay, float alpha, Multiblock multiblock){
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
        if(renderOverlay)renderOverlay(x,y,width,height, multiblock);
    }
    public void render(double x, double y, double z, double width, double height, double depth, boolean renderOverlay, float alpha, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc){
        double[] bounds = multiblock.getCubeBounds(this);
        bounds[0] *= width;
        bounds[1] *= height;
        bounds[2] *= depth;
        bounds[3] *= width;
        bounds[4] *= height;
        bounds[5] *= depth;
        if(getTexture()==null){
            Core.applyColor(Core.theme.getRGBA(1, 1, 0, alpha));
            VRCore.drawCube(x+bounds[0], y+bounds[1], z+bounds[2], x+bounds[3], y+bounds[4], z+bounds[5], 0, faceRenderFunc);
        }else{
            Core.applyWhite(alpha);
            VRCore.drawCube(x+bounds[0], y+bounds[1], z+bounds[2], x+bounds[3], y+bounds[4], z+bounds[5], Core.getTexture(getTexture()), faceRenderFunc);
        }
        if(renderOverlay)renderOverlay(x+bounds[0], y+bounds[1], z+bounds[2], bounds[3], bounds[4], bounds[5],multiblock,faceRenderFunc);
    }
    public void renderGrayscale(double x, double y, double width, double height, boolean renderOverlay, Multiblock multiblock){
        renderGrayscale(x, y, width, height, renderOverlay, 1, multiblock);
    }
    public void renderGrayscale(double x, double y, double width, double height, boolean renderOverlay, float alpha, Multiblock multiblock){
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
        if(renderOverlay)renderOverlay(x,y,width,height, multiblock);
    }
    public abstract void renderOverlay(double x, double y, double width, double height, Multiblock multiblock);
    public abstract void renderOverlay(double x, double y, double z, double width, double height, double depth, Multiblock multiblock, Function<Direction, Boolean> faceRenderFunc);
    public void drawCircle(double x, double y, double width, double height, Color color){
        Core.applyColor(color);
        Renderer2D.drawRect(x, y, x+width, y+height, Core.sourceCircle);
        Core.applyWhite();
    }
    public void drawCircle(double x, double y, double z, double width, double height, double depth, Color color, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        Core.applyColor(color);
        if(py)drawCircleBit(x,y,z,width,height,depth);
        if(pz){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 1, 0, 0);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawCircleBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(nz){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, -1, 0, 0);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawCircleBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(nx){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 0, 0, 1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawCircleBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(px){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 0, 0, -1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawCircleBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(ny){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(180, 0, 0, 1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawCircleBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        Core.applyWhite();
    }
    private void drawCircleBit(double x, double y, double z, double width, double height, double depth){
        double innerRadius = width/4;
        double outerRadius = width/8*3;
        int resolution = (int)(Math.max(12,2*Math.PI*outerRadius*100));
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        double thickness = width/32;
        for(int i = 0; i<resolution; i++){
            double nextAngle = angle+(360d/resolution);
            if(nextAngle>=360)nextAngle-=360;
            double inX = x+width/2+Math.cos(Math.toRadians(angle-90))*innerRadius;
            double inZ = z+depth/2+Math.sin(Math.toRadians(angle-90))*innerRadius;
            double outX = x+width/2+Math.cos(Math.toRadians(angle-90))*outerRadius;
            double outZ = z+depth/2+Math.sin(Math.toRadians(angle-90))*outerRadius;
            double nextInX = x+width/2+Math.cos(Math.toRadians(nextAngle-90))*innerRadius;
            double nextInZ = z+depth/2+Math.sin(Math.toRadians(nextAngle-90))*innerRadius;
            double nextOutX = x+width/2+Math.cos(Math.toRadians(nextAngle-90))*outerRadius;
            double nextOutZ = z+depth/2+Math.sin(Math.toRadians(nextAngle-90))*outerRadius;
            //inner face
            GL11.glVertex3d(inX, y+height, inZ);
            GL11.glVertex3d(nextInX, y+height, nextInZ);
            GL11.glVertex3d(nextInX, y+height+thickness, nextInZ);
            GL11.glVertex3d(inX, y+height+thickness, inZ);
            //middle face
            GL11.glVertex3d(inX, y+height+thickness, inZ);
            GL11.glVertex3d(nextInX, y+height+thickness, nextInZ);
            GL11.glVertex3d(nextOutX, y+height+thickness, nextOutZ);
            GL11.glVertex3d(outX, y+height+thickness, outZ);
            //outer face
            GL11.glVertex3d(outX, y+height, outZ);
            GL11.glVertex3d(nextOutX, y+height, nextOutZ);
            GL11.glVertex3d(nextOutX, y+height+thickness, nextOutZ);
            GL11.glVertex3d(outX, y+height+thickness, outZ);
            angle = nextAngle;
        }
        GL11.glEnd();
    }
    public void drawOutline(double x, double y, double width, double height, Color color){
        Core.applyColor(color);
        Renderer2D.drawRect(x, y, x+width, y+height, Core.outlineSquare);
        Core.applyWhite();
    }
    public void drawOutline(double x, double y, double z, double width, double height, double depth, Color color, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        Core.applyColor(color);
        if(py)drawOutlineBit(x,y,z,width,height,depth);
        if(pz){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 1, 0, 0);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawOutlineBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(nz){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, -1, 0, 0);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawOutlineBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(nx){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 0, 0, 1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawOutlineBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(px){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(90, 0, 0, -1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawOutlineBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        if(ny){
            GL11.glPushMatrix();
            GL11.glTranslated(x+width/2, y+height/2, z+depth/2);
            GL11.glRotated(180, 0, 0, 1);
            GL11.glTranslated(-x-width/2, -y-height/2, -z-depth/2);
            drawOutlineBit(x,y,z,width,height,depth);
            GL11.glPopMatrix();
        }
        Core.applyWhite();
    }
    private void drawOutlineBit(double x, double y, double z, double width, double height, double depth){
        double w = width/32d;//pixel
        double h = height/32d;//pixel
        double d = depth/32d;//pixel
        Function<Direction, Boolean> func = (Direction t) -> t!=Direction.NY;//don't render the bottom face; this is rendering on top
        VRCore.drawCube(x+w/2, y+height, z+d/2, x+width-w/2, y+height+h/2, z+d*3/2, 0, func);//top
        VRCore.drawCube(x+w/2, y+height, z+width-d*3/2, x+width-w/2, y+height+h/2, z+width-d/2, 0, func);//bottom
        VRCore.drawCube(x+w/2, y+height, z+d*3/2, x+w*3/2, y+height+h/2, z+width-d*3/2, 0, func);//left
        VRCore.drawCube(x+width-w*3/2, y+height, z+d*3/2, x+width-w/2, y+height+h/2, z+width-d*3/2, 0, func);//right
    }
    public Block copy(int x, int y, int z){
        Block b = newInstance(x, y, z);
        copyProperties(b);
        return b;
    }
    public abstract boolean isValid();
    public abstract boolean isActive();
    public abstract boolean isCore();
    public abstract boolean hasRules();
    public abstract boolean calculateRules(Multiblock multiblock);
    public abstract boolean matches(Block template);
    public abstract boolean requires(Block other, Multiblock mb);
    public abstract boolean canGroup();
    public abstract boolean canBeQuickReplaced();
    public boolean defaultEnabled(){
        return true;
    }
    public abstract Block copy();
    public abstract boolean isEqual(Block other);
    public boolean roughMatch(String blockNam){
        blockNam = blockNam.toLowerCase(Locale.ENGLISH);
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        blockNam = blockNam.replace("_", " ").replace("liquid ", "").replace(" cooler", "").replace(" heat sink", "").replace(" heatsink", "").replace(" sink", "").replace(" neutron shield", "").replace(" shield", "").replace(" moderator", "").replace(" coolant", "").replace(" heater", "").replace("fuel ", "").replace(" reflector", "");
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        String blockName = getName();
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        blockName = blockName.toLowerCase(Locale.ENGLISH).replace("_", " ").replace("liquid ", "").replace(" cooler", "").replace(" heat sink", "").replace(" heatsink", "").replace(" sink", "").replace(" neutron shield", "").replace(" shield", "").replace(" moderator", "").replace(" coolant", "").replace(" heater", "").replace("fuel ", "").replace(" reflector", "");
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        return blockNam.equalsIgnoreCase(blockName);
    }
    public boolean isFullBlock(){
        return true;
    }
    public Configuration getConfiguration(){
        return configuration;
    }
    public abstract void convertTo(Configuration to) throws MissingConfigurationEntryException;
    public boolean shouldRenderFace(Block against){
        return against==null;
    }
}