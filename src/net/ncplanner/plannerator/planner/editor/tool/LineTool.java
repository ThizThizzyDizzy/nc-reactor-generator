package net.ncplanner.plannerator.planner.editor.tool;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.image.Image;
import java.util.ArrayList;
import java.util.HashSet;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.symmetry.Symmetry;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class LineTool extends EditorTool{
    public LineTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos leftDragStart;
    private BlockPos rightDragStart;
    private BlockPos leftDragEnd;
    private BlockPos rightDragEnd;
    @Override
    public void render(float x, float y, float width, float height, int themeIndex){
        Renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        Renderer.fillQuad(x+width*.25f, y+height*.875f, x+width*.125f, y+height*.75f, x+width*.875f, y+height*.25f, x+width*.75f, y+height*.125f);
    }
    @Override
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        BoundingBox bbox = editor.getMultiblock().getBoundingBox();
        Renderer.setColor(Color.WHITE, .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart, leftDragEnd, (pos) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), pos))return;
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = pos.x*xAxis.x+pos.y*xAxis.y+pos.z*xAxis.z-x1;
            int sy = pos.x*yAxis.x+pos.y*yAxis.y+pos.z*yAxis.z-y1;
            int sz = pos.x*axis.x+pos.y*axis.y+pos.z*axis.z;
            if(sz!=layer)return;
            if(sx<0||sx>x2)return;
            if(sy<0||sy>y2)return;
            Renderer.fillRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, ResourceManager.getTexture(texture));
        }, editor.getSymmetry(), bbox.getWidth(), bbox.getHeight(), bbox.getDepth());
        Renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart, rightDragEnd, (pos) -> {
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = pos.x*xAxis.x+pos.y*xAxis.y+pos.z*xAxis.z-x1;
            int sy = pos.x*yAxis.x+pos.y*yAxis.y+pos.z*yAxis.z-y1;
            int sz = pos.x*axis.x+pos.y*axis.y+pos.z*axis.z;
            if(sz!=layer)return;
            if(sx<0||sx>x2)return;
            if(sy<0||sy>y2)return;
            Renderer.fillRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
        }, editor.getSymmetry(), bbox.getWidth(), bbox.getHeight(), bbox.getDepth());
        Renderer.setColor(Color.WHITE);
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        BoundingBox bbox = editor.getMultiblock().getBoundingBox();
        Renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        float border = blockSize/64;
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart, leftDragEnd, (pos) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), pos))return;
            PlanneratorRenderer.drawCube(x+pos.x*blockSize-border, y+pos.y*blockSize-border, z+pos.z*blockSize-border, x+(pos.x+1)*blockSize+border, y+(pos.y+1)*blockSize+border, z+(pos.z+1)*blockSize+border, texture);
        }, editor.getSymmetry(), bbox.getWidth(), bbox.getHeight(), bbox.getDepth());
        Renderer.setColor(Color.WHITE, .5f);
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart, rightDragEnd, (pos) -> {
            if(editor.getMultiblock().getBlock(pos)==null)return;
            PlanneratorRenderer.drawCube(x+pos.x*blockSize-border, y+pos.y*blockSize-border, z+pos.z*blockSize-border, x+(pos.x+1)*blockSize+border, y+(pos.y+1)*blockSize+border, z+(pos.z+1)*blockSize+border, null);
        }, editor.getSymmetry(), bbox.getWidth(), bbox.getHeight(), bbox.getDepth());
        Renderer.setColor(Color.WHITE);
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0)leftDragStart = leftDragEnd = null;
        if(button==1)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)leftDragStart = leftDragEnd = pos;
        if(button==1)rightDragStart = rightDragEnd = pos;
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            raytrace(leftDragStart, pos, (p) -> {
                if(editorSpace.isSpaceValid(set.block, p))set.add(p);
            });
            set.symmetrize(editor.getMultiblock(), editor.getSymmetry());
            if(!set.isEmpty())editor.setblocks(id, set);
        }
        if(button==1&&rightDragStart!=null){
            SetblocksAction set = new SetblocksAction(null);
            raytrace(rightDragStart, pos, set::add);
            set.symmetrize(editor.getMultiblock(), editor.getSymmetry());
            if(!set.isEmpty())editor.setblocks(id, set);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0)leftDragEnd = pos;
        if(button==1)rightDragEnd = pos;
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public String getTooltip(){
        return "Line tool (L)\nUse this tool to draw blocks in a line through the multiblock\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
    private Iterable<BlockPos> symmetrize(ArrayList<BlockPos> leftSelectedBlocks, Symmetry symmetry){
        HashSet<BlockPos> set = new HashSet<>();
        BoundingBox bbox = editor.getMultiblock().getBoundingBox();
        leftSelectedBlocks.forEach((t) -> {
            symmetry.apply(t, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), set::add);
        });
        return set;
    }
}