package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import java.util.HashSet;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.multiblock.symmetry.Symmetry;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
public class PencilTool extends EditorTool{
    public PencilTool(Editor editor, int id){
        super(editor, id);
    }
    private BlockPos leftDragStart;
    private Object leftStart = null;
    private BlockPos rightDragStart;
    private Object rightStart = null;
    private ArrayList<BlockPos> leftSelectedBlocks = new ArrayList<>();
    private ArrayList<BlockPos> rightSelectedBlocks = new ArrayList<>();
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.drawElement("pencil", x, y, width, height);
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0&&leftDragStart==null)return;
        if(button==1&&rightDragStart==null)return;
        mouseReleased(null, editorSpace, null, button);//allow you to release outside the editor grid and still place blocks
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        AbstractBlock selected = editor.getSelectedBlock(id);
        if(button==0){
            synchronized(leftSelectedBlocks){
                if(editorSpace.isSpaceValid(selected, pos))leftSelectedBlocks.add(pos);
                leftDragStart = pos;
                leftStart = obj;
            }
        }
        if(button==1){
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.add(pos);
                rightDragStart = pos;
                rightStart = obj;
            }
        }
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            synchronized(leftSelectedBlocks){
                for(BlockPos p : leftSelectedBlocks){
                    set.add(p);
                }
            }
            set.symmetrize(editor.getMultiblock(), editor.getSymmetry());
            if(!set.isEmpty())editor.setblocks(id, set);
        }
        if(button==1){
            SetblocksAction set = new SetblocksAction(null);
            synchronized(rightSelectedBlocks){
                for(BlockPos p : rightSelectedBlocks){
                    set.add(p);
                }
            }
            set.symmetrize(editor.getMultiblock(), editor.getSymmetry());
            if(!set.isEmpty())editor.setblocks(id, set);
        }
        if(button==0){
            leftDragStart = null;
            leftStart = null;
            synchronized(leftSelectedBlocks){
                leftSelectedBlocks.clear();
            }
        }
        if(button==1){
            rightDragStart = null;
            rightStart = null;
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.clear();
            }
        }
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0){
            if(obj!=leftStart){
                leftDragStart = pos;
                leftStart = obj;
            }
            if(leftDragStart!=null){
                if(leftDragStart.equals(pos))return;
                raytrace(leftDragStart, pos, (P) -> {
                    if(P.equals(leftDragStart))return;
                    if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), P))return;
                    synchronized(leftSelectedBlocks){
                        for(BlockPos p : leftSelectedBlocks){
                            if(p.equals(P))return;
                        }
                        leftSelectedBlocks.add(P);
                    }
                });
                leftDragStart = pos;
            }
        }
        if(button==1){
            if(obj!=rightStart){
                rightDragStart = pos;
                rightStart = obj;
            }
            if(rightDragStart!=null){
                if(rightDragStart.equals(pos))return;
                raytrace(rightDragStart, pos, (P) -> {
                    if(P.equals(rightDragStart))return;
                    synchronized(rightSelectedBlocks){
                        for(BlockPos p : rightSelectedBlocks){
                            if(p.equals(P))return;
                        }
                        rightSelectedBlocks.add(P);
                    }
                }, false);
                rightDragStart = pos;
            }
        }
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        renderer.setWhite(.5f);
        synchronized(leftSelectedBlocks){
            for(BlockPos p : symmetrize(leftSelectedBlocks, editor.getSymmetry())){
                Axis xAxis = axis.get2DXAxis();
                Axis yAxis = axis.get2DYAxis();
                int sx = p.x*xAxis.x+p.y*xAxis.y+p.z*xAxis.z-x1;
                int sy = p.x*yAxis.x+p.y*yAxis.y+p.z*yAxis.z-y1;
                int sz = p.x*axis.x+p.y*axis.y+p.z*axis.z;
                if(sz!=layer)continue;
                if(sx<0||sx>x2)continue;
                if(sy<0||sy>y2)continue;
                renderer.drawImage(texture, x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
            }
        }
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        synchronized(rightSelectedBlocks){
            for(BlockPos p : symmetrize(rightSelectedBlocks, editor.getSymmetry())){
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
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        renderer.setWhite(.5f);
        float border = blockSize/64;
        synchronized(leftSelectedBlocks){
            for(BlockPos p : symmetrize(leftSelectedBlocks, editor.getSymmetry())){
                renderer.drawCube(x+p.x*blockSize-border, y+p.y*blockSize-border, z+p.z*blockSize-border, x+(p.x+1)*blockSize+border, y+(p.y+1)*blockSize+border, z+(p.z+1)*blockSize+border, texture);
            }
        }
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        synchronized(rightSelectedBlocks){
            for(BlockPos p : symmetrize(rightSelectedBlocks, editor.getSymmetry())){
                if(editor.getMultiblock().getBlock(p)==null)continue;
                renderer.drawCube(x+p.x*blockSize-border, y+p.y*blockSize-border, z+p.z*blockSize-border, x+(p.x+1)*blockSize+border, y+(p.y+1)*blockSize+border, z+(p.z+1)*blockSize+border, null);
            }
        }
        renderer.setWhite();
    }
    @Override
    public String getTooltip(){
        return "Pencil tool (P)\nUse this tool to draw blocks one at a time\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){
        leftStart = rightStart = null;
    }
    private Iterable<BlockPos> symmetrize(ArrayList<BlockPos> blocks, Symmetry symmetry){
        HashSet<BlockPos> set = new HashSet<>();
        BoundingBox bbox = editor.getMultiblock().getBoundingBox();
        blocks.forEach((t) -> {
            symmetry.apply(t, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), (pos) -> {
                set.add(pos);
            });
        });
        return set;
    }
}