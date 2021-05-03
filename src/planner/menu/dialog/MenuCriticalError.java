package planner.menu.dialog;
import planner.Core;
import planner.menu.MenuMain;
import simplelibrary.error.ErrorCategory;
import simplelibrary.opengl.gui.GUI;
public class MenuCriticalError extends MenuDialog{
    public MenuCriticalError(GUI gui, String message, Throwable error, ErrorCategory category){
        super(gui, null);
        maxWidth = maxHeight = 1;
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
        textBox.setText("Critical "+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+" Error: "+message+"\n\n"+details);
        addButton("Main Menu", (e) -> {
            close();
            gui.open(new MenuMain(gui));
        });
        addButton("Exit", (e) -> {
            Core.autoSaveAndExit();
        });
    }
}