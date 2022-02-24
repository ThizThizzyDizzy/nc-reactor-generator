package net.ncplanner.plannerator.planner.gui.menu.dialog;
import java.util.HashMap;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.DebugInfoProvider;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.MenuMain;
public class MenuError extends MenuDialog implements DebugInfoProvider{
    private final Throwable error;
    private final String message;
    public MenuError(GUI gui, Menu parent, String message, Throwable error){
        super(gui, parent);
        this.message = message;
        this.error = error;
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
        textBox.setText("Error: "+message+"\n\n"+details);
        addButton("Main Menu", () -> {
            close();
            gui.open(new MenuMain(gui));
        });
        addButton("Ignore", () -> {
            close();
        });
        addButton("Exit", () -> {
            Core.autoSaveAndExit();
        });
    }
    @Override
    public HashMap<String, Object> getDebugInfo(HashMap<String, Object> debugInfo){
        debugInfo.put("message", message);
        debugInfo.put("error", error);
        return debugInfo;
    }
}