package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.UUID;
public class GenerationThread{
    public boolean running = true;
    public GenerationThread(Runnable r){
        Thread t = new Thread(() -> {
            try{
                while(running)r.run();
            }catch(Exception ex){
                running = false;
                throw new RuntimeException(ex);
            }
        }, "Generation Thread "+UUID.randomUUID().toString());
        t.setDaemon(true);
        t.start();
    }
}