package multiblock;
public enum Edge{
    PP(Direction.PX,Direction.PY),
    PN(Direction.PX,Direction.NY),
    NP(Direction.NX,Direction.PY),
    NN(Direction.NX,Direction.NY);
    public final Direction[] directions;
    private Edge(Direction... directions){
        this.directions = directions;
    }
}