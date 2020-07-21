package discord;
public abstract class SecretCommand extends Command{
    public SecretCommand(String name){
        super(name);
    }
    @Override
    public boolean isSecret(){
        return true;
    }
}