package my.edu.tarc.communechat_v2.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PrivacyControl {
	private StringToPattern regex;
	private String message;

//    public static void main(String[] args) {
//        PrivacyControl pc = new PrivacyControl("Kono B@Ka h3n+@1 uRUZa! shineh!");
//        pc.setRegex("baka hentai urusai");
//        System.out.println(pc.verify());
//    }

	public PrivacyControl(String message) {
		this.message = removeSpecialChars(message.toLowerCase());
	}

	private String removeSpecialChars(String str) {
		//only leave letters and digits in the string
		String removed = str.replaceAll("[^ -~]", "").replaceAll(" ", "");
		System.out.println(removed);
		return removed;
	}

	private void setRegex(String str) {
		regex = new StringToPattern(str);
	}

	public boolean verify() {
		return regex.toPattern().matcher(message).matches();
	}

	public class StringToPattern {

		private String str;
		private String regexString;
		private Map<String, String> rules;

		public StringToPattern(String str) {
			this.str = str;
			regexString = "";
			rules = new HashMap<String, String>();
			initializeRules();
		}

		private void generateRegexString() {
			str = removeSpecialChars(str);
			char[] chars = str.toCharArray();
			regexString += ".*";
			//loop thru each char in string
			for (char ch : chars) {
				//convert char to regex
				//append to regexString
				String regex = charToRegexString(ch);
				regexString += regex;
			}
			regexString += ".*";
			System.out.println(regexString);
		}

		public Pattern toPattern() {
			generateRegexString();
			Pattern pattern = Pattern.compile(regexString);
			return pattern;
		}

		private String charToRegexString(char ch) {
			String charString = Character.toString(ch);
			return "[" + rules.get(charString) + "]";
		}

		private String removeSpecialChars(String str) {
			//only leave letters and digits in the string
			String removed = str.replaceAll("[^ -~]", "").replaceAll(" ", "");
			System.out.println(removed);
			return removed;
		}

		private void initializeRules() {
			//TODO: initialize similar letters of chars a-z, 0-9
			rules.put("a", "aA@");
			rules.put("b", "bB6");
			rules.put("c", "cC\\{\\[");
			rules.put("d", "dD");
			rules.put("e", "eE3");
			rules.put("f", "fF");
			rules.put("g", "gG");
			rules.put("h", "hH");
			rules.put("i", "iI1!");
			rules.put("j", "jJ");
			rules.put("k", "kK");
			rules.put("l", "lL!");
			rules.put("m", "mM");
			rules.put("n", "nN");
			rules.put("o", "oO");
			rules.put("p", "pP");
			rules.put("q", "qQ");
			rules.put("r", "rR");
			rules.put("s", "sS2zZ");
			rules.put("t", "tT+");
			rules.put("u", "uU");
			rules.put("v", "vV");
			rules.put("w", "wW");
			rules.put("x", "xX");
			rules.put("y", "yY");
			rules.put("z", "zZ");
			rules.put("1", "1");
			rules.put("2", "2");
			rules.put("3", "3");
			rules.put("4", "4");
			rules.put("5", "5");
			rules.put("6", "6");
			rules.put("7", "7");
			rules.put("8", "8");
			rules.put("9", "9");
			rules.put("0", "0");
		}

//    public static void main(String[] args) {
//        StringToPattern strToPattern = new StringToPattern("ab");
//        System.out.println("Do ''A6'' matches with ''ab''? " + strToPattern.toPattern().matcher("A6").matches());
//    }
	}

}
