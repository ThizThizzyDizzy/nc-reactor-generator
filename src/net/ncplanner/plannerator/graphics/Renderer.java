package net.ncplanner.plannerator.graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Function;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.graphics.legacyobj.model.Face;
import net.ncplanner.plannerator.graphics.legacyobj.model.Model;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
public class Renderer{
    private Font font = Core.theme.getDefaultFont();
    private static Shader shader;
    private static Stack<Bound> boundStack = new Stack<>();
    private static Matrix4fStack modelMatStack = new Matrix4fStack(64);
    private static final HashMap<String, Element> elements = new HashMap<>();
    static{
        elements.put("pencil", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();

                float[] verticies = new float[]{
                    .25f,  .75f, 0, 0, 0, 1, 0, 0, //pencil tip tip
                    .375f, .75f, 0, 0, 0, 1, 0, 0, //pencil tip right
                    .25f, .625f, 0, 0, 0, 1, 0, 0, //pencil tip top
                    .4f,  .725f, 0, 0, 0, 1, 0, 0, //pencil shaft bottom
                    .275f,  .6f, 0, 0, 0, 1, 0, 0, //pencil shaft left
                    .5f,  .375f, 0, 0, 0, 1, 0, 0, //pencil shaft top
                    .625f,  .5f, 0, 0, 0, 1, 0, 0, //pencil shaft right
                    .525f, .35f, 0, 0, 0, 1, 0, 0, //pencil eraser left
                    .65f, .475f, 0, 0, 0, 1, 0, 0, //pencil eraser bottom
                    .75f, .375f, 0, 0, 0, 1, 0, 0, //pencil eraser right
                    .625f, .25f, 0, 0, 0, 1, 0, 0  //pencil eraser top
                    
                };
                int[] indicies = new int[]{
                    0, 1, 2, //pencil tip
                    5, 4, 3, //pencil shaft left
                    3, 6, 5, //pencil shaft right
                    10, 7, 8,//pencil eraser left
                    8, 9, 10 //pencil eraser right
                };

                glBindVertexArray(vao);

                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, verticies, GL_STREAM_DRAW);

                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STREAM_DRAW);

