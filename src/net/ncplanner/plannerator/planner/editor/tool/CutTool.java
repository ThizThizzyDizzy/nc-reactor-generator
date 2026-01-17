package net.ncplanner.plannerator.planner.editor.tool;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class CutTool extends EditorTool{
    public CutTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos dragStart;
    private BlockPos dragEnd;
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.drawCircle(x+width*.3f, y+height*.3f, width*.075f, width*.125f);
        renderer.drawCircle(x+width*.3f, y+height*.7f, width*.075f, width*.125f);
        renderer.fillQuad(x+width*.4f, y+height*.35f, x+width*.35f, y+height*.4f, x+width*.75f, y+height*.8f, x+width*.85f, y+height*.8f);
        renderer.fillQuad(x+width*.4f, y+height*.65f, x+width*.35f, y+height*.6f, x+width*.75f, y+height*.2f, x+width*.85f, y+height*.2f);
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
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
                renderer.setColor(Core.theme.getSelectionColor(), .5f);
                renderer.fillRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(maxSY+1));
                renderer.setColor(Core.theme.getSelectionColor());
                renderer.fillRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(border+minSY));//top
                renderer.fillRect(x+blockSize*minSX, y+blockSize*(maxSY+1-border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1));//bottom
                renderer.fillRect(x+blockSize*minSX, y+blockSize*(minSY+border), x+blockSize*(border+minSX), y+blockSize*(maxSY+1-border));//left
                renderer.fillRect(x+blockSize*(maxSX+1-border), y+blockSize*(minSY+border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1-border));//right
            }
        }
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        //TODO VR: cut tool ghosts
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)dragStart = dragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        editor.clearSelection(id);
        if(button==0)dragStart = pos;
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0&&dragStart!=null){
            editor.select(id, dragStart, pos);
            editor.cutSelection(id, new BlockPos((dragStart.x+pos.x)/2, (dragStart.y+pos.y)/2, (dragStart.z+pos.z)/2));
            editor.clearSelection(id);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)dragEnd = pos;
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Cut tool\nUse this to select an area to cut\nOnce an area is selected, click to paste that selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}