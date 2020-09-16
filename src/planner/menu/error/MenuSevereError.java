package planner.menu.error;
import planner.Core;
import planner.menu.Menu;
import planner.menu.MenuMain;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistScrollable;
import planner.menu.component.MenuComponentTextDisplay;
import simplelibrary.error.ErrorCategory;
import simplelibrary.opengl.gui.GUI;
public class MenuSevereError extends Menu{
    public MenuComponentMinimalistButton mainMenu = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Main Menu", true, true));
    public MenuComponentMinimalistButton ignore = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Ignore", true, true));
    public MenuComponentMinimalistButton exit = add(new MenuComponentMinimalistButton(0, 0, 0, 64, "Exit", true, true));
    public MenuComponentMinimalistScrollable scroller = add(new MenuComponentMinimalistScrollable(0, 0, 0, 0, 24, 24));
    public MenuSevereError(GUI gui, simplelibrary.opengl.gui.Menu menu, String message, Throwable error, ErrorCategory category){
        super(gui, menu);
        String details = "";
        Throwable t = error;
        while(t!=null){
            details+=t.getClass().getName()+" "+t.getMessage();
            StackTraceElement[] stackTrace = t.getStackTrace();
            for(StackTraceElement e : stackTrace){
                if(e.getClassName().startsWith("net.")||e.getClassName().startsWith("com."))continue;
                String[] splitClassName = e.getClassName().split("\\Q.");
                String filename = splitClassName[splitClassName.length-1]+".java";
                String nextLine = "\nat "+e.getClassName()+"."+e.getMethodName()+"("+filename+":"+e.getLineNumber()+")";
                if((details+nextLine).length()+4>1024){
                    details+="\n...";
                    break;
                }else details+=nextLine;
            }
            t = t.getCause();
            if(t!=null)details+="\nCaused by ";
        }
        scroller.add(new MenuComponentTextDisplay("Severe "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message+"\n\n"+details));
        mainMenu.addActionListener((e) -> {
            gui.open(new MenuMain(gui));
        });
        ignore.addActionListener((e) -> {
            gui.open(parent);
        });
        exit.addActionListener((e) -> {
            Core.helper.running = false;
        });
    }
    @Override
    public void renderBackground(){
        mainMenu.width = ignore.width = Core.helper.displayWidth()/3;
        ignore.x = mainMenu.x+mainMenu.width;
        exit.x = ignore.x+ignore.width;
        exit.width = Core.helper.displayWidth()-exit.x;
        scroller.width = Core.helper.displayWidth();
        scroller.height = mainMenu.y = ignore.y = exit.y = Core.helper.displayHeight()-mainMenu.height;
        super.renderBackground();
    }
}