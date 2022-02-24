package net.ncplanner.plannerator.graphics;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import static org.lwjgl.stb.STBTruetype.*;
public class FontCharacter{
    private final Font font;
    private final char c;
    public float dx, dy;//move writing point to here after this character
    private int vao, ivao;
    public FontCharacter(Font font, char c){
        this.font = font;
        this.c = c;
        init();
        initItalic(0.25f);
    }
    private void init(){
        vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        
        STBTTAlignedQuad quad = STBTTAlignedQuad.create();
        float[] xpos = new float[1];
        float[] ypos = new float[1];
        stbtt_GetBakedQuad(font.charBuffer, font.bitmapSize, font.bitmapSize, c, xpos, ypos, quad, true);
        dx = xpos[0];
        dy = ypos[0];
        
        float[] verticies = new float[]{
            quad.x0()/font.height, quad.y1()/font.height, 0,    0, 0, 0,    quad.s0()/4, quad.t1()/4,//0, 1, //top left
            quad.x0()/font.height, quad.y0()/font.height, 0,    0, 0, 0,    quad.s0()/4, quad.t0()/4,//0, 0, //bottom left
            quad.x1()/font.height, quad.y1()/font.height, 0,    0, 0, 0,    quad.s1()/4, quad.t1()/4,//1, 1, //top right
            quad.x1()/font.height, quad.y0()/font.height, 0,    0, 0, 0,    quad.s1()/4, quad.t0()/4//1, 0, //bottom right
        };
        int[] indicies = new int[]{
            1, 0, 2,
            3, 1, 2
        };
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);
        
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
    private void initItalic(float tilt){
        ivao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        
        STBTTAlignedQuad quad = STBTTAlignedQuad.create();
        float[] xpos = new float[1];
        float[] ypos = new float[1];
        stbtt_GetBakedQuad(font.charBuffer, font.bitmapSize, font.bitmapSize, c, xpos, ypos, quad, true);
        dx = xpos[0];
        dy = ypos[0];
        
        float[] verticies = new float[]{
            quad.x0()/font.height+tilt, -quad.y0()/font.height, 0,    0, 0, 0,    quad.s0()/4, quad.t1()/4,//0, 1, //top left
            quad.x0()/font.height, -quad.y1()/font.height, 0,    0, 0, 0,    quad.s0()/4, quad.t0()/4,//0, 0, //bottom left
            quad.x1()/font.height+tilt, -quad.y0()/font.height, 0,    0, 0, 0,    quad.s1()/4, quad.t1()/4,//1, 1, //top right
            quad.x1()/font.height, -quad.y1()/font.height, 0,    0, 0, 0,    quad.s1()/4, quad.t0()/4//1, 0, //bottom right
        };
        int[] indicies = new int[]{
            1, 0, 2,
            3, 1, 2
        };
        
        glBindVertexArray(ivao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicies, GL_STATIC_DRAW);
        
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
    /**
     * Draw the font character. TEXTURE MUST ALREADY BE BOUND!
     */
    public void draw(){
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
    /**
     * Draw the font character italicized. TEXTURE MUST ALREADY BE BOUND!
     */
    public void drawItalic(){
        glBindVertexArray(ivao);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}