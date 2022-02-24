package net.ncplanner.plannerator.planner;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
/**
 * Queue class from SimpleLibraryPlus. (https://github.com/ThizThizzyDizzy/SimpleLibraryPlus/blob/master/src/simplelibrary/Queue.java
 */
public class Queue<T> implements Iterable<T>{
    protected QueueEntry head;
    protected QueueEntry tail;
    protected int size;
    public synchronized void enqueue(T obj){
        QueueEntry t = new QueueEntry(obj);
        if(head==null||tail==null){
            head = t;
            tail = t;
            size = 1;
        }else{
            tail.next=t;
            tail = t;
            size++;
        }
    }
    public synchronized T dequeue(){
        if(head==null){
            size = 0;
            return null;
        }
        T obj = head.obj;
        head = head.next;
        size--;
        return obj;
    }
    public synchronized T peek(){
        if(head==null){
            return null;
        }
        return head.obj;
    }
    public synchronized void clear(){
        head = null;
        tail = null;
        size = 0;
    }
    public int size(){
        return recountSize();
    }
    public boolean isEmpty(){
        return head==null;
    }
    /**
     * Clones this Queue into a like-typed ArrayList, leaving the Queue untouched.
     */
    public ArrayList<T> toList(){
        ArrayList<T> t = new ArrayList<>(size);
        QueueEntry h = head;
        while(h!=null){
            t.add(h.obj);
            h = h.next;
        }
        return t;
    }
    public Queue<T> copy() {
        Queue<T> q = new Queue<T>();
        QueueEntry h = head;
        while(h!=null){
            q.enqueue(h.obj);
            h = h.next;
        }
        return q;
    }
    /**
     * Creates a shallow copy of this Queue.
     * WARNING ABOUT SHALLOW COPIES:  ENQUEUE operations WILL ALSO enqueue on ALL DUPLICATE/ORIGINAL QUEUES
     *      created since either queue in question was completely empty.
     *          ALSO, IF A DUPLICATE QUEUE IS THEN ENQUEUED TO, ALL PRIOR DATA ENQUEUED AFTER DUPLICATION WILL BE LOST!
     * DEQUEUE operations WILL NEVER effect other queues.  As such, shallow copies are ONLY recommended for backtracking DEQUEUE operations.
     * If you need to ENQUEUE to a duplicate, use the <code>copy()</code> function, as it generates a more memory-intensive deep copy.
     */
    public Queue<T> shallow(){
        Queue<T> q = new Queue<T>();
        q.head = head;
        q.tail = tail;
        q.size = size;
        return q;
    }
    public int recountSize() {
        QueueEntry h = head;
        size = 0;
        while(h!=null){
            size++;
            h = h.next;
        };
        return size;
    }
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            QueueEntry last = null;
            QueueEntry previous = null;
            boolean removed = true;
            @Override
            public boolean hasNext() {
                return (last==null&&head!=null)||(last!=null&&last.next!=null);
            }
            @Override
            public T next() {
                T obj = (last==null?head.obj:last.next.obj);
                if(!removed) previous = last;//If it was removed, DO NOT update the previous!
                removed = false;
                last = (last==null?head:last.next);
                return obj;
            }
            @Override
            public void remove(){
                if(removed) return;
                removed = true;
                synchronized(Queue.this){
                    if(last==head) head = last.next;//Removing the first entry
                    else if(previous!=null){
                        previous.next = last.next;//Removing second through last entries
                        //If we remove the last entry, last.next==null will be true- so setting previous.next=null will drop the last entry.
                        if(previous.next==null) tail = previous;
                    }
                    size--;
                }
            }
        };
    }
    protected class QueueEntry{
        protected T obj;
        protected QueueEntry next;
        protected QueueEntry(T obj){
            this.obj = obj;
        }
    }
    public Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
    }
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    public Stream<T> streamContent(){
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<T>() {
            @Override
            public boolean hasNext(){
                return !isEmpty();
            }
            @Override
            public T next(){
                return dequeue();
            }
        }, Spliterator.ORDERED), false);
    }
}
