package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
public abstract class DefinedNCPFObject{
    public abstract void convertFromObject(NCPFObject ncpf);
    public abstract void convertToObject(NCPFObject ncpf);
    public <T extends NCPFElement> void conglomerateElementList(List<T> elements, List<T> addonElements){
        for(T addonElem : addonElements){
            T match = null;
            for(T elem : elements){
                if(elem.definition.matches(addonElem.definition)){
                    match = elem;
                    break;
                }
            }
            if(match==null)elements.add(addonElem);
            else match.conglomerate(addonElem);
        }
    }
    public <T extends DefinedNCPFObject> T copyTo(Supplier<T> copy){
        NCPFObject obj = new NCPFObject();
        convertToObject(obj);
        T t = copy.get();
        t.convertFromObject(obj);
        return t;
    }
    public <T extends DefinedNCPFObject> List<T> copyRecipes(NCPFBlockRecipesModule from, Supplier<T> newCopy){
        return copyList(from.recipes, newCopy);
    }
    public <T extends DefinedNCPFObject> List<NCPFElement> copyRecipes(List<T> from, NCPFBlockRecipesModule to){
        return copyList(from, to.recipes, NCPFElement::new);
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> List<T> copyList(List<V> from, Supplier<T> newCopy){
        return copyList(from, new ArrayList<>(), newCopy);
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> List<T> copyList(List<V> from, List<T> to, Supplier<T> newCopy){
        for(V v : from)to.add(v.copyTo(newCopy));
        return to;
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> T[][][] copy3DArray(V[][][] from, Supplier<T> newCopy){
        T[][][] to = (T[][][])new DefinedNCPFObject[from.length][from[0].length][from[0][0].length];
        copy3DArray(from, to, newCopy);
        return to;
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> void copy3DArray(V[][][] from, T[][][] to, Supplier<T> newCopy){
        for(int x = 0; x<from.length; x++){
            for(int y = 0; y<from[x].length; y++){
                for(int z = 0; z<from[x][y].length; z++){
                    to[x][y][z] = copy(from[x][y][z], newCopy);
                }
            }
        }
    }
    public <T extends NCPFElement, V extends NCPFElement> void match3DArray(V[][][] from, T[][][] to, List<T> list){
        for(int x = 0; x<from.length; x++){
            for(int y = 0; y<from[x].length; y++){
                for(int z = 0; z<from[x][y].length; z++){
                    to[x][y][z] = matchElement(from[x][y][z], list);
                }
            }
        }
    }
    public <T extends DefinedNCPFObject> T copy(DefinedNCPFObject from, Supplier<T> copy){
        return from==null?null:from.copyTo(copy);
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject, U extends DefinedNCPFModularObject> T[][][] copy3DArrayConditional(V[][][] from, T[][][] to, U[][][] conditions, Supplier<T> newCopy, Function<U, Boolean> condition){
        for(int x = 0; x<from.length; x++){
            for(int y = 0; y<from[x].length; y++){
                for(int z = 0; z<from[x][y].length; z++){
                    if(conditions[x][y][z]!=null&&condition.apply(conditions[x][y][z]))to[x][y][z] = copy(from[x][y][z], newCopy);
                }
            }
        }
        return to;
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject, U extends DefinedNCPFModularObject> T[][][] copy3DArrayConditional(V[][][] from, U[][][] conditions, Supplier<T> newCopy, Function<U, Boolean> condition){
        return copy3DArrayConditional(from, (T[][][])new DefinedNCPFObject[from.length][from[0].length][from[0][0].length], conditions, newCopy, condition);
    }
    public <T extends NCPFElement, V extends NCPFElement, U extends DefinedNCPFModularObject> T[][][] match3DArrayConditional(V[][][] from, T[][][] to, U[][][] conditions, Function<U, List<T>> listProvider, Function<U, Boolean> condition){
        for(int x = 0; x<from.length; x++){
            for(int y = 0; y<from[x].length; y++){
                for(int z = 0; z<from[x][y].length; z++){
                    if(conditions[x][y][z]!=null&&condition.apply(conditions[x][y][z]))to[x][y][z] = matchElement(from[x][y][z], listProvider.apply(conditions[x][y][z]));
                }
            }
        }
        return to;
    }
    public <T extends DefinedNCPFObject> T[][][] combine3DArrays(T[][][]... arrays){
        T[][][] finalArray = (T[][][])new DefinedNCPFObject[arrays[0].length][arrays[0][0].length][arrays[0][0][0].length];
        return combine3DArraysInto(finalArray, arrays);
    }
    public <T extends DefinedNCPFObject> T[][][] combine3DArraysInto(T[][][] finalArray, T[][][]... arrays){
        for(T[][][] array : arrays){
            for(int x = 0; x<array.length; x++){
                for(int y = 0; y<array[x].length; y++){
                    for(int z = 0; z<array[x][y].length; z++){
                        if(array[x][y][z]!=null)finalArray[x][y][z] = array[x][y][z];
                    }
                }
            }
        }
        return finalArray;
    }
    public void setReferences(List<NCPFElement> lst){}
    private <T extends NCPFElement, V extends NCPFElement> T matchElement(V element, List<T> list){
        if(element==null)return null;
        for(T elem : list){
            if(element.definition.matches(elem.definition))return elem;
        }
        return null;
    }
}