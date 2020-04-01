package common;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
public class WeightedRandom{
    public static <T extends Scorable> T random(Iterable<T> ts){
        RandomCollection<T> rand = new RandomCollection<>();
        for(T t : ts){
            rand.add(t.getScore(), t);
        }
        return rand.next();
    }
    private static class RandomCollection<E>{
        private final NavigableMap<Double, E> map = new TreeMap<>();
        private final Random random;
        private double total = 0;
        public RandomCollection(){
            this(new Random());
        }
        public RandomCollection(Random random){
            this.random = random;
        }
        public RandomCollection<E> add(double weight, E result){
            if(weight<=0)return this;
            total+=weight;
            map.put(total, result);
            return this;
        }
        public E next(){
            if(map.isEmpty())return null;
            double value = random.nextDouble()*total;
            return map.higherEntry(value).getValue();
        }
    }
}