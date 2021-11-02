package net.ncplanner.plannerator.planner.menu.dialog;
import java.util.HashMap;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.DebugInfoProvider;
import net.ncplanner.plannerator.planner.menu.MenuMain;
import simplelibrary.error.ErrorCategory;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuSevereError extends MenuDialog implements DebugInfoProvider{
    private final Throwable error;
    private final String message;
    private final ErrorCategory category;
    public MenuSevereError(GUI gui, Menu parent, String message, Throwable error, ErrorCategory category){
        super(gui, parent);
        this.message = message;
        this.error = error;
        this.category = category;
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
        textBox.setText("Severe "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message+"\n\n"+details);
        addButton("Main Menu", (e) -> {
            close();
            gui.open(new MenuMain(gui));
        });
        addButton("Ignore", (e) -> {
            close();
        });
        addButton("Exit", (e) -> {
            Core.autoSaveAndExit();
        });
    }
    @Override
    public HashMap<String, Object> getDebugInfo(HashMap<String, Object> debugInfo){
        debugInfo.put("message", message);
        debugInfo.put("error", error);
        debugInfo.put("category", category);
        return debugInfo;
    }
}