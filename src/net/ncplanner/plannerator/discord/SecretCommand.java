package net.ncplanner.plannerator.discord;
public abstract class SecretCommand extends Command{
    public SecretCommand(String name, String... alternates){
        super(name, alternates);
    }
    @Override
    public String getHelpText(){
        return "";
    }
    @Override
    public boolean isSecret(){
        return true;
    }
}