package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import net.ncplanner.plannerator.Renderer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.Editor;
import simplelibrary.image.Image;
public class PencilTool extends EditorTool{
    public PencilTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] leftDragStart;
    private Object leftStart = null;
    private int[] rightDragStart;
    private Object rightStart = null;
    private ArrayList<int[]> leftSelectedBlocks = new ArrayList<>();
    private ArrayList<int[]> rightSelectedBlocks = new ArrayList<>();
    @Override
    public void render(Renderer renderer, double x, double y, double width, double height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.fillPolygon(new double[]{x+width*.25, x+width*.375, x+width*.25}, new double[]{y+height*.75, y+height*.75, y+height*.625});
        renderer.fillPolygon(new double[]{x+width*.4, x+width*.275, x+width*.5, x+width*.625}, new double[]{y+height*.725, y+height*.6, y+height*.375, y+height*.5});
        renderer.fillPolygon(new double[]{x+width*.525, x+width*.65, x+width*.75, x+width*.625}, new double[]{y+height*.35, y+height*.475, y+height*.375, y+height*.25});
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==0&&leftDragStart==null)return;
        if(button==1&&rightDragStart==null)return;
        mouseReleased(null, editorSpace, 0, 0, 0, button);//allow you to release outside the editor grid and still place blocks
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock(id);
        if(button==0){
            synchronized(leftSelectedBlocks){
                if(editorSpace.isSpaceValid(selected, x, y, z))leftSelectedBlocks.add(new int[]{x,y,z});
                leftDragStart = new int[]{x,y,z};
                leftStart = obj;
            }
        }
        if(button==1){
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.add(new int[]{x,y,z});
                rightDragStart = new int[]{x,y,z};
                rightStart = obj;
            }
        }
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            synchronized(leftSelectedBlocks){
                for(int[] i : leftSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(id, set);
        }
        if(button==1){
            SetblocksAction set = new SetblocksAction(null);
            synchronized(rightSelectedBlocks){
                for(int[] i : rightSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(id, set);
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
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0){
            if(obj!=leftStart){
                leftDragStart = new int[]{x,y,z};
                leftStart = obj;
            }
            if(leftDragStart!=null){
                if(leftDragStart[0]==x&&leftDragStart[1]==y&&leftDragStart[2]==z)return;
                raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                    if(X==leftDragStart[0]&&Y==leftDragStart[1]&&Z==leftDragStart[2])return;
                    if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), X, Y, Z))return;
                    synchronized(leftSelectedBlocks){
                        for(int[] i : leftSelectedBlocks){
                            if(i[0]==X&&i[1]==Y&&i[2]==Z)return;
                        }
                        leftSelectedBlocks.add(new int[]{X,Y,Z});
                    }
                });
                leftDragStart = new int[]{x,y,z};
            }
        }
        if(button==1){
            if(obj!=rightStart){
                rightDragStart = new int[]{x,y,z};
                rightStart = obj;
            }
            if(rightDragStart!=null){
                if(rightDragStart[0]==x&&rightDragStart[1]==y&&rightDragStart[2]==z)return;
                raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
                    if(X==rightDragStart[0]&&Y==rightDragStart[1]&&Z==rightDragStart[2])return;
                    synchronized(rightSelectedBlocks){
                        for(int[] i : rightSelectedBlocks){
                            if(i[0]==X&&i[1]==Y&&i[2]==Z)return;
                        }
                        rightSelectedBlocks.add(new int[]{X,Y,Z});
                    }
                }, false);
                rightDragStart = new int[]{x,y,z};
            }
        }
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, double x, double y, double width, double height, int blockSize, Image texture){
        renderer.setWhite(.5f);
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
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
                renderer.drawImage(texture, x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
            }
        }
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
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
                renderer.fillRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
            }
        }
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        renderer.setWhite(.5f);
        double border = blockSize/64;
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
                renderer.drawCube(x+i[0]*blockSize-border, y+i[1]*blockSize-border, z+i[2]*blockSize-border, x+(i[0]+1)*blockSize+border, y+(i[1]+1)*blockSize+border, z+(i[2]+1)*blockSize+border, texture);
            }
        }
        renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(editor.getMultiblock().getBlock(i[0], i[1], i[2])==null)continue;
                renderer.drawCube(x+i[0]*blockSize-border, y+i[1]*blockSize-border, z+i[2]*blockSize-border, x+(i[0]+1)*blockSize+border, y+(i[1]+1)*blockSize+border, z+(i[2]+1)*blockSize+border, 0);
            }
        }
        renderer.setWhite();
    }
    @Override
    public String getTooltip(){
        return "Pencil tool (P)\nUse this tool to draw blocks one at a time\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){
        leftStart = rightStart = null;
    }
}