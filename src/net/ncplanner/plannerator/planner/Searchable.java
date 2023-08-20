package net.ncplanner.plannerator.planner;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
public interface Searchable{
    public ArrayList<String> getSimpleSearchableNames();//[\s\A](-([\w\d]+)|-\"(.+?)\")[\Z\s]
    public ArrayList<String> getSearchableNames();//[\s\A](-([\w\d]+)|-\"(.+?)\")[\Z\s]
    public static final String excludePattern = "(\\s|^)(-([\\w\\d]+)|-\\\"(.+?)\\\")($|\\s)";
    public static boolean isValidForSearch(Searchable searchable, String searchText){
        String regex = ".*";
        if(searchText.startsWith("regex:")){
            regex = searchText.substring(6);
        }else{
            while(true){
                Matcher matcher = Pattern.compile(excludePattern).matcher(searchText);
                if(matcher.find()){
                    String exclude = matcher.group(4);
                    if(exclude==null)exclude = matcher.group(3);
                    String excludeRegex = ".*";
                    for(char c : exclude.toCharArray()){
                        if(Character.isLetterOrDigit(c)){
                            excludeRegex+="["+Character.toLowerCase(c)+""+Character.toUpperCase(c)+"]";
                        }else excludeRegex+="\\"+c;
                    }
                    excludeRegex+=".*";
                    for(String nam : searchable.getSearchableNames()){
                        if(nam!=null&&nam.matches(excludeRegex))return false;//excluded
                    }
                    searchText = searchText.replaceFirst(excludePattern, " ");
                }else break;
            }
            searchText = searchText.trim();
            //old stuff
            for(char c : searchText.toCharArray()){
                if(Character.isLetterOrDigit(c)){
                    regex+="["+Character.toLowerCase(c)+""+Character.toUpperCase(c)+"].*";
                }else regex+="\\"+c+".*";
            }
        }
        for(String nam : searchable.getSearchableNames()){
            try{
                if(nam!=null&&nam.matches(regex))return true;
            }catch(PatternSyntaxException ex){return false;}
        }
        return false;
    }
    public static boolean isValidForSimpleSearch(Searchable searchable, String searchText){
        String regex = ".*";
        if(searchText.startsWith("regex:")){
            regex = searchText.substring(6);
        }else{
            while(true){
                Matcher matcher = Pattern.compile(excludePattern).matcher(searchText);
                if(matcher.find()){
                    String exclude = matcher.group(4);
                    if(exclude==null)exclude = matcher.group(3);
                    String excludeRegex = ".*";
                    for(char c : exclude.toCharArray()){
                        if(Character.isLetterOrDigit(c)){
                            excludeRegex+="["+Character.toLowerCase(c)+""+Character.toUpperCase(c)+"]";
                        }else excludeRegex+="\\"+c;
                    }
                    excludeRegex+=".*";
                    for(String nam : searchable.getSimpleSearchableNames()){
                        if(nam.matches(excludeRegex))return false;//excluded
                    }
                    searchText = searchText.replaceFirst(excludePattern, " ");
                }else break;
            }
            searchText = searchText.trim();
            //old stuff
            for(char c : searchText.toCharArray()){
                if(Character.isLetterOrDigit(c)){
                    regex+="["+Character.toLowerCase(c)+""+Character.toUpperCase(c)+"].*";
                }else regex+="\\"+c+".*";
            }
        }
        for(String nam : searchable.getSimpleSearchableNames()){
            try{
                if(nam.matches(regex))return true;
            }catch(PatternSyntaxException ex){return false;}
        }
        return false;
    }
}