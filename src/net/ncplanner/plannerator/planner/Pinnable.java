package net.ncplanner.plannerator.planner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
public interface Pinnable extends Searchable{
    public static <T extends Object> ArrayList<T> searchAndSort(List<T> list, String searchText){
        list = new ArrayList<>(list);//copy so it's not destructive
        ArrayList<T> outputList = new ArrayList<>();
        for(Iterator<T> it = list.iterator(); it.hasNext();){//move pinned stuff to the top
            T c = it.next();
            if(c instanceof Pinnable){
                if(Pinnable.isPinned((Pinnable)c)){
                    if(Searchable.isValidForSearch((Searchable)c, searchText))outputList.add(c);
                    it.remove();
                }
            }
        }
        for(T c : list){
            if(c instanceof Searchable){
                if(Searchable.isValidForSearch((Searchable)c, searchText))outputList.add(c);
            }else outputList.add(c);
        }
        return outputList;
    }
    public static <T extends Object> ArrayList<T> sort(List<T> list){
        list = new ArrayList<>(list);//copy so it's not destructive
        ArrayList<T> outputList = new ArrayList<>();
        for(Iterator<T> it = list.iterator(); it.hasNext();){//move pinned stuff to the top
            T c = it.next();
            if(c instanceof Pinnable){
                if(Pinnable.isPinned((Pinnable)c)){
                    outputList.add(c);
                    it.remove();
                }
            }
        }
        outputList.addAll(list);
        return outputList;
    }
    public static boolean isPinned(Pinnable pinnable){
        synchronized(Core.pinnedStrs){
            return Core.pinnedStrs.contains(pinnable.getPinnedName());
        }
    }
    public static void togglePin(Pinnable p){
        synchronized(Core.pinnedStrs){
            if(isPinned(p))Core.pinnedStrs.remove(p.getPinnedName());
            else Core.pinnedStrs.add(p.getPinnedName());
        }
    }
    public String getPinnedName();
}