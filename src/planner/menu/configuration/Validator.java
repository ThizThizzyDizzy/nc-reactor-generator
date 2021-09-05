package planner.menu.configuration;
import planner.Core;
import simplelibrary.image.Color;
public abstract class Validator{
    private ValidatorMessage lastMessage;
    public abstract void stage(String stage);
    public abstract void onFinish();
    public final void finish(){
        if(lastMessage!=null)message(lastMessage);
        onFinish();
    }
    public ValidatorMessage warn(String message){
        if(lastMessage!=null)message(lastMessage);
        return lastMessage = new ValidatorMessage(message).color(Core.theme.getValidatorWarningTextColor());
    }
    public ValidatorMessage error(String message){
        if(lastMessage!=null)message(lastMessage);
        return lastMessage = new ValidatorMessage(message).color(Core.theme.getValidatorErrorTextColor());
    }
    public abstract void message(ValidatorMessage message);
    public class ValidatorMessage{
        public final String message;
        public Color color;
        public String hint;
        public Runnable solveFunc;
        public String solveHint;
        public ValidatorMessage(String message){
            this.message = message;
        }
        public ValidatorMessage color(Color color){
            this.color = color;
            return this;
        }
        public ValidatorMessage hint(String hint){
            this.hint = hint;
            return this;
        }
        public ValidatorMessage solve(Runnable solveFunc, String solveHint){
            this.solveFunc = solveFunc;
            this.solveHint = solveHint;
            return this;
        }
    }
}