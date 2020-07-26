package discord.play;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
public abstract class Game{
    private static final int timeout = 60000;
    private long lastUpdate;
    private final String name;
    public boolean running;
    private TextChannel channel;
    public Game(String name){
        this.name = name;
    }
    public final void startGame(GuildMessageReceivedEvent event){
        channel = event.getChannel();
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
            if(PlayBot.currentGame==this)PlayBot.currentGame = null;
        });
        thread.setDaemon(true);
        thread.setName(getName());
        thread.start();
        start(event);
    }
    protected abstract void start(GuildMessageReceivedEvent event);
    public String getName(){
        return name;
    }
    public final void stopGame(){
        running = false;
        stop(channel);
    }
    protected abstract void stop(TextChannel channel);
    protected void update(){
        lastUpdate = System.currentTimeMillis();
    }
    public abstract void onMessage(GuildMessageReceivedEvent event);
}