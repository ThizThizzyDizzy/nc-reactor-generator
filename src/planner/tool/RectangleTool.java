package planner.tool;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.action.SetblocksAction;
import simplelibrary.opengl.Renderer2D;
public class RectangleTool extends EditorTool{
    public RectangleTool(MenuEdit editor){
        super(editor);
    }
    private int[] leftDragStart;
    private int[] rightDragStart;
    private int[] leftDragEnd;
    private int[] rightDragEnd;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        int n = 3;
        double border = width/24;
        x+=border;
        y+=border;
        width-=border*2;
        height-=border*2;
        double w = width/n;
        double h = height/n;
        for(int X = 0; X<n; X++){
            for(int Y = 0; Y<n; Y++){
                Renderer2D.drawRect(x+X*w+border, y+Y*h+border, x+(X+1)*w-border, y+(Y+1)*h-border, 0);
            }
        }
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void mouseReset(int button){
        if(button==0)leftDragStart = leftDragEnd = null;
        if(button==1)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(int x, int y, int z, int button){
        if(button==0)leftDragStart = new int[]{x,y,z};
        if(button==1)rightDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(int x, int y, int z, int button){
        if(button==0&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock());
            foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
            });
            editor.setblocks(set);
        }
        if(button==1&&rightDragStart!=null){
            SetblocksAction set = new SetblocksAction(null);
            foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
            });
            editor.setblocks(set);
        }
        mouseReset(button);
    }
    @Override
    public void mouseDragged(int x, int y, int z, int button){
        if(button==0)leftDragEnd = new int[]{x,y,z};
        if(button==1)rightDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
}