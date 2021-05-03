package planner.menu.dialog;
import java.util.Random;
import simplelibrary.error.ErrorCategory;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuWarningMessage extends MenuDialog{
    private static final String[] extraPossibilities = new String[]{"Got it", "Thanks", "Great", "Cool", "Alright", "Yep", "Awknowledged", "Aye", "Ignore", "Skip"};
    private static final Random rand = new Random();
    public MenuWarningMessage(GUI gui, Menu parent, String message, Throwable error, ErrorCategory category){
        super(gui, parent);
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
        if(!details.isEmpty()){
            details = "\n\n"+Character.toUpperCase(category.toString().charAt(0))+category.toString().substring(1)+"\n"+details;
        }
        textBox.setText("Warning: "+message+details);
        addButton(rand.nextDouble()<.01?extraPossibilities[rand.nextInt(extraPossibilities.length)]:"OK", (e) -> {
            close();
        });
    }
}