package net.ncplanner.plannerator;
import java.util.ArrayList;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import org.lwjgl.opengl.GL11;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class Renderer{
    public void fillRect(double left, double top, double right, double bottom){
        if(right<left){
            double r = left;
            double l = right;
            right = r;
            left = l;
        }
        if(bottom<top){
            double b = top;
            double t = bottom;
            bottom = b;
            top = t;
        }
        Renderer2D.drawRect(left, top, right, bottom, 0);
    }
    public void drawImage(String image, double left, double top, double right, double bottom){
        drawImage(TextureManager.getImage(image.substring("/textures/".length(), image.length()-".png".length())), left, top, right, bottom);
    }
    public void drawImage(Image image, double left, double top, double right, double bottom){
        if(right<left){
            double r = left;
            double l = right;
            right = r;
            left = l;
        }
        if(bottom<top){
            double b = top;
            double t = bottom;
            bottom = b;
            top = t;
        }
        if(image==null)fillRect(left, top, right, bottom);
        else Renderer2D.drawRect(left, top, right, bottom, Core.getTexture(image));
    }
    public void drawCenteredText(double left, double top, double right, double bottom, String text){
        Renderer2D.drawCenteredText(left, top, right, bottom, text);
    }
    public void drawText(double left, double top, double right, double bottom, String text){
        Renderer2D.drawText(left, top, right, bottom, text);
    }
    /**
     * Draws formatted text.
     * @param left left edge
     * @param top top edge
     * @param right right edge
     * @param bottom bottom edge
     * @param text The <code>FormattedText</code> to draw.
     * @param snap Which side to snap the text to. Defaults to left. -1 = left. 0 = center. 1 = right
     */
    public void drawFormattedText(double left, double top, double right, double bottom, FormattedText text, int snap){
        if(FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)>right-left){
            text.trimSlightly();
            drawFormattedText(left, top, right, bottom, text, snap);
            return;
        }
        if(snap==0){
            left = (left+right)/2-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top);
        }
        while(text!=null){
            if(text.color!=null)GL11.glColor3f(text.color.getRed()/255f, text.color.getGreen()/255f, text.color.getBlue()/255f);
            double textWidth = FontManager.getLengthForStringWithHeight(text.text, bottom-top);
            if(text.italic){
                drawItalicText(left, top, right, bottom, text.text);
            }else{
                drawText(left, top, right, bottom, text.text);
            }
            if(text.bold){
                double offset = (bottom-top)/20;
                for(int x = 0; x<offset+1; x++){
                    for(int y = 0; y<offset+1; y++){
                        if(text.italic){
                            drawItalicText(left+x, top, right+x, bottom, text.text);
                            drawItalicText(left+x, top-y, right+x, bottom-y, text.text);
                            drawItalicText(left, top-y, right, bottom-y, text.text);
                        }else{
                            drawText(left+x, top, right+x, bottom, text.text);
                            drawText(left+x, top-y, right+x, bottom-y, text.text);
                            drawText(left, top-y, right, bottom-y, text.text);
                        }
                    }
                }
            }
            if(text.strikethrough){
                double topIndent = (bottom-top)*.6;
                double bottomIndent = (bottom-top)*.3;
                fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
            }
            if(text.underline){
                double indent = (bottom-top)*.9;
                fillRect(left, top+indent, left+textWidth, bottom);
            }
            left+=textWidth;
            text = text.next;
        }
    }
    public FormattedText drawFormattedTextWithWrap(double left, double top, double right, double bottom, FormattedText text, int snap){
        if(FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)>right-left){
            String txt = text.text;
            text.trimSlightlyWithoutElipses();
            FormattedText also = drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
            txt = txt.substring(text.text.length());
            return new FormattedText(also!=null?also.text+txt:txt, text.color, text.bold, text.italic, text.underline, text.strikethrough);
        }
        if(snap==0){
            left = (left+right)/2-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-FontManager.getLengthForStringWithHeight(text.toString(), bottom-top);
        }
        if(text.color!=null)GL11.glColor3f(text.color.getRed()/255f, text.color.getGreen()/255f, text.color.getBlue()/255f);
        double textWidth = FontManager.getLengthForStringWithHeight(text.text, bottom-top);
        if(text.italic){
            drawItalicText(left, top, right, bottom, text.text);
        }else{
            drawText(left, top, right, bottom, text.text);
        }
        if(text.bold){
            double offset = (bottom-top)/20;
            for(int x = 0; x<offset+1; x++){
                for(int y = 0; y<offset+1; y++){
                    if(text.italic){
                        drawItalicText(left+x, top, right+x, bottom, text.text);
                        drawItalicText(left+x, top-y, right+x, bottom-y, text.text);
                        drawItalicText(left, top-y, right, bottom-y, text.text);
                    }else{
                        drawText(left+x, top, right+x, bottom, text.text);
                        drawText(left+x, top-y, right+x, bottom-y, text.text);
                        drawText(left, top-y, right, bottom-y, text.text);
                    }
                }
            }
        }
        if(text.strikethrough){
            double topIndent = (bottom-top)*.6;
            double bottomIndent = (bottom-top)*.3;
            fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
        }
        if(text.underline){
            double indent = (bottom-top)*.9;
            fillRect(left, top+indent, left+textWidth, bottom);
        }
        left+=textWidth;
        if(text.next!=null){
            return drawFormattedTextWithWrap(left+textWidth, top, right, bottom, text, snap);
        }
        return null;
    }
    /**
     * Draws formatted text with word-wrapping.
     * @param left left edge
     * @param top top edge
     * @param right right possible edge
     * @param bottom bottom edge
     * @param text The <code>FormattedText</code> to draw.
     * @param snap Which side to snap the text to. Defaults to left. -1 = left. 0 = center. 1 = right
     * @return the portion of text wrapped to the next line
     */
    public FormattedText drawFormattedTextWithWordWrap(double left, double top, double right, double bottom, FormattedText text, int snap){
        ArrayList<FormattedText> words = text.split(" ");
        if(words.isEmpty())return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
        String str = words.get(0).text;
        double height = bottom-top;
        double length = right-left;
        for(int i = 1; i<words.size(); i++){
            String string = str+" "+words.get(i).text;
            if(FontManager.getLengthForStringWithHeight(string.trim(), height)>=length){
                drawFormattedTextWithWrap(left, top, right, bottom, new FormattedText(str, text.color, text.bold, text.italic, text.underline, text.strikethrough), snap);
                return new FormattedText(text.text.replaceFirst("\\Q"+str, "").trim());
            }else{
                str = string;
            }
        }
        return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
    }
    private void drawItalicText(double left, double top, double right, double bottom, String text){
        ImageStash.instance.bindTexture(FontManager.getFontImage());
        GL11.glBegin(GL11.GL_QUADS);
        for(char c : text.toCharArray()){
            double[] texLoc = FontManager.getTextureLocationForChar(c);
            double tilt = (bottom-top)*.25;
            double len = FontManager.getLengthForStringWithHeight(c+"", bottom-top);
            GL11.glTexCoord2d(texLoc[0], texLoc[1]);
            GL11.glVertex2d(left+tilt, top);
            GL11.glTexCoord2d(texLoc[2], texLoc[1]);
            GL11.glVertex2d(left+len+tilt, top);
            GL11.glTexCoord2d(texLoc[2], texLoc[3]);
            GL11.glVertex2d(left+len, bottom);
            GL11.glTexCoord2d(texLoc[0], texLoc[3]);
            GL11.glVertex2d(left, bottom);
            left+=len;
        }
        GL11.glEnd();
    }
    public String drawTextWithWordWrap(double left, double top, double right, double bottom, String text){
        String[] words = text.split(" ");
        String str = words[0];
        double height = bottom-top;
        double length = right-left;
        for(int i = 1; i<words.length; i++){
            String string = str+" "+words[i];
            if(FontManager.getLengthForStringWithHeight(string.trim(), height)>=length){
                Renderer2D.drawTextWithWrap(left, top, right, bottom, str.trim());
                return text.replaceFirst("\\Q"+str, "").trim();
            }else{
                str = string;
            }
        }
        return Renderer2D.drawTextWithWrap(left, top, right, bottom, text);
    }
    public void drawCircle(double x, double y, double innerRadius, double outerRadius){
        int resolution = (int)(2*MathUtil.pi()*outerRadius);
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        for(int i = 0; i<resolution; i++){
            double inX = x+MathUtil.cos(MathUtil.toRadians(angle-90))*innerRadius;
            double inY = y+MathUtil.sin(MathUtil.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
            double outX = x+MathUtil.cos(MathUtil.toRadians(angle-90))*outerRadius;
            double outY = y+MathUtil.sin(MathUtil.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            angle+=(360d/resolution);
            if(angle>=360)angle-=360;
            outX = x+MathUtil.cos(MathUtil.toRadians(angle-90))*outerRadius;
            outY = y+MathUtil.sin(MathUtil.toRadians(angle-90))*outerRadius;
            GL11.glVertex2d(outX,outY);
            inX = x+MathUtil.cos(MathUtil.toRadians(angle-90))*innerRadius;
            inY = y+MathUtil.sin(MathUtil.toRadians(angle-90))*innerRadius;
            GL11.glVertex2d(inX, inY);
        }
        GL11.glEnd();
    }
    public void drawRegularPolygon(double x, double y, double radius, int quality, double angle, int texture){
        if(quality<3){
            throw new IllegalArgumentException("A polygon must have at least 3 sides!");
        }
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_TRIANGLES);
        for(int i = 0; i<quality; i++){
            GL11.glVertex2d(x, y);
            double X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius;
            double Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius;
            GL11.glVertex2d(X, Y);
            angle+=(360D/quality);
            X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius;
            Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius;
            GL11.glVertex2d(X, Y);
        }
        GL11.glEnd();
    }
    public void drawOval(double x, double y, double xRadius, double yRadius, double xThickness, double yThickness, int quality, int texture){
        drawOval(x, y, xRadius, yRadius, xThickness, yThickness, quality, texture, 0, quality-1);
    }
    public void drawOval(double x, double y, double xRadius, double yRadius, double thickness, int quality, int texture){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, texture, 0, quality-1);
    }
    public void drawOval(double x, double y, double xRadius, double yRadius, double thickness, int quality, int texture, int left, int right){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, texture, left, right);
    }
    public void drawOval(double x, double y, double xRadius, double yRadius, double xThickness, double yThickness, int quality, int texture, int left, int right){
        if(quality<3){
            throw new IllegalArgumentException("Quality must be >=3!");
        }
        while(left<0)left+=quality;
        while(right<0)right+=quality;
        while(left>quality)left-=quality;
        while(right>quality)right-=quality;
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        double angle = 0;
        for(int i = 0; i<quality; i++){
            boolean inRange = false;
            if(left>right)inRange = i>=left||i<=right;
            else inRange = i>=left&&i<=right;
            if(inRange){
                double X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*xRadius;
                double Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*yRadius;
                GL11.glVertex2d(X, Y);
                X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*(xRadius-xThickness);
                Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*(yRadius-yThickness);
                GL11.glVertex2d(X, Y);
            }
            angle+=(360D/quality);
            if(inRange){
                double X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*(xRadius-xThickness);
                double Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*(yRadius-yThickness);
                GL11.glVertex2d(X, Y);
                X = x+MathUtil.cos(MathUtil.toRadians(angle-90))*xRadius;
                Y = y+MathUtil.sin(MathUtil.toRadians(angle-90))*yRadius;
                GL11.glVertex2d(X, Y);
            }
        }
        GL11.glEnd();
    }
    public void setWhite(){
        setColor(Core.theme.getWhiteColor());
    }
    public void setWhite(float alpha){
        setColor(Core.theme.getWhiteColor(), alpha);
    }
    public void setColor(Color c){
        GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
    }
    public void setColor(Color c, float alpha){
        GL11.glColor4f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f*alpha);
    }
