package net.ncplanner.plannerator.planner.ui;
import com.thizthizzydizzy.dizzyengine.DizzyEngine;
import com.thizthizzydizzy.dizzyengine.graphics.Renderer;
import com.thizthizzydizzy.dizzyengine.ui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuCalibrateCursor;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWDropCallback;
import static org.lwjgl.opengl.GL11.*;
/** Adapts Plannerator's existing layouts and actions to a DizzyUI menu. */
public class PlanneratorMenu extends Menu{
    public final net.ncplanner.plannerator.planner.gui.Menu content;
    public PlanneratorMenu(net.ncplanner.plannerator.planner.gui.Menu content){this.content=content;}
    @Override
    public void render(double deltaTime){
        float width=content.gui.getWidth(),height=content.gui.getHeight();
        Renderer.projection(new Matrix4f().setOrtho(0,width,height,0,0.1f,10));
        net.ncplanner.plannerator.graphics.PlanneratorRenderer.resetClipping(
            DizzyEngine.screenSize.x/Math.max(1,width),DizzyEngine.screenSize.y/Math.max(1,height));
        Renderer.setColor(net.ncplanner.plannerator.planner.Core.theme.getMenuBackgroundColor());
        Renderer.fillRect(0,0,width,height);
        Renderer.setColor(com.thizthizzydizzy.dizzyengine.graphics.image.Color.WHITE);
        content.width=width;
        content.height=height;
        Renderer.setTemporaryView(new Matrix4f().setTranslation(0,0,-5));
        Renderer.setTemporaryProjection(new Matrix4f().setPerspective(45,width/Math.max(1,height),0.1f,100));
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        try{content.render3d(deltaTime);}finally{
            Renderer.restoreView();Renderer.restoreProjection();
            glDisable(GL_DEPTH_TEST);glDisable(GL_CULL_FACE);
        }
        net.ncplanner.plannerator.planner.Core.render2d(new net.ncplanner.plannerator.graphics.PlanneratorRenderer(), deltaTime);
        content.render2d(deltaTime);
    }
    @Override
    public void onResize(Vector2f size){content.width=content.gui.getWidth();content.height=content.gui.getHeight();}
    @Override
    public void onChar(int id,int codepoint){content.onCharTyped((char)codepoint);}
    @Override
    public void onCharMods(int id,int codepoint,int mods){content.onCharTypedWithModifiers((char)codepoint,mods);}
    @Override
    public void onCursorPos(int id,double x,double y){
        x=x*MenuCalibrateCursor.xMult/MenuCalibrateCursor.xGUIScale+MenuCalibrateCursor.xOff;
        y=y*MenuCalibrateCursor.yMult/MenuCalibrateCursor.yGUIScale+MenuCalibrateCursor.yOff;
        content.gui.mouseX=x;content.gui.mouseY=y;
        content.onCursorMoved(x,y);
    }
    @Override
    public void onKey(int id,int key,int scancode,int action,int mods){content.onKeyEvent(key,scancode,action,mods);}
    @Override
    public void onMouseButton(int id,Vector2d pos,int button,int action,int mods){
        if(pos==null)return;
        onCursorPos(id,pos.x,pos.y);
        content.onMouseButton(content.gui.mouseX,content.gui.mouseY,button,action,mods);
    }
    @Override
    public boolean onScroll(int id,Vector2d pos,double x,double y){return content.onScroll(x,y);}
    @Override
    public void onDrop(int id,Vector2d pos,int count,long names){
        String[] files=new String[count];
        for(int i=0;i<count;i++)files[i]=GLFWDropCallback.getName(names,i);
        content.onFilesDropped(files);
    }
    @Override
    protected void onCursorFound(int id){content.onCursorEnteredWindow();}
    @Override
    protected void onCursorLost(int id){content.onCursorExitedWindow();}
}
