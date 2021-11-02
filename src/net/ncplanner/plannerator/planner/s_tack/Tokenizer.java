package net.ncplanner.plannerator.planner.s_tack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.ncplanner.plannerator.planner.s_tack.token.BlankToken;
import net.ncplanner.plannerator.planner.s_tack.token.BoolValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.CharValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.CommentToken;
import net.ncplanner.plannerator.planner.s_tack.token.DecrementToken;
import net.ncplanner.plannerator.planner.s_tack.token.FloatValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.IdentifierToken;
import net.ncplanner.plannerator.planner.s_tack.token.IncrementToken;
import net.ncplanner.plannerator.planner.s_tack.token.IntValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.InvalidToken;
import net.ncplanner.plannerator.planner.s_tack.token.LBraceToken;
import net.ncplanner.plannerator.planner.s_tack.token.LBracketToken;
import net.ncplanner.plannerator.planner.s_tack.token.LabelToken;
import net.ncplanner.plannerator.planner.s_tack.token.RBraceToken;
import net.ncplanner.plannerator.planner.s_tack.token.RBracketToken;
import net.ncplanner.plannerator.planner.s_tack.token.StringValueToken;
import net.ncplanner.plannerator.planner.s_tack.token.Token;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.AddAllKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.AppendKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.BoolKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.BreakKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.CastKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.CharKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ClearKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ContainsAllKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ContainsKeyKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ContainsKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ContainsValKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.CopyKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.CountKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.CountToKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.DefKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.DictKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.DupKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.EntrySetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ExchKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ExecKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.FloatKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ForeachKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.HaltKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.HashKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IfKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IndexGetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IndexOfKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IndexSetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IntKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.InterpretKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.InvKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.IsEmptyKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.KeySetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ListKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.NegKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.NotKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.PopKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.PrintKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.PutAllKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.RangeKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ReadKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.RemoveAllKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.RemoveKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.RidKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.RollKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.SetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.SizeKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.StringKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.TupleKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.TypeKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.UnpackKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.keyword.ValSetKeyword;
import net.ncplanner.plannerator.planner.s_tack.token.operator.AndEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.AndOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ArithmeticLeftShiftEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ArithmeticLeftShiftOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ArithmeticRightShiftEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ArithmeticRightShiftOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ConcatEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ConcatOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.DivideEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.DivideOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.EqualToOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.EqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.IDivideEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.IDivideOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.LessOrEqualOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.LessThanOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.LogicalRightShiftEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.LogicalRightShiftOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MinusEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MinusOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ModuloEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.ModuloOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MoreOrEqualOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MoreThanOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MultiplyEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.MultiplyOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.NotEqualToOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.OrEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.OrOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.PlusEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.PlusOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.PowerEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.PowerOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.RemainderEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.RemainderOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.XOrEqualsOperator;
import net.ncplanner.plannerator.planner.s_tack.token.operator.XOrOperator;
import simplelibrary.Queue;
public class Tokenizer{
    public static final ArrayList<Token> tokens = new ArrayList<>();
    static{
        tokens.add(new BlankToken());
        tokens.add(new CommentToken());//comment
        
        tokens.add(new LBraceToken());//l_brace
        tokens.add(new RBraceToken());//r_brace
        
        tokens.add(new LBracketToken());//l_bracket
        tokens.add(new RBracketToken());//r_bracket
        
        tokens.add(new DefKeyword());
        
        tokens.add(new ExchKeyword());
        tokens.add(new PopKeyword());
        tokens.add(new DupKeyword());
        
        tokens.add(new RollKeyword());
        tokens.add(new RidKeyword());
        tokens.add(new CopyKeyword());
        
        tokens.add(new CountKeyword());
        tokens.add(new CountToKeyword());
        
        tokens.add(new ReadKeyword());
        tokens.add(new PrintKeyword());
        tokens.add(new InterpretKeyword());
        
        tokens.add(new IntKeyword());
        tokens.add(new BoolKeyword());
        tokens.add(new FloatKeyword());
        tokens.add(new CharKeyword());
        tokens.add(new StringKeyword());
        
        tokens.add(new RangeKeyword());
        tokens.add(new ListKeyword());
        tokens.add(new TupleKeyword());
        tokens.add(new SetKeyword());
        tokens.add(new DictKeyword());
        
        tokens.add(new HashKeyword());
        tokens.add(new UnpackKeyword());
        
        tokens.add(new IfKeyword());
        
        tokens.add(new ForeachKeyword());
        
        tokens.add(new SizeKeyword());  
        tokens.add(new IsEmptyKeyword());
        tokens.add(new ContainsKeyword());
        tokens.add(new AppendKeyword());
        tokens.add(new RemoveKeyword());
        tokens.add(new ContainsAllKeyword());
        tokens.add(new AddAllKeyword());
        tokens.add(new RemoveAllKeyword());
        tokens.add(new ClearKeyword());
        
        tokens.add(new IndexGetKeyword());
        tokens.add(new IndexSetKeyword());
        tokens.add(new IndexOfKeyword());
        
        tokens.add(new ContainsKeyKeyword());
        tokens.add(new ContainsValKeyword());
        tokens.add(new PutAllKeyword());
        tokens.add(new KeySetKeyword());
        tokens.add(new ValSetKeyword());
        tokens.add(new EntrySetKeyword());
        
        tokens.add(new TypeKeyword());
        tokens.add(new CastKeyword());
        
        tokens.add(new ExecKeyword());
        
        tokens.add(new HaltKeyword());
        tokens.add(new BreakKeyword());
        
        tokens.add(new IncrementToken());
        tokens.add(new DecrementToken());
        
        tokens.add(new EqualsOperator());
        
        tokens.add(new PlusEqualsOperator());
        tokens.add(new AndEqualsOperator());
        tokens.add(new OrEqualsOperator());
        tokens.add(new XOrEqualsOperator());
        tokens.add(new MinusEqualsOperator());
        tokens.add(new ConcatEqualsOperator());
        
        tokens.add(new ArithmeticLeftShiftEqualsOperator());
        tokens.add(new ArithmeticRightShiftEqualsOperator());
        tokens.add(new LogicalRightShiftEqualsOperator());
        
        tokens.add(new MultiplyEqualsOperator());
        tokens.add(new DivideEqualsOperator());
        tokens.add(new RemainderEqualsOperator());
        tokens.add(new PowerEqualsOperator());
        tokens.add(new IDivideEqualsOperator());
        tokens.add(new ModuloEqualsOperator());
        
        tokens.add(new EqualToOperator());
        tokens.add(new NotEqualToOperator());
        
        tokens.add(new LessThanOperator());
        tokens.add(new LessOrEqualOperator());
        tokens.add(new MoreThanOperator());
        tokens.add(new MoreOrEqualOperator());
        
        tokens.add(new PlusOperator());
        tokens.add(new AndOperator());
        tokens.add(new OrOperator());
        tokens.add(new XOrOperator());
        tokens.add(new MinusOperator());
        tokens.add(new ConcatOperator());
        
        tokens.add(new ArithmeticLeftShiftOperator());
        tokens.add(new ArithmeticRightShiftOperator());
        tokens.add(new LogicalRightShiftOperator());
        
        tokens.add(new MultiplyOperator());
        tokens.add(new DivideOperator());
        tokens.add(new RemainderOperator());
        tokens.add(new PowerOperator());
        tokens.add(new IDivideOperator());
        tokens.add(new ModuloOperator());
        
        tokens.add(new NotKeyword());
        tokens.add(new NegKeyword());
        tokens.add(new InvKeyword());
        
        tokens.add(new IntValueToken());//int_value
        tokens.add(new BoolValueToken());//bool_value
        tokens.add(new FloatValueToken());//float_value
        tokens.add(new CharValueToken());//char_value
        tokens.add(new StringValueToken());//string_value
        
        tokens.add(new LabelToken());//label
        tokens.add(new IdentifierToken());//indentifier
        
        tokens.add(new InvalidToken());//everything invalid
    }
    public static Token next(String script){
        for(Token token : tokens){
            Matcher matcher = token.getStartPattern().matcher(script);
            if(matcher.find()){
                if(matcher.start()==0)return token.newInstance(matcher.group());
            }
        }
        return null;
    }
    public static ArrayList<Token> tokenize(String script){
        ArrayList<Token> tokenized = new ArrayList<>();
        while(!script.isEmpty()){
            Token token = next(script);
            tokenized.add(token);
            script = script.substring(token.text.length());
        }
        return tokenized;
    }
    public static void cleanup(ArrayList<Token> script){
        for(Iterator<Token> it = script.iterator(); it.hasNext();){
            Token next = it.next();
            if(next instanceof BlankToken || next instanceof CommentToken){
                it.remove();
            }
        }
    }
}