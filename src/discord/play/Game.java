package discord.play;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
public abstract class Game{
    protected int timeout = 300_000;//5 minutes
    private long lastUpdate;
    private final String name;
    public boolean running;
    private MessageChannel channel;
    public Game(String name){
        this.name = name;
    }
    public final void startGame(MessageChannel channel){
        this.channel = channel;
        update();
        running = true;
        Thread thread = new Thread(() -> {
            while(running){
                try{
                    Thread.sleep(100);
                }catch(InterruptedException ex){}
                if(System.currentTimeMillis()-lastUpdate>timeout){
                    stopGame();
                }
            }
            if(PlayBot.games.get(channel.getIdLong())==this)PlayBot.games.remove(channel.getIdLong());
        });
        thread.setDaemon(true);
        thread.setName(getName());
        thread.start();
        running = start(channel);
    }
    protected abstract boolean start(MessageChannel channel);
    public String getName(){
        return name;
    }
    public final void stopGame(){
        running = false;
        stop(channel);
    }
    protected abstract void stop(MessageChannel channel);
    protected void update(){
        lastUpdate = System.currentTimeMillis();
    }
    public abstract void onMessage(Message message);
}