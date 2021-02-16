package planner.editor.tool;
import multiblock.Block;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.editor.Editor;
import planner.vr.VRCore;
import simplelibrary.opengl.Renderer2D;
public class MoveTool extends EditorTool{
    public MoveTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] leftDragStart;
    private int[] leftDragEnd;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        double w = width/16;
        double h = height/16;
        Renderer2D.drawRect(x+width/2-w, y+height/4, x+width/2+w, y+height*3/4, 0);
        Renderer2D.drawRect(x+width/4, y+height/2-h, x+width*3/4, y+height/2+h, 0);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(width/4+w, height/4);
        GL11.glVertex2d(width/2, h);
        GL11.glVertex2d(width*3/4-w, height/4);
        
        GL11.glVertex2d(width/4+w, height*3/4);
        GL11.glVertex2d(width/2, height-h);
        GL11.glVertex2d(width*3/4-w, height*3/4);
        
        GL11.glVertex2d(width/4, height/4+w);
        GL11.glVertex2d(w, height/2);
        GL11.glVertex2d(width/4, height*3/4-h);
        
        GL11.glVertex2d(width*3/4, height/4+h);
        GL11.glVertex2d(width-w, height/2);
        GL11.glVertex2d(width*3/4, height*3/4-h);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(int[] i : editor.getSelection(id)){
                        if(i[1]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[2]*blockSize, x+(i[0]+1)*blockSize, y+(i[2]+1)*blockSize, 0);
                    }
                }
            }
            int[] diff = new int[]{leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]};
            synchronized(editor.getSelection(id)){
                for(int[] i : editor.getSelection(id)){
                    int[] j = new int[]{i[0]+diff[0], i[1]+diff[1], i[2]+diff[2]};
                    if(j[0]<0||j[1]<0||j[2]<0||j[0]>=editor.getMultiblock().getX()||j[1]>=editor.getMultiblock().getY()||j[2]>=editor.getMultiblock().getZ())continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    if(j[1]==layer)Renderer2D.drawRect(x+j[0]*blockSize, y+j[2]*blockSize, x+(j[0]+1)*blockSize, y+(j[2]+1)*blockSize, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(int[] i : editor.getSelection(id)){
                        if(i[2]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[1]*blockSize, x+(i[0]+1)*blockSize, y+(i[1]+1)*blockSize, 0);
                    }
                }
            }
            int[] diff = new int[]{leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]};
            synchronized(editor.getSelection(id)){
                for(int[] i : editor.getSelection(id)){
                    int[] j = new int[]{i[0]+diff[0], i[1]+diff[1], i[2]+diff[2]};
                    if(j[0]<0||j[1]<0||j[2]<0||j[0]>=editor.getMultiblock().getX()||j[1]>=editor.getMultiblock().getY()||j[2]>=editor.getMultiblock().getZ())continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    if(j[2]==layer)Renderer2D.drawRect(x+j[0]*blockSize, y+j[1]*blockSize, x+(j[0]+1)*blockSize, y+(j[1]+1)*blockSize, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(int[] i : editor.getSelection(id)){
                        Renderer2D.drawRect(x+(i[2]-1)*blockSize, y, x+i[2]*blockSize, y+blockSize, 0);
                    }
                }
            }
            int[] diff = new int[]{leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]};
            synchronized(editor.getSelection(id)){
                for(int[] i : editor.getSelection(id)){
                    int[] j = new int[]{i[0]+diff[0], i[1]+diff[1], i[2]+diff[2]};
                    if(j[0]<0||j[1]<0||j[2]<0||j[0]>=editor.getMultiblock().getX()||j[1]>=editor.getMultiblock().getY()||j[2]>=editor.getMultiblock().getZ())continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    Renderer2D.drawRect(x+(j[2]-1)*blockSize, y, x+j[2]*blockSize, y+blockSize, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            double border = blockSize/64;
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(int[] i : editor.getSelection(id)){
                        if(editor.getMultiblock().getBlock(i[0], i[1], i[2])==null)continue;//already air
                        VRCore.drawCube(x+i[0]*blockSize-border/2, y+i[1]*blockSize-border/2, z+i[2]*blockSize-border/2, x+(i[0]+1)*blockSize+border/2, y+(i[1]+1)*blockSize+border/2, z+(i[2]+1)*blockSize+border/2, 0);
                    }
                }
            }
            int[] diff = new int[]{leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]};
            synchronized(editor.getSelection(id)){
                for(int[] i : editor.getSelection(id)){
                    int[] j = new int[]{i[0]+diff[0], i[1]+diff[1], i[2]+diff[2]};
                    if(j[0]<0||j[1]<0||j[2]<0||j[0]>=editor.getMultiblock().getX()||j[1]>=editor.getMultiblock().getY()||j[2]>=editor.getMultiblock().getZ())continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    if(b==null&&editor.getMultiblock().getBlock(j[0], j[1], j[2])==null)continue;//already air, don't need to higlight air again
                    VRCore.drawCube(x+j[0]*blockSize-border, y+j[1]*blockSize-border, z+j[2]*blockSize-border, x+(j[0]+1)*blockSize+border, y+(j[1]+1)*blockSize+border, z+(j[2]+1)*blockSize+border, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void mouseReset(int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null&&leftDragEnd!=null){
            if(editor.isControlPressed(id))editor.cloneSelection(id, leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]);
            else editor.moveSelection(id, leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]);
        }
        mouseReset(button);
    }
    @Override
    public void mouseDragged(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Move tool (M)\nUse this to move or copy selections\nHold Ctrl to copy selections\nHold Ctrl+Shift to copy selection, and keep the old selection";
    }
    @Override
    public void mouseMoved(Object obj, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj){}
}