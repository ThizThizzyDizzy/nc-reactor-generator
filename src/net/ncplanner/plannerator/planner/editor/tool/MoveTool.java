package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import java.util.Iterator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.CopyAction;
import net.ncplanner.plannerator.multiblock.editor.action.MoveAction;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class MoveTool extends EditorTool{
    public MoveTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos leftDragStart;
    private BlockPos leftDragEnd;
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        float w = width/16;
        float h = height/16;
        renderer.fillRect(x+width/2-w, y+height/4, x+width/2+w, y+height*3/4);
        renderer.fillRect(x+width/4, y+height/2-h, x+width*3/4, y+height/2+h);
        renderer.fillPolygon(new float[]{x+width/4+w,x+width/2,x+width*3/4-w}, new float[]{y+height/4,y+h,y+height/4});
        renderer.fillPolygon(new float[]{x+width/4+w,x+width/2,x+width*3/4-w}, new float[]{y+height*3/4,y+height-h,y+height*3/4});
        renderer.fillPolygon(new float[]{x+width/4,x+w,x+width/4}, new float[]{y+height/4+w,y+height/2,y+height*3/4-h});
        renderer.fillPolygon(new float[]{x+width*3/4,x+width-w,x+width*3/4}, new float[]{y+height/4+h,y+height/2,y+height*3/4-h});
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(BlockPos p : editor.getSelection(id)){
                        Axis xAxis = axis.get2DXAxis();
                        Axis yAxis = axis.get2DYAxis();
                        int sx = p.x*xAxis.x+p.y*xAxis.y+p.z*xAxis.z-x1;
                        int sy = p.x*yAxis.x+p.y*yAxis.y+p.z*yAxis.z-y1;
                        int sz = p.x*axis.x+p.y*axis.y+p.z*axis.z;
                        if(sz!=layer)continue;
                        if(sx<0||sx>x2)continue;
                        if(sy<0||sy>y2)continue;
                        renderer.fillRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
                    }
                }
            }
            BlockPos diff = leftDragEnd.offset(leftDragStart, -1);
            synchronized(editor.getSelection(id)){
                for(BlockPos p : editor.getSelection(id)){
                    BlockPos op = p.offset(diff);
                    BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                    if(op.x<bbox.x1||op.x>bbox.x2)continue;
                    if(op.y<bbox.y1||op.y>bbox.y2)continue;
                    if(op.z<bbox.z1||op.z>bbox.z2)continue;
                    Axis xAxis = axis.get2DXAxis();
                    Axis yAxis = axis.get2DYAxis();
                    int sx = op.x*xAxis.x+op.y*xAxis.y+op.z*xAxis.z-x1;
                    int sy = op.x*yAxis.x+op.y*yAxis.y+op.z*yAxis.z-y1;
                    int sz = op.x*axis.x+op.y*axis.y+op.z*axis.z;
                    if(sz!=layer)continue;
                    if(sx<0||sx>x2)continue;
                    if(sy<0||sy>y2)continue;
                    AbstractBlock b = editor.getMultiblock().getBlock(p);
                    if(!editorSpace.isSpaceValid(b, op))continue;
                    if(b!=null)renderer.setWhite(.5f);
                    else renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
                    renderer.drawImage(b==null?null:b.getTexture(), x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
                }
            }
        }
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(leftDragStart!=null&&leftDragEnd!=null){
            float border = blockSize/64;
            if(!editor.isControlPressed(id)){
                synchronized(editor.getSelection(id)){
                    for(BlockPos p : editor.getSelection(id)){
                        if(editor.getMultiblock().getBlock(p)==null)continue;//already air
                        renderer.drawCube(x+p.x*blockSize-border/2, y+p.y*blockSize-border/2, z+p.z*blockSize-border/2, x+(p.x+1)*blockSize+border/2, y+(p.y+1)*blockSize+border/2, z+(p.z+1)*blockSize+border/2, null);
                    }
                }
            }
            BlockPos diff = leftDragEnd.offset(leftDragStart, -1);
            synchronized(editor.getSelection(id)){
                for(BlockPos p : editor.getSelection(id)){
                    BlockPos op = p.offset(diff);
                    BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                    if(op.x<bbox.x1||op.x>bbox.x2)continue;
                    if(op.y<bbox.y1||op.y>bbox.y2)continue;
                    if(op.z<bbox.z1||op.z>bbox.z2)continue;
                    AbstractBlock b = editor.getMultiblock().getBlock(p);
                    if(b==null&&editor.getMultiblock().getBlock(op)==null)continue;//already air, don't need to higlight air again
                    if(b!=null)renderer.setWhite(.5f);
                    else renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
                    renderer.drawCube(x+op.x*blockSize-border, y+op.y*blockSize-border, z+op.z*blockSize-border, x+(op.x+1)*blockSize+border, y+(op.y+1)*blockSize+border, z+(op.z+1)*blockSize+border, b==null?null:b.getTexture());
                }
            }
        }
        renderer.setWhite();
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)leftDragStart = leftDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)leftDragStart = pos;
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(leftDragStart!=null&&leftDragEnd!=null){
            BlockPos d = leftDragEnd.offset(leftDragStart, -1);
            if(button==0&&leftDragStart!=null&&leftDragEnd!=null){
                ArrayList<BlockPos> selection = new ArrayList<>(editor.getSelection(id));
                for(Iterator<BlockPos> it = selection.iterator(); it.hasNext();){
                    BlockPos p = it.next();
                    AbstractBlock b = editor.getMultiblock().getBlock(p);
                    if(!editorSpace.isSpaceValid(b, p.offset(d)))it.remove();
                }
                if(editor.isControlPressed(id))editor.action(new CopyAction(editor, id, selection, editor.getSelection(id), d.x, d.y, d.z), true);
                else editor.action(new MoveAction(editor, id, selection, editor.getSelection(id), d.x, d.y, d.z), true);
            }
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)leftDragEnd = pos;
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
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}