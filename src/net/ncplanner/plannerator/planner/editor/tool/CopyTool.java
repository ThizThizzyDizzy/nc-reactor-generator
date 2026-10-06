package net.ncplanner.plannerator.planner.editor.tool;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class CopyTool extends EditorTool{
    public CopyTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos dragStart;
    private BlockPos dragEnd;
    @Override
    public void render(float x, float y, float width, float height, int themeIndex){
        Renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        Renderer.fillRect(x+width*.35f, y+height*.15f, x+width*.8f, y+height*.75f);
        Renderer.setColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        Renderer.fillRect(x+width*.4f, y+height*.2f, x+width*.75f, y+height*.7f);
        Renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        Renderer.fillRect(x+width*.2f, y+height*.25f, x+width*.65f, y+height*.85f);
        Renderer.setColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        Renderer.fillRect(x+width*.25f, y+height*.3f, x+width*.6f, y+height*.8f);
    }
    @Override
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        if(dragEnd!=null&&dragStart!=null){
            float border = 1/8f;
            int minBX = Math.min(dragStart.x, dragEnd.x);
            int minBY = Math.min(dragStart.y, dragEnd.y);
            int minBZ = Math.min(dragStart.z, dragEnd.z);
            int maxBX = Math.max(dragStart.x, dragEnd.x);
            int maxBY = Math.max(dragStart.y, dragEnd.y);
            int maxBZ = Math.max(dragStart.z, dragEnd.z);
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int minSX = Math.max(0,Math.min(x2,minBX*xAxis.x+minBY*xAxis.y+minBZ*xAxis.z-x1));
            int minSY = Math.max(0,Math.min(y2,minBX*yAxis.x+minBY*yAxis.y+minBZ*yAxis.z-y1));
            int maxSX = Math.max(0,Math.min(x2,maxBX*xAxis.x+maxBY*xAxis.y+maxBZ*xAxis.z-x1));
            int maxSY = Math.max(0,Math.min(y2,maxBX*yAxis.x+maxBY*yAxis.y+maxBZ*yAxis.z-y1));
            int minSZ = minBX*axis.x+minBY*axis.y+minBZ*axis.z;
            int maxSZ = maxBX*axis.x+maxBY*axis.y+maxBZ*axis.z;
            if(layer>=minSZ&&layer<=maxSZ){
                Renderer.setColor(Core.theme.getSelectionColor(), .5f);
                Renderer.fillRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(maxSY+1));
                Renderer.setColor(Core.theme.getSelectionColor());
                Renderer.fillRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(border+minSY));//top
                Renderer.fillRect(x+blockSize*minSX, y+blockSize*(maxSY+1-border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1));//bottom
                Renderer.fillRect(x+blockSize*minSX, y+blockSize*(minSY+border), x+blockSize*(border+minSX), y+blockSize*(maxSY+1-border));//left
                Renderer.fillRect(x+blockSize*(maxSX+1-border), y+blockSize*(minSY+border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1-border));//right
            }
        }
        Renderer.setColor(Color.WHITE);
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        //TODO VR: Copy tool ghosts
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)dragStart = dragEnd = null;
    }
    @Override
    public void mousePressed(Object layer, EditorSpace editorSpace, BlockPos pos, int button){
        editor.clearSelection(id);
        if(button==0)dragStart = pos;
    }
    @Override
    public void mouseReleased(Object layer, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0&&dragStart!=null){
            editor.select(id, dragStart, pos);
            editor.copySelection(id, new BlockPos((dragStart.x+pos.x)/2, (dragStart.y+pos.y)/2, (dragStart.y+pos.z)/2));
            editor.clearSelection(id);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object layer, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)dragEnd = pos;
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Copy tool\nUse this to select an area to copy\nOnce an area is selected, click to paste that selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}