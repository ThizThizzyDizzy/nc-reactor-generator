package discord.play;
import discord.play.game.StopReason;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
public abstract class Game{
    protected int timeout = 300_000;//5 minutes
    private long lastUpdate;
    private final String name;
    public boolean running;
    protected MessageChannel channel;
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
                    timeout();
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
    public final void timeout(){
        running = false;
        stop(channel, StopReason.TIMEOUT);
    }
    public abstract void stop(MessageChannel channel, StopReason reason);
    protected void update(){
        lastUpdate = System.currentTimeMillis();
    }
    public abstract void onMessage(Message message);
    public abstract boolean canAnyoneStop();
}