package net.ncplanner.plannerator.planner.editor.tool;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class SelectionTool extends EditorTool{
    public SelectionTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos leftDragStart;
    private BlockPos leftDragEnd;
    private BlockPos rightDragStart;
    private BlockPos rightDragEnd;
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.fillRect(x+width/10, y+height/10, x+width/3, y+height/6);
        renderer.fillRect(x+width/10, y+height/10, x+width/6, y+height/3);
        renderer.fillRect(x+width-width/10, y+height/10, x+width-width/3, y+height/6);
        renderer.fillRect(x+width-width/10, y+height/10, x+width-width/6, y+height/3);
        renderer.fillRect(x+width/10, y+height-height/10, x+width/3, y+height-height/6);
        renderer.fillRect(x+width/10, y+height-height/10, x+width/6, y+height-height/3);
        renderer.fillRect(x+width-width/10, y+height-height/10, x+width-width/3, y+height-height/6);
        renderer.fillRect(x+width-width/10, y+height-height/10, x+width-width/6, y+height-height/3);
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        if(leftDragEnd!=null&&leftDragStart!=null){
            float border = 1/8f;
            int minBX = Math.min(leftDragStart.x, leftDragEnd.x);
            int minBY = Math.min(leftDragStart.y, leftDragEnd.y);
            int minBZ = Math.min(leftDragStart.z, leftDragEnd.z);
            int maxBX = Math.max(leftDragStart.x, leftDragEnd.x);
            int maxBY = Math.max(leftDragStart.y, leftDragEnd.y);
            int maxBZ = Math.max(leftDragStart.z, leftDragEnd.z);
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int minSX = minBX*xAxis.x+minBY*xAxis.y+minBZ*xAxis.z-x1;
            int minSY = minBX*yAxis.x+minBY*yAxis.y+minBZ*yAxis.z-y1;
            int maxSX = maxBX*xAxis.x+maxBY*xAxis.y+maxBZ*xAxis.z-x1;
            int maxSY = maxBX*yAxis.x+maxBY*yAxis.y+maxBZ*yAxis.z-y1;
            if(maxSX>=x1&&maxSY>=y1&&minSX<=x2&&minSY<=y2){
                minSX = Math.max(0,Math.min(x2-x1,minSX));
                minSY = Math.max(0,Math.min(y2-y1,minSY));
                maxSX = Math.max(0,Math.min(x2-x1,maxSX));
                maxSY = Math.max(0,Math.min(y2-y1,maxSY));
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
        }
        if(rightDragEnd!=null&&rightDragStart!=null){
            float border = 1/8f;
            int minBX = Math.min(rightDragStart.x, rightDragEnd.x);
            int minBY = Math.min(rightDragStart.y, rightDragEnd.y);
            int minBZ = Math.min(rightDragStart.z, rightDragEnd.z);
            int maxBX = Math.max(rightDragStart.x, rightDragEnd.x);
            int maxBY = Math.max(rightDragStart.y, rightDragEnd.y);
            int maxBZ = Math.max(rightDragStart.z, rightDragEnd.z);
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int minSX = minBX*xAxis.x+minBY*xAxis.y+minBZ*xAxis.z-x1;
            int minSY = minBX*yAxis.x+minBY*yAxis.y+minBZ*yAxis.z-y1;
            int maxSX = maxBX*xAxis.x+maxBY*xAxis.y+maxBZ*xAxis.z-x1;
            int maxSY = maxBX*yAxis.x+maxBY*yAxis.y+maxBZ*yAxis.z-y1;
            if(maxSX>=x1&&maxSY>=y1&&minSX<=x2&&minSY<=y2){
                minSX = Math.max(0,Math.min(x2-x1,minSX));
                minSY = Math.max(0,Math.min(y2-y1,minSY));
                maxSX = Math.max(0,Math.min(x2-x1,maxSX));
                maxSY = Math.max(0,Math.min(y2-y1,maxSY));
                int minSZ = minBX*axis.x+minBY*axis.y+minBZ*axis.z;
                int maxSZ = maxBX*axis.x+maxBY*axis.y+maxBZ*axis.z;
                if(layer>=minSZ&&layer<=maxSZ){
                    renderer.setColor(Core.theme.getSelectionColor());
                    renderer.fillRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(border+minSY));//top
                    renderer.fillRect(x+blockSize*minSX, y+blockSize*(maxSY+1-border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1));//bottom
                    renderer.fillRect(x+blockSize*minSX, y+blockSize*(minSY+border), x+blockSize*(border+minSX), y+blockSize*(maxSY+1-border));//left
                    renderer.fillRect(x+blockSize*(maxSX+1-border), y+blockSize*(minSY+border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1-border));//right
                }
            }
        }
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float w, Image texture){
        if(leftDragEnd!=null&&leftDragStart!=null){
            float border = w/16;
            int minX = Math.min(leftDragStart.x, leftDragEnd.x);
            int minY = Math.min(leftDragStart.y, leftDragEnd.y);
            int minZ = Math.min(leftDragStart.z, leftDragEnd.z);
            int maxX = Math.max(leftDragStart.x, leftDragEnd.x);
            int maxY = Math.max(leftDragStart.y, leftDragEnd.y);
            int maxZ = Math.max(leftDragStart.z, leftDragEnd.z);
            renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id), .5f);
            renderer.drawCube(x+w*minX-border/4, y+w*minY-border/4, z+w*minZ-border/4, x+w*(maxX+1)+border/4, y+w*(maxY+1)+border/4, z+w*(maxZ+1)+border/4, null);
            renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id));
            renderer.drawCubeOutline(x+w*minX-border, y+w*minY-border, z+w*minZ-border, x+w*(maxX+1)+border, y+w*(maxY+1)+border, z+w*(maxZ+1)+border, border);
        }
        if(rightDragEnd!=null&&rightDragStart!=null){
            float border = w/16;
            int minX = Math.min(rightDragStart.x, rightDragEnd.x);
            int minY = Math.min(rightDragStart.y, rightDragEnd.y);
            int minZ = Math.min(rightDragStart.z, rightDragEnd.z);
            int maxX = Math.max(rightDragStart.x, rightDragEnd.x);
            int maxY = Math.max(rightDragStart.y, rightDragEnd.y);
            int maxZ = Math.max(rightDragStart.z, rightDragEnd.z);
            renderer.setColor(editor.convertToolColor(Core.theme.getSelectionColor(), id));
            renderer.drawCubeOutline(x+w*minX-border, y+w*minY-border, z+w*minZ-border, x+w*(maxX+1)+border, y+w*(maxY+1)+border, z+w*(maxZ+1)+border, border);
        }
        renderer.setWhite();
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)leftDragStart = leftDragEnd = null;
        if(button==1)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(!editor.isControlPressed(id)){
            editor.clearSelection(id);
        }
        if(editor.isShiftPressed(id)){
            if(button==0)editor.selectGroup(id, pos);
            if(button==1)editor.deselectGroup(id, pos);
            return;
        }
        if(editor.isAltPressed(id)){
            if(button==0)editor.selectCluster(id, pos);
            if(button==1)editor.deselectCluster(id, pos);
            return;
        }
        if(button==0)leftDragStart = pos;
        if(button==1)rightDragStart = pos;
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0&&leftDragStart!=null)editor.select(id, leftDragStart, pos);
        if(button==1&&rightDragStart!=null)editor.deselect(id, rightDragStart, pos);
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)leftDragEnd = pos;
        if(button==1)rightDragEnd = pos;
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Select tool (S)\nUse this to select areas of the reactor\nCtrl-click to select multiple selections\nShift-click to select Groups of blocks that require each other\nAlt-click to select clusters\nEdits can only be made inside of selections (unless there are no selections)\nPress Delete to delete the selected area\nPress Escape to clear selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}