                //pos
                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);

                //norm
                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);

                //tex
                glEnableVertexAttribArray(2);
                glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                bindTexture(0);
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 15, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("delete", new Element() {
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();

                float[] verticies = new float[]{
                    .1f, .8f, 0, 0, 0, 1, 0, 0, // / left   0
                    .2f, .9f, 0, 0, 0, 1, 0, 0, // / bottom 1
                    .8f, .1f, 0, 0, 0, 1, 0, 0, // / top    2
                    .9f, .2f, 0, 0, 0, 1, 0, 0, // / right  3
                    .1f, .2f, 0, 0, 0, 1, 0, 0, // \ left   4
                    .2f, .1f, 0, 0, 0, 1, 0, 0, // \ top    5
                    .9f, .8f, 0, 0, 0, 1, 0, 0, // \ right  6
                    .8f, .9f, 0, 0, 0, 1, 0, 0, // \ bottom 7
                };
                int[] indicies = new int[]{
                    2, 0, 1,
                    1, 3, 2,
                    5, 4, 7,
                    7, 6, 5
                };

                glBindVertexArray(vao);

                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, verticies, GL_STREAM_DRAW);

                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STREAM_DRAW);

                //pos
                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);

                //norm
                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);

                //tex
                glEnableVertexAttribArray(2);
                glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                bindTexture(0);
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 12, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
    }
    public static void initElements(){
        for(Element e : elements.values())e.init();
    }
    public static void cleanupElements(){
        for(Element e : elements.values())e.cleanup();
    }
    public void setFont(Font font){
        this.font = font;
    }
    public void resetFont(){
        font = Core.theme.getDefaultFont();
    }
    public void fillRect(float left, float top, float right, float bottom){
        if(right<left){
            float r = left;
            float l = right;
            right = r;
            left = l;
        }
        if(bottom<top){
            float b = top;
            float t = bottom;
            bottom = b;
            top = t;
        }
        unbindTexture();
        drawScreenRect(left, top, right, bottom, 1, 0, 0, 1, 1);
    }
    public void drawScreenRect(float x1, float y1, float x2, float y2, float z, float s0, float t0, float s1, float t1){
        drawScreenQuad(x1, y1, x1, y2, x2, y1, x2, y2, z, s0, t1, s0, t0, s1, t1, s1, t0);
    }
    public void fillTri(float x1, float y1, float x2, float y2, float x3, float y3){
        drawScreenTri(x1, y1, x2, y2, x3, y3, 1, 0, 0, 0, 0, 0, 0);
    }
    public void fillQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
        drawScreenQuad(x1, y1, x2, y2, x3, y3, x4, y4, 1, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    public void drawScreenTri(float x1, float y1, float x2, float y2, float x3, float y3, float z, float s0, float t0, float s1, float t1, float s2, float t2){
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        
        float[] verticies = new float[]{
            x1, y1, z,  0, 0, -1,    s0, t0,
            x2, y2, z,  0, 0, -1,    s1, t1,
            x3, y3, z,  0, 0, -1,    s2, t2
        };
        int[] indicies = new int[]{
            0, 1, 2
        };
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STREAM_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STREAM_DRAW);
        
        //pos
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);
        
        //norm
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);
        
        //tex
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);
        
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0); //actually draw it
        glBindVertexArray(0);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
    public void drawScreenQuad(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, float z, float s0, float t0, float s1, float t1, float s2, float t2, float s3, float t3){
        drawQuad(new Vector3f(x1, y1, z), new Vector3f(x2, y2, z), new Vector3f(x3, y3, z), new Vector3f(x4, y4, z), new Vector2f(s0, t0), new Vector2f(s1, t1), new Vector2f(s2, t2), new Vector2f(s3, t3), new Vector3f(0, 0, -1));
    }
    
    public void drawTri(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector3f n1, Vector3f n2, Vector3f n3){
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        
        float[] verticies = new float[]{
            p1.x, p1.y, p1.z,  n1.x, n1.y, n1.z,    uv1.x, uv1.y,
            p2.x, p2.y, p2.z,  n2.x, n2.y, n2.z,    uv2.x, uv2.y,
            p3.x, p3.y, p3.z,  n3.x, n3.y, n3.z,    uv3.x, uv3.y,
        };
        int[] indicies = new int[]{
            0, 1, 2
        };
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STREAM_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STREAM_DRAW);
        
        //pos
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);
        
        //norm
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);
        
        //tex
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);
        
        glDrawElements(GL_TRIANGLES, 3, GL_UNSIGNED_INT, 0); //actually draw it
        glBindVertexArray(0);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
    public void drawQuad(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4, Vector3f normal){
        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        
        float[] verticies = new float[]{
            p1.x, p1.y, p1.z,  normal.x, normal.y, normal.z,    uv1.x, uv1.y,//top left
            p2.x, p2.y, p2.z,  normal.x, normal.y, normal.z,    uv2.x, uv2.y,//bottom left
            p3.x, p3.y, p3.z,  normal.x, normal.y, normal.z,    uv3.x, uv3.y,//top right
            p4.x, p4.y, p4.z,  normal.x, normal.y, normal.z,    uv4.x, uv4.y//bottom right
        };
        int[] indicies = new int[]{
            1, 0, 2,
            3, 1, 2
        };
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STREAM_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STREAM_DRAW);
        
        //pos
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8*4, 0);
        
        //norm
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8*4, 3*4);
        
        //tex
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8*4, 6*4);
        
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0); //actually draw it
        glBindVertexArray(0);
        glDeleteBuffers(ebo);
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
    @Deprecated
    public void drawImage(String image, float left, float top, float right, float bottom){
        drawImage(TextureManager.getImageRaw(image), left, top, right, bottom);
    }
    public void drawImage(Image image, float left, float top, float right, float bottom){
        drawTexture(Core.getTexture(image), left, top, right, bottom);
    }
    public void drawTexture(int tex, float left, float top, float right, float bottom){
        if(right<left){
            float r = left;
            float l = right;
            right = r;
            left = l;
        }
        if(bottom<top){
            float b = top;
            float t = bottom;
            bottom = b;
            top = t;
        }
        if(tex==0)fillRect(left, top, right, bottom);
        else{
            bindTexture(tex);
            drawScreenRect(left, top, right, bottom, 1, 0, 0, 1, 1);
        }
    }
    public void drawText(float x, float y, String text, float height){
        if(height<0)return;
        bindTexture(font.texture);
        for(int i = 0; i<text.length(); i++){
            char c = text.charAt(i);
            if(c=='\n'){
                if(i==text.length()-1)continue;//last character of string, ignore
                throw new IllegalArgumentException("Cannot draw newline character!");
            }
            FontCharacter character = font.getCharacter(c);
            if(character==null){
                System.err.println("Unknown font character: "+c);
                character = font.getCharacter('?');
            }
            model(createModelMatrix(x, y+height, height, height));
            character.draw();
            x+=character.dx/font.height*height;
            y+=character.dy/font.height*height;
        }
        resetModelMatrix();
    }
    public void drawCenteredText(float left, float top, float right, float bottom, String text){
        float width = font.getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = font.getStringWidth(text, bottom-top);
        }
        drawText((left+right)/2-width/2, top, text, bottom-top);
    }
    public void drawText(float left, float top, float right, float bottom, String text){
        float width = font.getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = font.getStringWidth(text, bottom-top);
        }
        drawText(left, top, text, bottom-top);
    }
    public void drawItalicText(float left, float top, float right, float bottom, String text){
        float width = font.getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = font.getStringWidth(text, bottom-top);
        }
        drawItalicText(left, top, text, bottom-top);
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
    public void drawFormattedText(float left, float top, float right, float bottom, FormattedText text, int snap){
        if(font.getStringWidth(text.toString(), bottom-top)>right-left){
            text.trimSlightly();
            drawFormattedText(left, top, right, bottom, text, snap);
            return;
        }
        if(snap==0){
            left = (left+right)/2-font.getStringWidth(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-font.getStringWidth(text.toString(), bottom-top);
        }
        while(text!=null){
            if(text.color!=null)setColor(text.color);
            float textWidth = font.getStringWidth(text.text, bottom-top);
            if(text.italic){
                drawItalicText(left, top, right, bottom, text.text);
            }else{
                drawText(left, top, right, bottom, text.text);
            }
            if(text.bold){
                float offset = (bottom-top)/20;
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
                float topIndent = (bottom-top)*.6f;
                float bottomIndent = (bottom-top)*.3f;
                fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
            }
            if(text.underline){
                float indent = (bottom-top)*.9f;
                fillRect(left, top+indent, left+textWidth, bottom);
            }
            left+=textWidth;
            text = text.next;
        }
    }
    public FormattedText drawFormattedTextWithWrap(float left, float top, float right, float bottom, FormattedText text, int snap){
        if(font.getStringWidth(text.toString(), bottom-top)>right-left){
            String txt = text.text;
            text.trimSlightlyWithoutElipses();
            FormattedText also = drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
            txt = txt.substring(text.text.length());
            return new FormattedText(also!=null?also.text+txt:txt, text.color, text.bold, text.italic, text.underline, text.strikethrough);
        }
        if(snap==0){
            left = (left+right)/2-font.getStringWidth(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-font.getStringWidth(text.toString(), bottom-top);
        }
        if(text.color!=null)setColor(text.color);
        float textWidth = font.getStringWidth(text.text, bottom-top);
        if(text.italic){
            drawItalicText(left, top, right, bottom, text.text);
        }else{
            drawText(left, top, right, bottom, text.text);
        }
        if(text.bold){
            float offset = (bottom-top)/20;
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
            float topIndent = (bottom-top)*.6f;
            float bottomIndent = (bottom-top)*.3f;
            fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
        }
        if(text.underline){
            float indent = (bottom-top)*.9f;
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
    public FormattedText drawFormattedTextWithWordWrap(float left, float top, float right, float bottom, FormattedText text, int snap){
        ArrayList<FormattedText> words = text.split(" ");
        if(words.isEmpty())return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
        String str = words.get(0).text;
        float height = bottom-top;
        float length = right-left;
        for(int i = 1; i<words.size(); i++){
            String string = str+" "+words.get(i).text;
            if(font.getStringWidth(string.trim(), height)>=length){
                drawFormattedTextWithWrap(left, top, right, bottom, new FormattedText(str, text.color, text.bold, text.italic, text.underline, text.strikethrough), snap);
                return new FormattedText(text.text.replaceFirst("\\Q"+str, "").trim());
            }else{
                str = string;
            }
        }
        return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
    }
    private void drawItalicText(float x, float y, String text, float height){
        if(height<0)return;
        bindTexture(font.texture);
        for(int i = 0; i<text.length(); i++){
            char c = text.charAt(i);
            FontCharacter character = font.getCharacter(c);
            model(createModelMatrix(x, y+height, height, height));
            character.drawItalic();
            x+=character.dx/font.height*height;
            y+=character.dy/font.height*height;
        }
        resetModelMatrix();
    }
    public String drawTextWithWordWrap(float left, float top, float right, float bottom, String text){
        String[] words = text.split(" ");
        String str = words[0];
        float height = bottom-top;
        float length = right-left;
        for(int i = 1; i<words.length; i++){
            String string = str+" "+words[i];
            if(font.getStringWidth(string.trim(), height)>=length){
                drawTextWithWrap(left, top, right, bottom, str.trim());
                return text.replaceFirst("\\Q"+str, "").trim();
            }else{
                str = string;
            }
        }
        return drawTextWithWrap(left, top, right, bottom, text);
    }
    public String drawTextWithWrap(float left, float top, float right, float bottom, String text){
        String original = text;
        float width = font.getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = font.getStringWidth(text, bottom-top);
        }
        drawText(left, top, text, bottom-top);
        if(original.equals(text))return "";//prevent infinite loops
        return original.substring(text.length());
    }
    public void drawCircle(float x, float y, float innerRadius, float outerRadius){
        int resolution = (int)(2*MathUtil.pi()*outerRadius);
        unbindTexture();
        float angle = 0;
        for(int i = 0; i<resolution; i++){
            float x1 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*innerRadius);
            float y1 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*innerRadius);
            float x2 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*outerRadius);
            float y2 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*outerRadius);
            angle+=(360d/resolution);
            if(angle>=360)angle-=360;
            float x3 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*outerRadius);
            float y3 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*outerRadius);
            float x4 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*innerRadius);
            float y4 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*innerRadius);
            drawScreenQuad(x1, y1, x2, y2, x3, y3, x4, y4, 1, 0, 1, 0, 0, 1, 0, 1, 1);
        }
    }
    public void drawRegularPolygon(float x, float y, float radius, int quality, float angle){
        if(quality<3){
            throw new IllegalArgumentException("A polygon must have at least 3 sides!");
        }
        unbindTexture();
        for(int i = 0; i<quality; i++){
            float x2 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius);
            float y2 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius);
            angle+=(360D/quality);
            float x3 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius);
            float y3 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius);
            drawScreenTri(x, y, x2, y2, x3, y3, 1, 0, 1, 0, 0, 1, 0);
        }
    }
    public void drawOval(float x, float y, float xRadius, float yRadius, float xThickness, float yThickness, int quality){
        drawOval(x, y, xRadius, yRadius, xThickness, yThickness, quality, 0, quality-1);
    }
    public void drawOval(float x, float y, float xRadius, float yRadius, float thickness, int quality){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, 0, quality-1);
    }
    public void drawOval(float x, float y, float xRadius, float yRadius, float thickness, int quality, int left, int right){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, left, right);
    }
    public void drawOval(float x, float y, float xRadius, float yRadius, float xThickness, float yThickness, int quality, int left, int right){
        if(quality<3){
            throw new IllegalArgumentException("Quality must be >=3!");
        }
        while(left<0)left+=quality;
        while(right<0)right+=quality;
        while(left>quality)left-=quality;
        while(right>quality)right-=quality;
        unbindTexture();
        float angle = 0;
        ArrayList<float[]> points = new ArrayList<>();
        for(int i = 0; i<quality; i++){
            boolean inRange = false;
            if(left>right)inRange = i>=left||i<=right;
            else inRange = i>=left&&i<=right;
            if(inRange){
                float X = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*xRadius);
                float Y = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*yRadius);
                points.add(new float[]{X, Y});
                X = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*(xRadius-xThickness));
                Y = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*(yRadius-yThickness));
                points.add(new float[]{X, Y});
            }
            angle+=(360D/quality);
            if(inRange){
                float X = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*(xRadius-xThickness));
                float Y = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*(yRadius-yThickness));
                points.add(new float[]{X, Y});
                X = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*xRadius);
                Y = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*yRadius);
                points.add(new float[]{X, Y});
            }
            while(points.size()>=4){
                float[] p1 = points.remove(0);
                float[] p2 = points.remove(0);
                float[] p3 = points.remove(0);
                float[] p4 = points.remove(0);
                drawScreenQuad(p1[0], p1[1], p2[0], p2[1], p3[0], p3[1], p4[0], p4[1], 1, 0, 0, 0, 1, 1, 1, 1, 0);
            }
        }
    }
    public void setWhite(){
        setColor(Core.theme.getWhiteColor());
    }
    public void setWhite(float alpha){
        setColor(Core.theme.getWhiteColor(), alpha);
    }
    public void setColor(Color c){
        setColor(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f);
    }
    public void setColor(Color c, float alpha){
        setColor(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f, c.getAlpha()/255f*alpha);
    }
    public void setColor(float red, float green, float blue, float alpha){
        shader.setUniform4f("color", red, green, blue, alpha);
    }
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
    public void drawCube(float x1, float y1, float z1, float x2, float y2, float z2, Image texture){
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
    public void drawCube(float x1, float y1, float z1, float x2, float y2, float z2, Image texture, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        bindTexture(Core.getTexture(texture));
        //xy +z
        if(pz){
            drawQuad(
                    new Vector3f(x1, y1, z2),
                    new Vector3f(x1, y2, z2),
                    new Vector3f(x2, y1, z2),
                    new Vector3f(x2, y2, z2),
                    new Vector2f(0, 1),
                    new Vector2f(0, 0),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector3f(0, 0, 1));
        }
        //xy -z
        if(nz){
            drawQuad(
                    new Vector3f(x1, y1, z1),
                    new Vector3f(x2, y1, z1),
                    new Vector3f(x1, y2, z1),
                    new Vector3f(x2, y2, z1),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(0, 0),
                    new Vector2f(1, 0),
                    new Vector3f(0, 0, -1));
        }
        //xz +y
        if(py){
            drawQuad(
                    new Vector3f(x1, y2, z1),
                    new Vector3f(x2, y2, z1),
                    new Vector3f(x1, y2, z2),
                    new Vector3f(x2, y2, z2),
                    new Vector2f(0, 0),
                    new Vector2f(1, 0),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector3f(0, 1, 0));
        }
        //xz -y
        if(ny){
            drawQuad(
                    new Vector3f(x1, y1, z1),
                    new Vector3f(x1, y1, z2),
                    new Vector3f(x2, y1, z1),
                    new Vector3f(x2, y1, z2),
                    new Vector2f(0, 1),
                    new Vector2f(0, 0),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector3f(0, -1, 0));
        }
        //yz +x
        if(px){
            drawQuad(
                    new Vector3f(x2, y1, z1),
                    new Vector3f(x2, y1, z2),
                    new Vector3f(x2, y2, z1),
                    new Vector3f(x2, y2, z2),
                    new Vector2f(0, 1),
                    new Vector2f(1, 1),
                    new Vector2f(0, 0),
                    new Vector2f(1, 0),
                    new Vector3f(1, 0, 0));
        }
        //yz -x
        if(nx){
            drawQuad(
                    new Vector3f(x1, y1, z1),
                    new Vector3f(x1, y2, z1),
                    new Vector3f(x1, y1, z2),
                    new Vector3f(x1, y2, z2),
                    new Vector2f(0, 1),
                    new Vector2f(0, 0),
                    new Vector2f(1, 1),
                    new Vector2f(1, 0),
                    new Vector3f(-1, 0, 0));
        }
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
    public void drawCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness){
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
    public void drawCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, Function<Direction[], Boolean> edgeRenderFunc){
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ}))drawCube(x1, y1, z1, x2, y1+thickness, z1+thickness, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ}))drawCube(x1, y1, z1, x1+thickness, y2, z1+thickness, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY}))drawCube(x1, y1, z1, x1+thickness, y1+thickness, z2, null);
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ}))drawCube(x2-thickness, y1, z1, x2, y2, z1+thickness, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY}))drawCube(x2-thickness, y1, z1, x2, y1+thickness, z2, null);
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ}))drawCube(x1, y2-thickness, z1, x2, y2, z1+thickness, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX}))drawCube(x1, y2-thickness, z1, x1+thickness, y2, z2, null);
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY}))drawCube(x1, y1, z2-thickness, x2, y1+thickness, z2, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX}))drawCube(x1, y1, z2-thickness, x1+thickness, y2, z2, null);
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ}))drawCube(x1, y2-thickness, z2-thickness, x2, y2, z2, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ}))drawCube(x2-thickness, y1, z2-thickness, x2, y2, z2, null);
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY}))drawCube(x2-thickness, y2-thickness, z1, x2, y2, z2, null);
    }
    public void drawPrimaryCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, float thickness2, Function<Direction[], Boolean> edgeRenderFunc){
        float xm = (x1+x2)/2;
        float ym = (y1+y2)/2;
        float zm = (z1+z2)/2;
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, null);//first corner
            drawCube(xm-thickness2, y1, z1, xm+thickness2, y1+thickness, z1+thickness, null);//edge
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, null);//first corner
            drawCube(x1, ym-thickness2, z1, x1+thickness, ym+thickness2, z1+thickness, null);//edge
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY})){
            drawCube(x1, y1, z1, x1+thickness, y1+thickness, z1+thickness, null);//first corner
            drawCube(x1, y1, zm-thickness2, x1+thickness, y1+thickness, zm+thickness2, null);//edge
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, null);//second corner
        }
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ})){
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, null);//first corner
            drawCube(x2-thickness, ym-thickness2, z1, x2, ym+thickness2, z1+thickness, null);//edge
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY})){
            drawCube(x2-thickness, y1, z1, x2, y1+thickness, z1+thickness, null);//first corner
            drawCube(x2-thickness, y1, zm-thickness2, x2, y1+thickness, zm+thickness2, null);//edge
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, null);//second corner
        }
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ})){
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, null);//first corner
            drawCube(xm-thickness2, y2-thickness, z1, xm+thickness2, y2, z1+thickness, null);//edge
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX})){
            drawCube(x1, y2-thickness, z1, x1+thickness, y2, z1+thickness, null);//first corner
            drawCube(x1, y2-thickness, zm-thickness2, x1+thickness, y2, zm+thickness2, null);//edge
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, null);//second corner
        }
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY})){
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, null);//first corner
            drawCube(xm-thickness2, y1, z2-thickness, xm+thickness2, y1+thickness, z2, null);//edge
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX})){
            drawCube(x1, y1, z2-thickness, x1+thickness, y1+thickness, z2, null);//first corner
            drawCube(x1, ym-thickness2, z2-thickness, x1+thickness, ym+thickness2, z2, null);//edge
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, null);//second corner
        }
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ})){
            drawCube(x1, y2-thickness, z2-thickness, x1+thickness, y2, z2, null);//first corner
            drawCube(xm-thickness2, y2-thickness, z2-thickness, xm+thickness2, y2, z2, null);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ})){
            drawCube(x2-thickness, y1, z2-thickness, x2, y1+thickness, z2, null);//first corner
            drawCube(x2-thickness, ym-thickness2, z2-thickness, x2, ym+thickness2, z2, null);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, null);//second corner
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY})){
            drawCube(x2-thickness, y2-thickness, z1, x2, y2, z1+thickness, null);//first corner
            drawCube(x2-thickness, y2-thickness, zm-thickness2, x2, y2, zm+thickness2, null);//edge
            drawCube(x2-thickness, y2-thickness, z2-thickness, x2, y2, z2, null);//second corner
        }
    }
    public void drawSecondaryCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, float thickness2, Function<Direction[], Boolean> edgeRenderFunc){
        float xm = (x1+x2)/2;
        float ym = (y1+y2)/2;
        float zm = (z1+z2)/2;
        //111 to XYZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.NY,Direction.NZ})){
            drawCube(x1+thickness, y1, z1, xm-thickness2, y1+thickness, z1+thickness, null);//first sub-edge
            drawCube(xm+thickness2, y1, z1, x2-thickness, y1+thickness, z1+thickness, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NZ})){
            drawCube(x1, y1+thickness, z1, x1+thickness, ym-thickness2, z1+thickness, null);//first sub-edge
            drawCube(x1, ym+thickness2, z1, x1+thickness, y2-thickness, z1+thickness, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.NX,Direction.NY})){
            drawCube(x1, y1, z1+thickness, x1+thickness, y1+thickness, zm-thickness2, null);//first sub-edge
            drawCube(x1, y1, zm+thickness2, x1+thickness, y1+thickness, z2-thickness, null);//second sub-edge
        }
        //X2 to YZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NZ})){
            drawCube(x2-thickness, y1+thickness, z1, x2, ym-thickness2, z1+thickness, null);//first sub-edge
            drawCube(x2-thickness, ym+thickness2, z1, x2, y2-thickness, z1+thickness, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.NY})){
            drawCube(x2-thickness, y1, z1+thickness, x2, y1+thickness, zm-thickness2, null);//first sub-edge
            drawCube(x2-thickness, y1, zm+thickness2, x2, y1+thickness, z2-thickness, null);//second sub-edge
        }
        //Y2 to XZ
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NZ})){
            drawCube(x1+thickness, y2-thickness, z1, xm-thickness2, y2, z1+thickness, null);//first sub-edge
            drawCube(xm+thickness2, y2-thickness, z1, x2-thickness, y2, z1+thickness, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.NX})){
            drawCube(x1, y2-thickness, z1+thickness, x1+thickness, y2, zm-thickness2, null);//first sub-edge
            drawCube(x1, y2-thickness, zm+thickness2, x1+thickness, y2, z2-thickness, null);//second sub-edge
        }
        //Z2 to XY
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NY})){
            drawCube(x1+thickness, y1, z2-thickness, xm-thickness2, y1+thickness, z2, null);//first sub-edge
            drawCube(xm+thickness2, y1, z2-thickness, x2-thickness, y1+thickness, z2, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PZ,Direction.NX})){
            drawCube(x1, y1+thickness, z2-thickness, x1+thickness, ym-thickness2, z2, null);//first sub-edge
            drawCube(x1, ym+thickness2, z2-thickness, x1+thickness, y2-thickness, z2, null);//second sub-edge
        }
        //XYZ to 222
        if(edgeRenderFunc.apply(new Direction[]{Direction.PY,Direction.PZ})){
            drawCube(x1+thickness, y2-thickness, z2-thickness, xm-thickness2, y2, z2, null);//first sub-edge
            drawCube(xm+thickness2, y2-thickness, z2-thickness, x2-thickness, y2, z2, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PZ})){
            drawCube(x2-thickness, y1+thickness, z2-thickness, x2, ym-thickness2, z2, null);//first sub-edge
            drawCube(x2-thickness, ym+thickness2, z2-thickness, x2, y2-thickness, z2, null);//second sub-edge
        }
        if(edgeRenderFunc.apply(new Direction[]{Direction.PX,Direction.PY})){
            drawCube(x2-thickness, y2-thickness, z1+thickness, x2, y2, zm-thickness2, null);//first sub-edge
            drawCube(x2-thickness, y2-thickness, zm+thickness2, x2, y2, z2-thickness, null);//second sub-edge
        }
    }
    public void fillPolygon(float[] xPoints, float[] yPoints){
        unbindTexture();
        int points = xPoints.length;
        if(points>4){
            float x0 = xPoints[0];
            float y0 = yPoints[0];
            for(int i = 1; i<xPoints.length-1; i++){
                fillPolygon(new float[]{x0, xPoints[i], xPoints[i+1]}, new float[]{y0, yPoints[i], yPoints[i+1]});
            }
            return;
        }
        if(points<3)throw new IllegalArgumentException("Invalid number of points for polygon: "+points);
        if(points==3){
            drawScreenTri(xPoints[0], yPoints[0], xPoints[1], yPoints[1], xPoints[2], yPoints[2], 1, 0, 0, 0, 0, 0, 0);
        }
        if(points==4){
            drawScreenQuad(xPoints[0], yPoints[0], xPoints[1], yPoints[1], xPoints[2], yPoints[2], xPoints[3], yPoints[3], 1, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }
    public void setShader(Shader shader){
        Renderer.shader = shader;
        glUseProgram(shader.shaderID);
    }
    Matrix4f createModelMatrix(float x, float y, float scaleX, float scaleY){
        return new Matrix4f().setTranslation(x, y, 0).scaleXY(scaleX, scaleY);
    }
    public void model(Matrix4f matrix){
        setExactModelMatrix(modelMatStack.mul(matrix, matrix));
    }
    public void setModel(Matrix4f matrix){
        resetModelMatrix();
        model(matrix);
    }
    private void setExactModelMatrix(Matrix4f matrix){
        glUniformMatrix4fv(glGetUniformLocation(shader.shaderID, "model"), false, matrix.get(new float[16]));
    }
    public void resetModelMatrix(){
        model(new Matrix4f());
    }
    public void view(Matrix4f matrix){
        glUniformMatrix4fv(glGetUniformLocation(shader.shaderID, "view"), false, matrix.get(new float[16]));
    }
    public void projection(Matrix4f matrix){
        glUniformMatrix4fv(glGetUniformLocation(shader.shaderID, "projection"), false, matrix.get(new float[16]));
    }
    public void bindTexture(Image tex){
        bindTexture(Core.getTexture(tex));
    }
    public void unbindTexture(){
        bindTexture(0);
    }
    private static void bindTexture(int tex){
        glBindTexture(GL_TEXTURE_2D, tex);
        if(tex==0)shader.setUniform4f("noTex", 1f, 1f, 1f, 0f);
        else shader.setUniform4f("noTex", 0f, 0f, 0f, 0f);
    }
    public void bound(float left, float top, float right, float bottom){
        boundStack.push(new Bound(modelMatStack.get(new Matrix4f())){
            @Override
            void draw(){
                fillRect(0, 0, left, Core.gui.getHeight());
                fillRect(left, 0, right, top);
                fillRect(left, bottom, right, Core.gui.getHeight());
                fillRect(right, 0, Core.gui.getWidth(), Core.gui.getHeight());
            }
        });
        redrawStencil();
    }
    public void translate(float x, float y){
        translate(x, y, 1, 1);
    }
    public void translate(float x, float y, float sx, float sy){
        modelMatStack.pushMatrix();
//        createModelMatrix(x, y, sx, sy).mul(modelMatStack, modelMatStack);
        modelMatStack.mul(createModelMatrix(x, y, sx, sy));
        resetModelMatrix();
    }
    public void redrawStencil(){
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilFunc(GL_ALWAYS, 1, 0xff);
        glStencilMask(0xff);
        glClearColor(0f, 0f, 0f, 0f);
        glClear(GL_STENCIL_BUFFER_BIT);
        glColorMask(false, false, false, false);
        glDepthMask(false);
        for(int i = 0; i<boundStack.size(); i++){
            Bound bound = boundStack.get(i);
            setExactModelMatrix(bound.modelMatrix);
            bound.draw();
        }
        glStencilFunc(GL_NOTEQUAL, 1, 0xff);
        glDepthMask(true);
        glColorMask(true, true, true, true);
        glStencilMask(0x00);
        resetModelMatrix();
    }
    public void unTranslate(){
        modelMatStack.popMatrix();
        resetModelMatrix();
    }
    public void unBound(){
        boundStack.pop();
        redrawStencil();
    }
    public float getStringWidth(String text, float height){
        return font.getStringWidth(text, height);
    }
    public void drawGear(float x, float y, float holeRad, int teeth, float averageRadius, float toothSize, float rot){
        int resolution = (int)(2*Math.PI*averageRadius*2/teeth);//an extra *2 to account for wavy surface?
        double angle = rot;
        double radius = averageRadius+toothSize/2;
        for(int i = 0; i<teeth*resolution; i++){
            float x1 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*holeRad);
            float y1 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*holeRad);
            float x2 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius);
            float y2 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius);
            angle+=(360d/(teeth*resolution));
            if(angle>=360)angle-=360;
            radius = averageRadius+(toothSize/2)*MathUtil.cos(MathUtil.toRadians(teeth*(angle-rot)));
            float x3 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*radius);
            float y3 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*radius);
            float x4 = (float)(x+MathUtil.cos(MathUtil.toRadians(angle-90))*holeRad);
            float y4 = (float)(y+MathUtil.sin(MathUtil.toRadians(angle-90))*holeRad);
            drawScreenQuad(x1, y1, x2, y2, x3, y3, x4, y4, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }
    public void clearTranslationsAndBounds(){
        clearTranslations();
        clearBounds();
    }
    public void clearTranslations(){
        modelMatStack.clear();
        resetModelMatrix();
    }
    public void clearBounds(){
        boundStack.clear();
        redrawStencil();
    }
    public void drawElement(String name, float x, float y, float width, float height){
        model(new Matrix4f().setTranslation(x, y, 1).scale(width, height, 0));
        drawElement(name);
        resetModelMatrix();
    }
    private void drawElement(String name){
        if(!elements.containsKey(name))throw new IllegalArgumentException("Cannot draw element: "+name+" does not exist!");
        elements.get(name).draw();
    }
    private static abstract class Bound{
        private final Matrix4f modelMatrix;
        public Bound(Matrix4f modelMatrix){
            this.modelMatrix = modelMatrix;
        }
        abstract void draw();
    }
    @Deprecated
    public void drawModel(Model model){
        if(model==null){
            return;
        }
        model(new Matrix4f().setTranslation(model.origin.x, model.origin.y, model.origin.z));
        int oldTexture = -1;
        int oldPolygonSize = 0;
        for(Face face : model.faces){
            int texture = face.getTexture();
            int polygonSize = face.verticies.size();
            if(oldTexture!=texture||oldPolygonSize!=polygonSize){
                bindTexture(texture);
            }
            if(face.colorOverride!=null){
                setColor(face.colorOverride.getRed()/255f, face.colorOverride.getGreen()/255f, face.colorOverride.getBlue()/255f, face.colorOverride.getAlpha()/255f);
            }
            oldTexture = texture;
            oldPolygonSize = polygonSize;
            for(int i = 0; i < face.verticies.size()-2; i++){
                int vert1 = face.verticies.get(0);
                int vert2 = face.verticies.get(i+1);
                int vert3 = face.verticies.get(i+2);
                Vector3f p1 = model.verticies.get(vert1-1);
                Vector3f p2 = model.verticies.get(vert2-1);
                Vector3f p3 = model.verticies.get(vert3-1);
                Vector2f uv1 = new Vector2f();
                Vector2f uv2 = new Vector2f();
                Vector2f uv3 = new Vector2f();
                Vector3f n1 = new Vector3f();
                Vector3f n2 = new Vector3f();
                Vector3f n3 = new Vector3f();
                if(face.textureCoords.size()>0){
                    float[] uv = model.textures.get(face.textureCoords.get(i)-1);
                    uv1.set(uv[0], uv[1]);
                    uv = model.textures.get(face.textureCoords.get(i+1)-1);
                    uv2.set(uv[0], uv[1]);
                    uv = model.textures.get(face.textureCoords.get(i+2)-1);
                    uv3.set(uv[0], uv[1]);
                }
                if(face.normals.size()>0){
                    n1 = model.normals.get((int)face.normals.get(i)-1);
                    n2 = model.normals.get((int)face.normals.get(i+1)-1);
                    n3 = model.normals.get((int)face.normals.get(i+2)-1);
                }
                drawTri(p1, p2, p3, uv1, uv2, uv3, n1, n2, n3);
            }
        }
        resetModelMatrix();
        setWhite();
    }
    public static interface Element{
        public void init();
        public void draw();
        public void cleanup();
    }
}