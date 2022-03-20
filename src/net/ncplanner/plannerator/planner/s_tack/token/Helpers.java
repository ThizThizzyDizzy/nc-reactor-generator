package net.ncplanner.plannerator.planner.s_tack.token;
public class Helpers{
    public static final String all = "[\\x00-\\x7f]";
    public static final String digit = "[0-9]";
    public static final String letter = "[a-zA-Z]";
    public static final String name = letter+"["+digit+letter+"_]*";
    public static final String sign = "[+-]";
    
    public static final String tab = "\\x09";
    public static final String lf = "\\x0a";
    public static final String cr = "\\x0d";
    public static final String eol = "(?>"+cr+"|"+lf+"|"+cr+lf+")";
    public static final String separator = "(?> |"+tab+"|"+eol+")";
    public static final String separators = "[ "+tab+cr+lf+"]";
    
    public static final String apostrophe = "\\x27";
    public static final String quote = "\\x22";
    public static final String not_eol = "["+all.substring(1, all.length()-1)+"&&[^"+cr+lf+"]]";
    public static final String escape_char = "\\\\"+not_eol;
    
    public static final String c_char = "(?>["+all.substring(1, all.length()-1)+"&&[^"+apostrophe+"\\\\"+lf+cr+"]]|"+escape_char+")";
    public static final String c_char_sequence = c_char+"+";
    public static final String s_char = "(?>["+all.substring(1, all.length()-1)+"&&[^"+quote+"\\\\"+lf+cr+"]]|"+escape_char+")";
    public static final String s_char_sequence = s_char+"*";
    
    public static final String not_star = "["+all.substring(1, all.length()-1)+"&&[^*]]";
    public static final String not_star_slash = "["+not_star.substring(1, all.length()-1)+"&&[^/]]";
    
    public static final String double_slash = "//";
    public static final String slash_star = "/\\*";
    
    public static final String line_comment = double_slash+not_eol+"*?"+eol;
    public static final String block_comment = slash_star+not_star+"*\\*(?>"+not_star_slash+not_star+"*\\*+)*/";
	public static int mod(int a, int b){
		return (a%b+b)%b;
	}
	public static double mod(double a, double b){
		return (a%b+b)%b;
	}
}