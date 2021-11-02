package net.ncplanner.plannerator.planner;
import java.util.Locale;
/**
 * An abstraction layer for string operations between java and other platforms.
 * Also provides extra functionality used within the planner
 * @author Thiz
 */
public class StringUtil{
    public static String toLowerCase(String str){
        return str.toLowerCase(Locale.ROOT);
    }
    public static String superRemove(String str, String... patterns){
        for(String s : patterns)str = str.replace(s, "");
        return str;
    }
    public static String superReplace(String str, String... strs){
        for(int i = 0; i<strs.length; i+=2){
            str = str.replace(strs[i], strs[i+1]);
        }
        return str;
    }
    public static String replace(String str, String str1, String str2){
        return str.replace(str1, str2);
    }
    public static String substring(StringBuilder s, int min){
        return s.substring(min);
    }
    public static String[] split(String str, String regex){
        return str.split(regex);
    }
    public static boolean matches(String str, String regex){
        return str.matches(regex);
    }
}