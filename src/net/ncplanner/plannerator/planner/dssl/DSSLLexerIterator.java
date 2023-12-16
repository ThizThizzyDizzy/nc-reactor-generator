package net.ncplanner.plannerator.planner.dssl;
import dssl.interpret.LexerIterator;
import dssl.interpret.TokenExecutor;
import dssl.interpret.TokenIterator;
import dssl.interpret.element.BlockElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;
import net.ncplanner.plannerator.planner.dssl.token.Token;
public class DSSLLexerIterator extends LexerIterator{
    private final File file;
    public String script;
    public int[] linePos;
    public final ArrayList<Token> tokens;
    public Token currentToken;
    private final BiFunction<Token, Integer, Boolean> controlFunc;
    public DSSLLexerIterator(File file, BiFunction<Token, Integer, Boolean> controlFunc) throws FileNotFoundException, IOException{
        super(new PushbackReader(new FileReader(file)));
        this.file = file;
        script = Files.readString(file.toPath());
        tokens = Tokenizer.tokenize(script);
        String[] lines = script.split("\n");
        linePos = new int[lines.length];
        int pos = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            linePos[i] = pos;
            pos+=line.length()+1;
        }
        this.controlFunc = controlFunc;
    }
    @Override
    public dssl.node.Token next(){
        dssl.node.Token t = super.next();
        int pos = linePos[t.getLine()-1]+t.getPos()-1;
        currentToken = null;
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if(token.start==pos){
                currentToken = token;
                break;
            }
        }
        if(currentToken==null||!t.getText().equals(currentToken.text)){
            throw new IllegalStateException("Could not find matching token! L"+t.getLine()+" P"+t.getPos()+": "+t.getText());
        }
        while(!controlFunc.apply(currentToken, t.getLine()-1)){
            try{
                Thread.sleep(1);
            }catch(InterruptedException ex){
                throw new RuntimeException(ex);
            }
        }
        return t;
    }

    TokenIterator getBlockIterator(TokenExecutor exec, BlockElement block) {
        return new TokenIterator() {
            Iterator<dssl.node.Token> internal = block.tokens.iterator();
            @Override
            public void onStart() {
                curr = getNextChecked();
            }
            @Override
            public boolean validNext() {
                return internal.hasNext();
            }
            @Override
            protected dssl.node.Token getNext() {
                dssl.node.Token t = internal.next();
                Token token = getToken(t);
                if(token!=null)currentToken = token;
                if(currentToken==null||!currentToken.text.equals(t.getText())){
                    //accept the loss, probably in another file or whatever
                    //TODO no surrender
//                    throw new IllegalStateException("Could not find matching token! L"+t.getLine()+" P"+t.getPos()+": "+t.getText());
                }else{
                    while(!controlFunc.apply(currentToken, t.getLine()-1)){
                        try{
                            Thread.sleep(1);
                        }catch(InterruptedException ex){
                            throw new RuntimeException(ex);
                        }
                    }
                }
                return t;
            }
        };
    }
    private HashMap<dssl.node.Token, Token> tokenCache = new HashMap<>();
    private Token getToken(dssl.node.Token t){
        Token tok = tokenCache.get(t);
        if(tok!=null)return tok;
        int pos = t.getLine()>linePos.length?-1:(linePos[t.getLine()-1]+t.getPos()-1);
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if(token.start==pos){
                tokenCache.put(t, token);
                return token;
            }
        }
        return null;
    }
}