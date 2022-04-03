package net.ncplanner.plannerator.planner.dssl.object;
import java.util.Collection;
public abstract class StackCollection<T> extends StackObject{
    final T value;
    public StackCollection(T value){
        this.value = value;
    }
    @Override
    public Type getType(){
        return Type.COLLECTION;
    }
    @Override
    public T getValue(){
        return value;
    }
    @Override
    public String toString(){
        return "collection";
    }
    @Override
    public abstract StackObject duplicate();
    public abstract Collection<StackObject> collection();
    public abstract long size();
    public abstract boolean isEmpty();
    @Override
    public StackObject cast(StackObject obj){
        return obj.asCollection();
    }
    public boolean addAll(Collection<StackObject> collection){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public boolean add(StackObject elem){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public void clear(){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public StackObject get(StackObject elem){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public boolean containsAll(Collection<StackObject> collection){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public boolean contains(StackObject elem){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public void putAll(StackObject elem){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public StackObject put(StackObject elem1, StackObject elem2){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public StackObject removeAll(Collection<StackObject> collection){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
    public StackObject remove(StackObject elem){
        throw new UnsupportedOperationException("Function not supported by this collection!");
    }
}