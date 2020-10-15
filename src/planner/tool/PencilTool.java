package planner.tool;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import multiblock.action.SetblocksAction;
import org.lwjgl.glfw.GLFW;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class PencilTool extends EditorTool{
    public PencilTool(MenuEdit editor){
        super(editor);
    }
    private int[] leftDragStart;
    private MenuComponent leftLayerStart = null;
    private int[] rightDragStart;
    private MenuComponent rightLayerStart = null;
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
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            leftDragStart = null;
            leftLayerStart = null;
            synchronized(leftSelectedBlocks){
                leftSelectedBlocks.clear();
            }
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            rightDragStart = null;
            rightLayerStart = null;
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.clear();
            }
        }
    }
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock();
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            synchronized(leftSelectedBlocks){
                leftSelectedBlocks.add(new int[]{x,y,z});
                leftDragStart = new int[]{x,y,z};
                leftLayerStart = layer;
            }
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            synchronized(rightSelectedBlocks){
                rightSelectedBlocks.add(new int[]{x,y,z});
                rightDragStart = new int[]{x,y,z};
                rightLayerStart = layer;
            }
        }
    }
    @Override
    public void mouseReleased(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock());
            synchronized(leftSelectedBlocks){
                for(int[] i : leftSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(set);
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            SetblocksAction set = new SetblocksAction(null);
            synchronized(rightSelectedBlocks){
                for(int[] i : rightSelectedBlocks){
                    set.add(i[0], i[1], i[2]);
                }
            }
            editor.setblocks(set);
        }
        mouseReset(button);
    }
    @Override
    public void mouseDragged(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            if(layer!=leftLayerStart){
                leftDragStart = new int[]{x,y,z};
                leftLayerStart = layer;
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
            if(layer!=rightLayerStart){
                rightDragStart = new int[]{x,y,z};
                rightLayerStart = layer;
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
                if(i[0]==0&&i[1]==0)Renderer2D.drawRect(x+(i[2]-1)*blockSize, y, x+i[2]*blockSize, y+blockSize, texture);
            }
        }
        synchronized(rightSelectedBlocks){
            for(int[] i : rightSelectedBlocks){
                if(i[0]==0&&i[1]==0)Renderer2D.drawRect(x+(i[2]-1)*blockSize, y, x+i[2]*blockSize, y+blockSize, 0);
            }
        }
        Core.applyWhite();
    }
    @Override
    public String getTooltip(){
        return "Pencil tool (P)\nUse this tool to draw blocks one at a time\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(MenuComponent layer, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(MenuComponent layer){}
}