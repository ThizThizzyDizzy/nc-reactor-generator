package multiblock;
public enum Edge3{
    PXPY(Direction.PX,Direction.PY),
    PXNY(Direction.PX,Direction.NY),
    NXPY(Direction.NX,Direction.PY),
    NXNY(Direction.NX,Direction.NY),
    
    PXPZ(Direction.PX,Direction.PZ),
    PXNZ(Direction.PX,Direction.NZ),
    NXPZ(Direction.NX,Direction.PZ),
    NXNZ(Direction.NX,Direction.NZ),
    
    PYPZ(Direction.PY,Direction.PZ),
    PYNZ(Direction.PY,Direction.NZ),
    NYPZ(Direction.NY,Direction.PZ),
    NYNZ(Direction.NY,Direction.NZ);
    public final Direction[] directions;
    private Edge3(Direction... directions){
        this.directions = directions;
    }
}