package planner.editor.tool;
import java.util.ArrayList;
import java.util.Iterator;
import multiblock.Axis;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.EditorSpace;
import multiblock.action.CopyAction;
import multiblock.action.MoveAction;
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
    public void render(double x, double y, double width, double height, int themeIndex){
        Core.applyColor(Core.theme.getEditorToolTextColor(themeIndex));
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
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(int[] i : editor.getSelection(id)){
                        int bx = i[0];
                        int by = i[1];
                        int bz = i[2];
                        Axis xAxis = axis.get2DXAxis();
                        Axis yAxis = axis.get2DYAxis();
                        int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
                        int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
                        int sz = bx*axis.x+by*axis.y+bz*axis.z;
                        if(sz!=layer)continue;
                        if(sx<x1||sx>x2)continue;
                        if(sy<y1||sy>y2)continue;
                        Renderer2D.drawRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, 0);
                    }
                }
            }
            int[] diff = new int[]{leftDragEnd[0]-leftDragStart[0], leftDragEnd[1]-leftDragStart[1], leftDragEnd[2]-leftDragStart[2]};
            synchronized(editor.getSelection(id)){
                for(int[] i : editor.getSelection(id)){
                    int bx = i[0]+diff[0];
                    int by = i[1]+diff[1];
                    int bz = i[2]+diff[2];
                    BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                    if(bx<bbox.x1||bx>bbox.x2)continue;
                    if(by<bbox.y1||by>bbox.y2)continue;
                    if(bz<bbox.z1||bz>bbox.z2)continue;
                    Axis xAxis = axis.get2DXAxis();
                    Axis yAxis = axis.get2DYAxis();
                    int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
                    int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
                    int sz = bx*axis.x+by*axis.y+bz*axis.z;
                    if(sz!=layer)continue;
                    if(sx<x1||sx>x2)continue;
                    if(sy<y1||sy>y2)continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    if(!editorSpace.isSpaceValid(b, bx, by, bz))continue;
                    if(b!=null)Core.applyWhite(.5f);
                    else Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
                    Renderer2D.drawRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
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
                    int bx = i[0]+diff[0];
                    int by = i[1]+diff[1];
                    int bz = i[2]+diff[2];
                    BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                    if(bx<bbox.x1||bx>bbox.x2)continue;
                    if(by<bbox.y1||by>bbox.y2)continue;
                    if(bz<bbox.z1||bz>bbox.z2)continue;
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    if(b==null&&editor.getMultiblock().getBlock(bx, by, bz)==null)continue;//already air, don't need to higlight air again
                    if(b!=null)Core.applyWhite(.5f);
                    else Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
                    VRCore.drawCube(x+bx*blockSize-border, y+by*blockSize-border, z+bz*blockSize-border, x+(bx+1)*blockSize+border, y+(by+1)*blockSize+border, z+(bz+1)*blockSize+border, b==null?0:Core.getTexture(b.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(leftDragStart!=null&&leftDragEnd!=null){
            int dx = leftDragEnd[0]-leftDragStart[0], dy = leftDragEnd[1]-leftDragStart[1], dz = leftDragEnd[2]-leftDragStart[2];
            if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null&&leftDragEnd!=null){
                ArrayList<int[]> selection = new ArrayList<>(editor.getSelection(id));
                for(Iterator<int[]> it = selection.iterator(); it.hasNext();){
                    int[] i = it.next();
                    Block b = editor.getMultiblock().getBlock(i[0], i[1], i[2]);
                    int bx = i[0]+dx;
                    int by = i[1]+dy;
                    int bz = i[2]+dz;
                    if(!editorSpace.isSpaceValid(b, bx, by, bz))it.remove();
                }
                if(editor.isControlPressed(id))editor.action(new CopyAction(editor, id, selection, editor.getSelection(id), dx, dy, dz), true);
                else editor.action(new MoveAction(editor, id, selection, editor.getSelection(id), dx, dy, dz), true);
            }
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
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
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}