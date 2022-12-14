package net.ncplanner.plannerator.planner.dssl.token;
public abstract class ESSLToken extends Token{
    public ESSLToken(String regex){
        super(regex);
    }
    public ESSLToken(String regex, boolean plain){
        super(regex, plain);
    }
    public abstract String compile();
}