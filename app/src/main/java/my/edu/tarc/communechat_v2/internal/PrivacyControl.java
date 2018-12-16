package my.edu.tarc.communechat_v2.internal;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import my.edu.tarc.communechat_v2.model.User;

public class PrivacyControl {
	private String message;

	public PrivacyControl(String message) {
        this.message = removeSpecialChars(message);
	}

	private String removeSpecialChars(String str) {
		//only leave letters and digits in the string
		String removed = str.replaceAll("[^ -~]", "").replaceAll(" ", "");
//		System.out.println(removed);
		return removed;
	}

//	private void setRegex(String str) {
//		regex = new StringToPattern(str);
//	}

    public boolean verify(SharedPreferences pref) {
        //TODO: use everything in pref to compare with message

        String empty = ""; // to avoid NullPointerException
        User user = new User();
        user.setPassword(pref.getString(User.COL_PASSWORD, empty));
        user.setPhone_number(pref.getString(User.COL_PHONE_NUMBER, empty));
        user.setNric(pref.getString(User.COL_NRIC, empty));
        user.setAddress(pref.getString(User.COL_ADDRESS, empty));

        StringToPattern passwordRegex, phoneNumberRegex, nricRegex, addressRegex;
        passwordRegex = new StringToPattern(user.getPassword());
        phoneNumberRegex = new StringToPattern(user.getPhone_number());
        nricRegex = new StringToPattern(user.getNric());
        addressRegex = new StringToPattern(user.getAddress());
        Log.i("[PrivacyControl]", "user: " + user.getPassword() + "\n" + user.getPhone_number() + "\n" + user.getNric() + "\n" + user.getAddress());

        return (passwordRegex.matches(message)
                || phoneNumberRegex.matches(message)
                || nricRegex.matches(message)
                || addressRegex.matches(message));
	}

	public class StringToPattern {

		private String str;
		private Map<String, String> rules;

		public StringToPattern(String str) {
			this.str = str;
			rules = new HashMap<String, String>();
			initializeRules();
		}

        private String generateRegexString() {
			str = removeSpecialChars(str);
			char[] chars = str.toCharArray();
            StringBuilder regexString = new StringBuilder();
            regexString.append(".*(");
			//loop thru each char in string
			for (char ch : chars) {
				//convert char to regex
				//append to regexString
				String regex = charToRegexString(ch);
                regexString.append(regex);
			}
            regexString.append(").*");
            Log.i("[PrivacyControl]", "Regex string: " + regexString);
            //System.out.println(regexString);
            return regexString.toString();
        }

        public boolean matches(String msg) {
            return this.toPattern().matcher(msg).matches();
		}

		public Pattern toPattern() {
            String regexString = generateRegexString();
			Pattern pattern = Pattern.compile(regexString);
			return pattern;
		}

		private String charToRegexString(char ch) {
			String charString = Character.toString(ch);
            return rules.get(charString);
		}

		private String removeSpecialChars(String str) {
			//only leave letters and digits in the string
			String removed = str.replaceAll("[^ -~]", "").replaceAll(" ", "");
			System.out.println(removed);
			return removed;
		}

		private void initializeRules() {
			//TODO: initialize similar letters of chars a-z, 0-9
            rules.put("a", "[aA@]");
            rules.put("b", "([bB6]|(\\|3))");
            rules.put("c", "[cC\\{\\[]");
            rules.put("d", "([dD]|(\\|\\)))");
            rules.put("e", "[eE3]");
            rules.put("f", "[fF]");
            rules.put("g", "[gG]");
            rules.put("h", "([hH]|(\\|\\-\\|))");
            rules.put("i", "[iI1!]");
            rules.put("j", "[jJ]");
            rules.put("k", "([kK]|([\\|1iI!l]\\<))");
            rules.put("l", "([lL!]|(\\|\\_))");
            rules.put("m", "([mM]|(\\|[vV]\\|))");
            rules.put("n", "([nN]|(\\|\\\\\\|))");
            rules.put("o", "[oO0]");
            rules.put("p", "[pP]");
            rules.put("q", "[qQ]");
            rules.put("r", "[rR]");
            rules.put("s", "[sS2zZ]");
            rules.put("t", "[tT+]");
            rules.put("u", "[uU]");
            rules.put("v", "([vV]|(\\\\\\/))");
            rules.put("w", "([wW]|(\\\\\\/\\\\\\/))");
            rules.put("x", "([xX]|(\\>\\<))");
            rules.put("y", "[yY]");
            rules.put("z", "[zZ2]");
            rules.put("1", "[1iIl!]");
            rules.put("2", "[2zZ]");
            rules.put("3", "[3E]");
            rules.put("4", "[4A]");
            rules.put("5", "[5sS]");
            rules.put("6", "[6]");
            rules.put("7", "[7]");
            rules.put("8", "[8]");
            rules.put("9", "[9]");
            rules.put("0", "[0]");
		}

//    public static void main(String[] args) {
//        StringToPattern strToPattern = new StringToPattern("ab");
//        System.out.println("Do ''A6'' matches with ''ab''? " + strToPattern.toPattern().matcher("A6").matches());
//    }
	}

}