//    @Deprecated
//    public void applyAverageColor(Color c1, Color c2){
//        GL11.glColor4f((c1.getRed()+c2.getRed())/510f, (c1.getGreen()+c2.getGreen())/510f, (c1.getBlue()+c2.getBlue())/510f, (c1.getAlpha()+c2.getAlpha())/510f);
//    }
//    @Deprecated
//    public void applyAverageColor(Color c1, Color c2, float alpha){
//        GL11.glColor4f((c1.getRed()+c2.getRed())/510f, (c1.getGreen()+c2.getGreen())/510f, (c1.getBlue()+c2.getBlue())/510f, (c1.getAlpha()+c2.getAlpha())/510f*alpha);
//    }
    //VR rendering
    /**
     * Draws a cube using one texture for all sides
     * @param x1 the lower X boundary
     * @param y1 the lower Y boundary
     * @param z1 the lower Z boundary
     * @param x2 the upper X boundary
     * @param y2 the upper Y boundary
     * @param z2 the upper Z boundary
     * @param texture the texture used to render the cube
     */
    public void drawCube(double x1, double y1, double z1, double x2, double y2, double z2, int texture){
        drawCube(x1, y1, z1, x2, y2, z2, texture, (t) -> {
            return true;
        });
    }
    /**
     * Draws a cube using one texture for all sides
     * @param x1 the lower X boundary
     * @param y1 the lower Y boundary
     * @param z1 the lower Z boundary
     * @param x2 the upper X boundary
     * @param y2 the upper Y boundary
     * @param z2 the upper Z boundary
     * @param texture the texture used to render the cube
     * @param faceRenderFunc A function that defines if each face should render (Given a Direction)
     */
    public void drawCube(double x1, double y1, double z1, double x2, double y2, double z2, int texture, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        //xy +z
        if(pz){
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x1, y1, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x2, y1, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x2, y2, z2);
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x1, y2, z2);
        }
        //xy -z
        if(nz){
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x1, y1, z1);
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x1, y2, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x2, y2, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x2, y1, z1);
        }
        //xz +y
        if(py){
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x1, y2, z1);
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x1, y2, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x2, y2, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x2, y2, z1);
        }
        //xz -y
        if(ny){
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x1, y1, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x2, y1, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x2, y1, z2);
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x1, y1, z2);
        }
        //yz +x
        if(px){
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x2, y1, z1);
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x2, y2, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x2, y2, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x2, y1, z2);
        }
        //yz -x
        if(nx){
            if(texture!=0)GL11.glTexCoord2d(0, 1);
            GL11.glVertex3d(x1, y1, z1);
            if(texture!=0)GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x1, y1, z2);
            if(texture!=0)GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x1, y2, z2);
            if(texture!=0)GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x1, y2, z1);
        }
        GL11.glEnd();
    }
    /**
     * Draws a solid-colored cube outline
     * @param x1 the lower X boundary
     * @param y1 the lower Y boundary
     * @param z1 the lower Z boundary
     * @param x2 the upper X boundary
     * @param y2 the upper Y boundary
     * @param z2 the upper Z boundary
     * @param thickness the thickness of the outline
     */
    public void drawCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness){
        drawCubeOutline(x1, y1, z1, x2, y2, z2, thickness, (t) -> {
            return true;
        });
    }
    /**
     * Draws a solid-colored cube outline
     * @param x1 the lower X boundary
     * @param y1 the lower Y boundary
     * @param z1 the lower Z boundary
     * @param x2 the upper X boundary
     * @param y2 the upper Y boundary
     * @param z2 the upper Z boundary
     * @param thickness the thickness of the outline
     * @param edgeRenderFunc A function that defines if each edge should render (Given 2 Directions)
     */
    public void drawCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness, Function<Direction[], Boolean> edgeRenderFunc){
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ}))drawCube(x1, y1, z1, x2, y1+thickness, z1+thickness, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ}))drawCube(x1, y1, z1, x1+thickness, y2, z1+thickness, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY}))drawCube(x1, y1, z1, x1+thickness, y1+thickness, z2, 0);
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ}))drawCube(x2-thickness, y1, z1, x2, y2, z1+thickness, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY}))drawCube(x2-thickness, y1, z1, x2, y1+thickness, z2, 0);
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ}))drawCube(x1, y2-thickness, z1, x2, y2, z1+thickness, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX}))drawCube(x1, y2-thickness, z1, x1+thickness, y2, z2, 0);
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY}))drawCube(x1, y1, z2-thickness, x2, y1+thickness, z2, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX}))drawCube(x1, y1, z2-thickness, x1+thickness, y2, z2, 0);
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ}))drawCube(x1, y2-thickness, z2-thickness, x2, y2, z2, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ}))drawCube(x2-thickness, y1, z2-thickness, x2, y2, z2, 0);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY}))drawCube(x2-thickness, y2-thickness, z1, x2, y2, z2, 0);
    }
    public void drawPrimaryCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness, double thickness2, Function<Direction[], Boolean> edgeRenderFunc){
        double xm = (x1+x2)/2;
        double ym = (y1+y2)/2;
        double zm = (z1+z2)/2;
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, 0);//first corner
            drawCube(xm-thickness2, y1, z1, xm+thickness2, y1+thickness, z1+thickness, 0);//edge
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, 0);//first corner
            drawCube(x1, ym-thickness2, z1, x1+thickness, ym+thickness2, z1+thickness, 0);//edge
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, 0);//first corner
            drawCube(x1, y1, zm-thickness2, x1+thickness, y1+thickness, zm+thickness2, 0);//edge
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, 0);//second corner
        }
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ})){
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, 0);//first corner
            drawCube(x2-thickness, ym-thickness2, z1, x2, ym+thickness2, z1+thickness, 0);//edge
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY})){
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, 0);//first corner
            drawCube(x2-thickness, y1, zm-thickness2, x2, y1+thickness, zm+thickness2, 0);//edge
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, 0);//second corner
        }
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ})){
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, 0);//first corner
            drawCube(xm-thickness2, y2-thickness, z1, xm+thickness2, y2, z1+thickness, 0);//edge
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX})){
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, 0);//first corner
            drawCube(x1, y2-thickness, zm-thickness2, x1+thickness, y2, zm+thickness2, 0);//edge
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, 0);//second corner
        }
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY})){
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, 0);//first corner
            drawCube(xm-thickness2, y1, z2-thickness, xm+thickness2, y1+thickness, z2, 0);//edge
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX})){
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, 0);//first corner
            drawCube(x1, ym-thickness2, z2-thickness, x1+thickness, ym+thickness2, z2, 0);//edge
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, 0);//second corner
        }
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ})){
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, 0);//first corner
            drawCube(xm-thickness2, y2-thickness, z2-thickness, xm+thickness2, y2, z2, 0);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ})){
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, 0);//first corner
            drawCube(x2-thickness, ym-thickness2, z2-thickness, x2, ym+thickness2, z2, 0);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, 0);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY})){
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, 0);//first corner
            drawCube(x2-thickness, y2-thickness, zm-thickness2, x2, y2, zm+thickness2, 0);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, 0);//second corner
        }
    }
    public void drawSecondaryCubeOutline(double x1, double y1, double z1, double x2, double y2, double z2, double thickness, double thickness2, Function<Direction[], Boolean> edgeRenderFunc){
        double xm = (x1+x2)/2;
        double ym = (y1+y2)/2;
        double zm = (z1+z2)/2;
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ})){
            drawCube(x1+thickness, y1, z1, xm-thickness2, y1+thickness, z1+thickness, 0);//first sub-edge
            drawCube(xm+thickness2, y1, z1, x2-thickness, y1+thickness, z1+thickness, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ})){
            drawCube(x1, y1+thickness, z1, x1+thickness, ym-thickness2, z1+thickness, 0);//first sub-edge
            drawCube(x1, ym+thickness2, z1, x1+thickness, y2-thickness, z1+thickness, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY})){
            drawCube(x1, y1, z1+thickness, x1+thickness, y1+thickness, zm-thickness2, 0);//first sub-edge
            drawCube(x1, y1, zm+thickness2, x1+thickness, y1+thickness, z2-thickness, 0);//second sub-edge
        }
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ})){
            drawCube(x2-thickness, y1+thickness, z1, x2, ym-thickness2, z1+thickness, 0);//first sub-edge
            drawCube(x2-thickness, ym+thickness2, z1, x2, y2-thickness, z1+thickness, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY})){
            drawCube(x2-thickness, y1, z1+thickness, x2, y1+thickness, zm-thickness2, 0);//first sub-edge
            drawCube(x2-thickness, y1, zm+thickness2, x2, y1+thickness, z2-thickness, 0);//second sub-edge
        }
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ})){
            drawCube(x1+thickness, y2-thickness, z1, xm-thickness2, y2, z1+thickness, 0);//first sub-edge
            drawCube(xm+thickness2, y2-thickness, z1, x2-thickness, y2, z1+thickness, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX})){
            drawCube(x1, y2-thickness, z1+thickness, x1+thickness, y2, zm-thickness2, 0);//first sub-edge
            drawCube(x1, y2-thickness, zm+thickness2, x1+thickness, y2, z2-thickness, 0);//second sub-edge
        }
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY})){
            drawCube(x1+thickness, y1, z2-thickness, xm-thickness2, y1+thickness, z2, 0);//first sub-edge
            drawCube(xm+thickness2, y1, z2-thickness, x2-thickness, y1+thickness, z2, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX})){
            drawCube(x1, y1+thickness, z2-thickness, x1+thickness, ym-thickness2, z2, 0);//first sub-edge
            drawCube(x1, ym+thickness2, z2-thickness, x1+thickness, y2-thickness, z2, 0);//second sub-edge
        }
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ})){
            drawCube(x1+thickness, y2-thickness, z2-thickness, xm-thickness2, y2, z2, 0);//first sub-edge
            drawCube(xm+thickness2, y2-thickness, z2-thickness, x2-thickness, y2, z2, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ})){
            drawCube(x2-thickness, y1+thickness, z2-thickness, x2, ym-thickness2, z2, 0);//first sub-edge
            drawCube(x2-thickness, ym+thickness2, z2-thickness, x2, y2-thickness, z2, 0);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY})){
            drawCube(x2-thickness, y2-thickness, z1+thickness, x2, y2, zm-thickness2, 0);//first sub-edge
            drawCube(x2-thickness, y2-thickness, zm+thickness2, x2, y2, z2-thickness, 0);//second sub-edge
        }
    }
    public void fillPolygon(double[] xPoints, double[] yPoints){
        ImageStash.instance.bindTexture(0);
        int points = xPoints.length;
        if(points>4){
            double x0 = xPoints[0];
            double y0 = yPoints[0];
            for(int i = 1; i<xPoints.length-1; i++){
                fillPolygon(new double[]{x0, xPoints[i], xPoints[i+1]}, new double[]{y0, yPoints[i], yPoints[i+1]});
            }
            return;
        }
        if(points<3)throw new IllegalArgumentException("Invalid number of points for polygon: "+points);
        if(points==3)GL11.glBegin(GL11.GL_TRIANGLES);
        if(points==4)GL11.glBegin(GL11.GL_QUADS);
        for(int i = 0; i<xPoints.length; i++){
            GL11.glVertex2d(xPoints[i], yPoints[i]);
        }
        GL11.glEnd();
    }
}