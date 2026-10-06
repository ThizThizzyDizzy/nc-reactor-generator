package net.ncplanner.plannerator.graphics;
import static com.thizthizzydizzy.dizzyengine.graphics.Renderer.bindTexture;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.planner.Core;
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
        bindTexture(Math.max(0,Core.getTexture(texture)));
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
        bindTexture(Math.max(0,Core.getTexture(texture)));
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
}