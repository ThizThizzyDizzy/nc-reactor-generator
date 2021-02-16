package planner.editor.tool;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.Block;
import multiblock.action.SetblocksAction;
import org.lwjgl.glfw.GLFW;
import planner.editor.Editor;
import planner.vr.VRCore;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
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
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width*.25, y+height*.75);
        GL11.glVertex2d(x+width*.375, y+height*.75);
        GL11.glVertex2d(x+width*.25, y+height*.625);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x+width*.4, y+height*.725);
        GL11.glVertex2d(x+width*.275, y+height*.6);
        GL11.glVertex2d(x+width*.5, y+height*.375);
        GL11.glVertex2d(x+width*.625, y+height*.5);

        GL11.glVertex2d(x+width*.525, y+height*.35);
        GL11.glVertex2d(x+width*.65, y+height*.475);
        GL11.glVertex2d(x+width*.75, y+height*.375);
        GL11.glVertex2d(x+width*.625, y+height*.25);
        GL11.glEnd();
    }
    @Override
    public void mouseReset(int button){
        if(button==0&&leftDragStart==null)return;
        if(button==1&&rightDragStart==null)return;
        mouseReleased(null, 0, 0, 0, button);//allow you to release outside the editor grid and still place blocks
    }
    @Override
    public void mousePressed(Object obj, int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock(id);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            synchronized(leftSelectedBlocks){
                leftSelectedBlocks.add(new int[]{x,y,z});
                leftDragStart = new int[]{x,y,z};
                leftStart = obj;
            }
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.add(new int[]{x,y,z});
                rightDragStart = new int[]{x,y,z};
                rightStart = obj;
            }
        }
    }
    @Override
    public void mouseReleased(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            synchronized(leftSelectedBlocks){
                for(int[] i : leftSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(id, set);
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            SetblocksAction set = new SetblocksAction(null);
            synchronized(rightSelectedBlocks){
                for(int[] i : rightSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(id, set);
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            leftDragStart = null;
            leftStart = null;
            synchronized(leftSelectedBlocks){
                leftSelectedBlocks.clear();
            }
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            rightDragStart = null;
            rightStart = null;
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.clear();
            }
        }
    }
    @Override
    public void mouseDragged(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            if(obj!=leftStart){
                leftDragStart = new int[]{x,y,z};
                leftStart = obj;
            }
            if(leftDragStart!=null){
                if(leftDragStart[0]==x&&leftDragStart[1]==y&&leftDragStart[2]==z)return;
                raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                    if(X==leftDragStart[0]&&Y==leftDragStart[1]&&Z==leftDragStart[2])return;
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
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
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
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
                if(i[1]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[2]*blockSize, x+(i[0]+1)*blockSize, y+(i[2]+1)*blockSize, texture);
            }
        }
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(i[1]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[2]*blockSize, x+(i[0]+1)*blockSize, y+(i[2]+1)*blockSize, 0);
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
                if(i[2]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[1]*blockSize, x+(i[0]+1)*blockSize, y+(i[1]+1)*blockSize, texture);
            }
        }
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(i[2]==layer)Renderer2D.drawRect(x+i[0]*blockSize, y+i[1]*blockSize, x+(i[0]+1)*blockSize, y+(i[1]+1)*blockSize, 0);
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
                if(i[0]==editor.getMultiblock().getX()/2&&i[1]==0)Renderer2D.drawRect(x+(i[2]-1)*blockSize, y, x+i[2]*blockSize, y+blockSize, texture);
            }
        }
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(i[0]==editor.getMultiblock().getX()/2&&i[1]==0)Renderer2D.drawRect(x+(i[2]-1)*blockSize, y, x+i[2]*blockSize, y+blockSize, 0);
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        double border = blockSize/64;
        synchronized(leftSelectedBlocks){
            for(int[] i : leftSelectedBlocks){
                VRCore.drawCube(x+i[0]*blockSize-border, y+i[1]*blockSize-border, z+i[2]*blockSize-border, x+(i[0]+1)*blockSize+border, y+(i[1]+1)*blockSize+border, z+(i[2]+1)*blockSize+border, texture);
            }
        }
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(editor.getMultiblock().getBlock(i[0], i[1], i[2])==null)continue;
                VRCore.drawCube(x+i[0]*blockSize-border, y+i[1]*blockSize-border, z+i[2]*blockSize-border, x+(i[0]+1)*blockSize+border, y+(i[1]+1)*blockSize+border, z+(i[2]+1)*blockSize+border, 0);
            }
        }
        Core.applyWhite();
    }
    @Override
    public String getTooltip(){
        return "Pencil tool (P)\nUse this tool to draw blocks one at a time\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj){
        leftStart = rightStart = null;
    }
}