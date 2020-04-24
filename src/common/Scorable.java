package common;
public class Scorable<T>{
    private final T object;
    private final double score;
    public Scorable(T object, double score){
        this.object = object;
        this.score = score;
    }
    public T get(){
        return object;
    }
    public double getScore(){
        return score;
    }
}
