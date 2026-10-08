package net.ncplanner.plannerator.graphics;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import static com.thizthizzydizzy.dizzyengine.graphics.Renderer.bindTexture;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import java.util.function.Function;
import java.util.ArrayList;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import net.ncplanner.plannerator.graphics.legacyobj.model.Face;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.multiblock.Direction;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
public class PlanneratorRenderer extends com.thizthizzydizzy.dizzyengine.graphics.Renderer{
    @Deprecated
    public static void addCustomElements(){
        elements.put("cube_pz", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    0, 0, 1, 0, 0, 1, 0, 1,
                    0, 1, 1, 0, 0, 1, 0, 0,
                    1, 0, 1, 0, 0, 1, 1, 1,
                    1, 1, 1, 0, 0, 1, 1, 0
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("cube_nz", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    0, 0, 0, 0, 0, -1, 0, 1,
                    1, 0, 0, 0, 0, -1, 1, 1,
                    0, 1, 0, 0, 0, -1, 0, 0,
                    1, 1, 0, 0, 0, -1, 1, 0
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("cube_py", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    0, 1, 0, 0, 1, 0, 0, 0,
                    1, 1, 0, 0, 1, 0, 1, 0,
                    0, 1, 1, 0, 1, 0, 0, 1,
                    1, 1, 1, 0, 1, 0, 1, 1
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("cube_ny", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    0, 0, 0, 0, -1, 0, 0, 1,
                    0, 0, 1, 0, -1, 0, 0, 0,
                    1, 0, 0, 0, -1, 0, 1, 1,
                    1, 0, 1, 0, -1, 0, 1, 0
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("cube_px", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    1, 0, 0, 1, 0, 0, 0, 1,
                    1, 0, 1, 1, 0, 0, 1, 1,
                    1, 1, 0, 1, 0, 0, 0, 0,
                    1, 1, 1, 1, 0, 0, 1, 0
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
        elements.put("cube_nx", new Element(){
            public int vao, vbo, ebo;
            @Override
            public void init(){
                vao = glGenVertexArrays();
                vbo = glGenBuffers();
                ebo = glGenBuffers();
                float[] verticies = new float[]{
                    0, 0, 0, -1, 0, 0, 0, 1,
                    0, 1, 0, -1, 0, 0, 0, 0,
                    0, 0, 1, -1, 0, 0, 1, 1,
                    0, 1, 1, -1, 0, 0, 1, 0
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

                glBindVertexArray(0);
            }
            @Override
            public void draw(){
                glBindVertexArray(vao);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }
            @Override
            public void cleanup(){
                glDeleteBuffers(ebo);
                glDeleteBuffers(vbo);
                glDeleteVertexArrays(vao);
            }
        });
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
    public static void drawCube(float x1, float y1, float z1, float x2, float y2, float z2, Image texture){
        bindTexture(Math.max(0,ResourceManager.getTexture(texture)));
        drawElement("cube", x1, y1, z1, x2-x1, y2-y1, z2-z1);
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
    public static void drawCube(float x1, float y1, float z1, float x2, float y2, float z2, Image texture, Function<Direction, Boolean> faceRenderFunc){
        boolean px = faceRenderFunc.apply(Direction.PX);
        boolean py = faceRenderFunc.apply(Direction.PY);
        boolean pz = faceRenderFunc.apply(Direction.PZ);
        boolean nx = faceRenderFunc.apply(Direction.NX);
        boolean ny = faceRenderFunc.apply(Direction.NY);
        boolean nz = faceRenderFunc.apply(Direction.NZ);
        if(!px&&!py&&!pz&&!nx&&!ny&&!nz)return;//no faces are actually rendering, save some GL calls
        bindTexture(Math.max(0,ResourceManager.getTexture(texture)));
        if(pz)drawElement("cube_pz", x1, y1, z1, x2-x1, y2-y1, z2-z1);
        if(nz)drawElement("cube_nz", x1, y1, z1, x2-x1, y2-y1, z2-z1);
        if(py)drawElement("cube_py", x1, y1, z1, x2-x1, y2-y1, z2-z1);
        if(ny)drawElement("cube_ny", x1, y1, z1, x2-x1, y2-y1, z2-z1);
        if(px)drawElement("cube_px", x1, y1, z1, x2-x1, y2-y1, z2-z1);
        if(nx)drawElement("cube_nx", x1, y1, z1, x2-x1, y2-y1, z2-z1);
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
    public static void drawCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness){
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
    public static void drawCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, Function<Direction[], Boolean> edgeRenderFunc){
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
    public static void drawPrimaryCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, float thickness2, Function<Direction[], Boolean> edgeRenderFunc){
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
    public static void drawSecondaryCubeOutline(float x1, float y1, float z1, float x2, float y2, float z2, float thickness, float thickness2, Function<Direction[], Boolean> edgeRenderFunc){
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
    private static final org.joml.Matrix4fStack clipTransform = new org.joml.Matrix4fStack(256);
    private static final java.util.ArrayDeque<int[]> clips = new java.util.ArrayDeque<>();
    private static float clipScaleX=1,clipScaleY=1;
    public static void resetClipping(){resetClipping(1,1);}
    public static void resetClipping(float sx,float sy){
        clipScaleX=sx;clipScaleY=sy;
        clipTransform.clear();
        clips.clear();
        glDisable(GL_SCISSOR_TEST);
    }
    private static final java.util.ArrayDeque<java.util.ArrayDeque<int[]>> offscreenClips = new java.util.ArrayDeque<>();
    private static final java.util.ArrayDeque<float[]> offscreenScales = new java.util.ArrayDeque<>();
    public static void beginOffscreen(){
        offscreenScales.push(new float[]{clipScaleX,clipScaleY});
        clipScaleX=clipScaleY=1;
        pushModel(new Matrix4f(clipTransform).invert());
        clipTransform.pushMatrix().identity();
        offscreenClips.push(new java.util.ArrayDeque<>(clips));
        clips.clear();
        glDisable(GL_SCISSOR_TEST);
    }
    public static void endOffscreen(){
        popModel();
        clipTransform.popMatrix();
        clips.clear();
        clips.addAll(offscreenClips.pop());
        float[] scale=offscreenScales.pop();clipScaleX=scale[0];clipScaleY=scale[1];
        applyClip();
    }
    public static void translate(float x,float y){translate(x,y,1,1);}
    public static void translate(float x,float y,float sx,float sy){
        clipTransform.pushMatrix().translate(x,y,0).scale(sx,sy,1);
        com.thizthizzydizzy.dizzyengine.graphics.Renderer.translate(x,y,sx,sy);
    }
    public static void unTranslate(){
        clipTransform.popMatrix();
        com.thizthizzydizzy.dizzyengine.graphics.Renderer.unTranslate();
    }
    public static void bound(float left,float top,float right,float bottom){
        org.joml.Vector3f a=clipTransform.transformPosition(new org.joml.Vector3f(left,top,0));
        org.joml.Vector3f b=clipTransform.transformPosition(new org.joml.Vector3f(right,bottom,0));
        int[] rect={(int)Math.floor(Math.min(a.x,b.x)),(int)Math.floor(Math.min(a.y,b.y)),
            (int)Math.ceil(Math.max(a.x,b.x)),(int)Math.ceil(Math.max(a.y,b.y))};
        if(!clips.isEmpty()){
            int[] parent=clips.peek();
            rect[0]=Math.max(rect[0],parent[0]);rect[1]=Math.max(rect[1],parent[1]);
            rect[2]=Math.min(rect[2],parent[2]);rect[3]=Math.min(rect[3],parent[3]);
        }
        clips.push(rect);
        applyClip();
    }
    public static void unBound(){clips.pop();applyClip();}
    private static void applyClip(){
        if(clips.isEmpty()){glDisable(GL_SCISSOR_TEST);return;}
        int[] viewport=new int[4];glGetIntegerv(GL_VIEWPORT,viewport);
        int[] rect=clips.peek();
        glEnable(GL_SCISSOR_TEST);
        int left=(int)Math.floor(rect[0]*clipScaleX),top=(int)Math.floor(rect[1]*clipScaleY);
        int right=(int)Math.ceil(rect[2]*clipScaleX),bottom=(int)Math.ceil(rect[3]*clipScaleY);
        glScissor(viewport[0]+left,viewport[1]+viewport[3]-bottom,Math.max(0,right-left),Math.max(0,bottom-top));
    }
    public static void setWhite(){setColor(Color.WHITE);}
    public static void setWhite(float alpha){setColor(Color.WHITE,alpha);}
    public static void drawImage(Image image,float left,float top,float right,float bottom){
        fillRect(left,top,right,bottom,ResourceManager.getTexture(image));
    }
    public static void drawImage(String path,float left,float top,float right,float bottom){
        fillRect(left,top,right,bottom,ResourceManager.getTexture(path));
    }
    public static void bindTexture(Image image){bindTexture(ResourceManager.getTexture(image));}
    public static void drawTexture(int texture,float left,float top,float right,float bottom){
        fillRect(left,top,right,bottom,texture);
    }

    public static void drawItalicText(float left, float top, float right, float bottom, String text){
        float width = getFont().getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = getFont().getStringWidth(text, bottom-top);
        }
        drawItalicText(left, top, text, bottom-top);
    }
    public static void drawFormattedText(float left, float top, float right, float bottom, FormattedText text, int snap){
        if(getFont().getStringWidth(text.toString(), bottom-top)>right-left){
            text.trimSlightly();
            drawFormattedText(left, top, right, bottom, text, snap);
            return;
        }
        if(snap==0){
            left = (left+right)/2-getFont().getStringWidth(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-getFont().getStringWidth(text.toString(), bottom-top);
        }
        while(text!=null){
            if(text.color!=null)setColor(text.color);
            float textWidth = getFont().getStringWidth(text.text, bottom-top);
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
            if(text.strikethrough!=null){
                setColor(text.strikethrough);
                float topIndent = (bottom-top)*.6f;
                float bottomIndent = (bottom-top)*.3f;
                fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
            }
            if(text.underline!=null){
                setColor(text.underline);
                float indent = (bottom-top)*.9f;
                fillRect(left, top+indent, left+textWidth, bottom);
            }
            left+=textWidth;
            text = text.next;
        }
    }
    public static void drawRegularPolygon(float x, float y, float radius, int quality, float angle){
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
    public static void drawOval(float x, float y, float xRadius, float yRadius, float xThickness, float yThickness, int quality){
        drawOval(x, y, xRadius, yRadius, xThickness, yThickness, quality, 0, quality-1);
    }
    public static void drawOval(float x, float y, float xRadius, float yRadius, float thickness, int quality){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, 0, quality-1);
    }
    public static void drawOval(float x, float y, float xRadius, float yRadius, float thickness, int quality, int left, int right){
        drawOval(x, y, xRadius, yRadius, thickness, thickness, quality, left, right);
    }
    public static void drawOval(float x, float y, float xRadius, float yRadius, float xThickness, float yThickness, int quality, int left, int right){
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
    public static void drawGear(float x, float y, float holeRad, int teeth, float averageRadius, float toothSize, float rot){
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

    public static FormattedText drawFormattedTextWithWordWrap(float left, float top, float right, float bottom, FormattedText text, int snap){
        ArrayList<FormattedText> words = text.split(" ");
        if(words.isEmpty())return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
        String str = words.get(0).text;
        float height = bottom-top;
        float length = right-left;
        for(int i = 1; i<words.size(); i++){
            String string = str+" "+words.get(i).text;
            if(getFont().getStringWidth(string.trim(), height)>=length){
                drawFormattedTextWithWrap(left, top, right, bottom, new FormattedText(str, text.color, text.bold, text.italic, text.underline, text.strikethrough), snap);
                return new FormattedText(text.text.replaceFirst("\\Q"+str, "").trim());
            }else{
                str = string;
            }
        }
        return drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
    }
    private static void drawItalicText(float x, float y, String text, float height){
        if(height<0)return;
        bindTexture(getFont().texture);
        for(int i = 0; i<text.length(); i++){
            char c = text.charAt(i);
            com.thizthizzydizzy.dizzyengine.graphics.text.FontCharacter character = getFont().getCharacter(c);
            model(new Matrix4f().translate(x, y+height, 0).scale(height, height, 1));
            character.drawItalic();
            x+=character.dx/getFont().height*height;
            y+=character.dy/getFont().height*height;
        }
        resetModelMatrix();
    }
    public static String drawTextWithWordWrap(float left, float top, float right, float bottom, String text){
        String[] words = text.split(" ");
        String str = words[0];
        float height = bottom-top;
        float length = right-left;
        for(int i = 1; i<words.length; i++){
            String string = str+" "+words[i];
            if(getFont().getStringWidth(string.trim(), height)>=length){
                drawTextWithWrap(left, top, right, bottom, str.trim());
                return text.replaceFirst("\\Q"+str, "").trim();
            }else{
                str = string;
            }
        }
        return drawTextWithWrap(left, top, right, bottom, text);
    }
    public static void drawModel(net.ncplanner.plannerator.graphics.legacyobj.model.Model model){
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

    public static FormattedText drawFormattedTextWithWrap(float left, float top, float right, float bottom, FormattedText text, int snap){
        if(getFont().getStringWidth(text.toString(), bottom-top)>right-left){
            String txt = text.text;
            while(getFont().getStringWidth(text.toString(), bottom-top)>right-left&&text.toString().length()>0){
                text.trimSlightlyWithoutElipses();
            }
            FormattedText also = drawFormattedTextWithWrap(left, top, right, bottom, text, snap);
            txt = txt.substring(text.text.length());
            return new FormattedText(also!=null?also.text+txt:txt, text.color, text.bold, text.italic, text.underline, text.strikethrough);
        }
        if(snap==0){
            left = (left+right)/2-getFont().getStringWidth(text.toString(), bottom-top)/2;
        }
        if(snap>0){
            left = right-getFont().getStringWidth(text.toString(), bottom-top);
        }
        if(text.color!=null)setColor(text.color);
        float textWidth = getFont().getStringWidth(text.text, bottom-top);
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
        if(text.strikethrough!=null){
            setColor(text.strikethrough);
            float topIndent = (bottom-top)*.6f;
            float bottomIndent = (bottom-top)*.3f;
            fillRect(left, top+topIndent, left+textWidth, bottom-bottomIndent);
        }
        if(text.underline!=null){
            setColor(text.underline);
            float indent = (bottom-top)*.9f;
            fillRect(left, top+indent, left+textWidth, bottom);
        }
        left+=textWidth;
        if(text.next!=null){
            return drawFormattedTextWithWrap(left+textWidth, top, right, bottom, text, snap);
        }
        return null;
    }
    public static String drawTextWithWrap(float left, float top, float right, float bottom, String text){
        String original = text;
        float width = getFont().getStringWidth(text, bottom-top);
        while(width>right-left&&!text.isEmpty()){
            text = text.substring(0, text.length()-1);
            width = getFont().getStringWidth(text, bottom-top);
        }
        drawText(left, top, text, bottom-top);
        if(original.equals(text))return "";//prevent infinite loops
        return original.substring(text.length());
    }
}
