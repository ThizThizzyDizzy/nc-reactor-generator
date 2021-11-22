package net.ncplanner.plannerator.planner.editor.tool;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class RectangleTool extends EditorTool{
    public RectangleTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] leftDragStart;
    private int[] rightDragStart;
    private int[] leftDragEnd;
    private int[] rightDragEnd;
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        int n = 3;
        float border = width/24;
        x+=border;
        y+=border;
        width-=border*2;
        height-=border*2;
        float w = width/n;
        float h = height/n;
        for(int X = 0; X<n; X++){
            for(int Y = 0; Y<n; Y++){
                renderer.fillRect(x+X*w+border, y+Y*h+border, x+(X+1)*w-border, y+(Y+1)*h-border);
            }
        }
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        renderer.setWhite(.5f);
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (bx,by,bz) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), bx, by, bz))return;
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
            int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
            int sz = bx*axis.x+by*axis.y+bz*axis.z;
            if(sz!=layer)return;
            if(sx<0||sx>x2-x1)return;
            if(sy<0||sy>y2-y1)return;
            renderer.drawImage(texture, x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
        });
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (bx,by,bz) -> {
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
            int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
            int sz = bx*axis.x+by*axis.y+bz*axis.z;
            if(sz!=layer)return;
            if(sx<0||sx>x2-x1)return;
            if(sy<0||sy>y2-y1)return;
            renderer.fillRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
        });
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        renderer.setWhite(.5f);
        float border = blockSize/64;
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), X, Y, Z))return;
            renderer.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, texture);
        });
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(editor.getMultiblock().getBlock(X, Y, Z)==null)return;
            renderer.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, null);
        });
        renderer.setWhite();
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)leftDragStart = leftDragEnd = null;
        if(button==1)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0)leftDragStart = new int[]{x,y,z};
        if(button==1)rightDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                if(editorSpace.isSpaceValid(set.block, X, Y, Z))set.add(X, Y, Z);
            });
            editor.setblocks(id, set);
        }
        if(button==1&&rightDragStart!=null){
            SetblocksAction set = new SetblocksAction(null);
            foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
            });
            editor.setblocks(id, set);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0)leftDragEnd = new int[]{x,y,z};
        if(button==1)rightDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public String getTooltip(){
        return "Box tool (B)\nUse this tool to draw Rectangles or Cuboids of the same block\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}