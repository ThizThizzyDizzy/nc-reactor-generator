package discord.play;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
public abstract class Action{
    private final int time;
    public User user;
    public boolean cancelled = false;
    public Action(int time){
        this.time = time;
    }
    public void start(User user, TextChannel channel){
        this.user = user;
        channel.sendMessage(getBeginMessage()).queue();
        Thread t = new Thread(() -> {
            try{
                Thread.sleep(time);
            }catch(InterruptedException ex){}
            if(cancelled)return;
            channel.sendMessage(getFinishedMessage()).queue();
            SmoreBot.actions.remove(user.getIdLong());
            finish();
        });
        t.setName(user.getName()+" "+getName()+" Thread");
        t.setDaemon(true);
        t.start();
    }
    protected abstract String getBeginMessage();
    protected abstract String getFinishedMessage();
    protected abstract String getCanceledMessage();
    protected abstract String getName();
    public abstract void finish();
    public void cancel(TextChannel channel){
        cancelled = true;
        channel.sendMessage(getCanceledMessage()).queue();
    }
    public abstract String getAction();
}