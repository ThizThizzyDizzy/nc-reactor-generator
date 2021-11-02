package net.ncplanner.plannerator.planner;
import java.util.HashMap;
import java.util.Objects;
public interface DebugInfoProvider{
    public HashMap<String, Object> getDebugInfo(HashMap<String, Object> debugInfo);
    public static String asString(int indentation, HashMap<String, Object> debugInfo){
        String str = "";
        String indent = "";
        for(int i = 0; i<indentation; i++){
            indent+=i==indentation-1?"- ":"  ";
        }
        for(String key : debugInfo.keySet()){
            Object debug = debugInfo.get(key);
            String s = Objects.toString(debug);
            if(debug instanceof DebugInfoProvider){
                s = "\n"+asString(indentation+1, ((DebugInfoProvider)debug).getDebugInfo(debugInfo));
            }
            if(debug instanceof Throwable){
                Throwable t = (Throwable)debug;
                s = "";
                boolean first = true;
                do{
                    s+=(first?"":indent.replace("-"," ")+"Caused by ")+t.getClass().getName()+": "+t.getMessage()+"\n";
                    first = false;
                    for(StackTraceElement e : t.getStackTrace()){
                        s+=indent.replace("-"," ")+" at "+e.toString()+"\n";
                    }
                    t = t.getCause();
                }while(t!=null);
                s = s.trim();
            }
            str+=indent+key+": "+debug.getClass().getName()+": "+s+"\n";
        }
        return str;
    }
}