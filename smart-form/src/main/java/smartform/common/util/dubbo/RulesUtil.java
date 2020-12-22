package smartform.common.util.dubbo;

import org.apache.commons.lang3.StringEscapeUtils;
import smartform.common.util.HtmlUtil;
import smartform.widget.model.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 规则验证工具
 *
 * @author KaminanGTO
 *
 */
public class RulesUtil {

	/**
	 * @param regex 正则表达式字符串
	 * @param str   要匹配的字符串
	 * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
	 */
	public static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * 规则验证
	 *
	 * @param rule
	 * @param value
	 * @return
	 */
	public static boolean checkRules(RuleBase rule, Object value) {
		if(value == null)
		{
			// 如果是null，则不验证其他规则
			if (rule.getType() != 1)
				return true;
		}
		switch (rule.getType()) {
		case 1: // 必填
		{
			RuleRequired ruleRequired = (RuleRequired) rule;
			if (ruleRequired.getRequired() && value == null) {
				return false;
			}
			if (ruleRequired.getWhitespace()) {
				if (value != null) {
					if (String.valueOf(value).trim().length() == 0) {
						return false;
					}
				}
			}
		}
			break;
		case 2: // 长度
		{
			RuleLenght ruleLenght = (RuleLenght) rule;

			String strValue = value == null ? "" : StringEscapeUtils.unescapeHtml4(value.toString());
			strValue = HtmlUtil.delHTMLTag(strValue);
			int length = strValue.length();
			if (ruleLenght.getMinLenght() != null) {
				if (length < ruleLenght.getMinLenght()) {
					return false;
				}
			}
			if (ruleLenght.getMaxLenght() != null) {
				if (length > ruleLenght.getMaxLenght()) {
					return false;
				}
			}
		}
			break;
		case 3: // 正则
		{
			RuleRegexp ruleRegexp = (RuleRegexp) rule;
			String str = String.valueOf(value);
			switch (ruleRegexp.getRegexpID()) {
			case "0": // 自定义正则
			{
				String regExp = ruleRegexp.getRegexp();
				if (!match(regExp, str)) {
					return false;
				}
			}
				break;
			case "1": // 中国手机号
			{
				if (!IsHandset(str)) {
					return false;
				}
			}
				break;
			case "2": // 中国电话号码
			{
				if (!IsTelephone(str)) {
					return false;
				}
			}
				break;
			case "3": // 邮箱
			{
				if (!IsEmail(str)) {
					return false;
				}
			}
				break;
			case "4": // url
			{
				if (!IsUrl(str)) {
					return false;
				}
			}
				break;
			case "5": // 字母+数字
			{
				if (!IsLetterNum(str)) {
					return false;
				}
			}
				break;
			case "6": // 中国身份证
			{
				if(!IsIDCard(str)) {
					return false;
				}
			}
				break;
			}
		}
			break;
		case 4: // 符号
		{
			RuleSymbol ruleSymbol = (RuleSymbol) rule;
			Double num = value == null ? 0L : Double.valueOf(String.valueOf(value));
			switch (ruleSymbol.getPlusMinus()) {
			case 1: // 正数
			{
				if (num < 0) {
					return false;
				}
			}
				break;
			case 2: // 负数
			{
				if (num >= 0) {
					return false;
				}
			}
				break;
			default: {

			}
				break;
			}
		}
			break;
		case 5: // 小数
		{
			if (value == null)
				break;
			RuleDecimal RuleDecimal = (RuleDecimal) rule;
			int length = 0;
			String numStr = String.valueOf(value);
			String[] num = numStr.split("\\.");
			if (num.length == 2) {
				for (;;) {
					if (num[1].endsWith("0")) {
						num[1] = num[1].substring(0, num[1].length() - 1);
					} else {
						break;
					}
				}
				length = num[1].length();
			}
			if (!RuleDecimal.getDecimal()) {
				if (length > 0) {
					return false;
				}
			} else {
				if (length > RuleDecimal.getDecimalLenght()) {
					return false;
				}
			}
		}
			break;
		case 6: // 数字大小
		{
			RuleSize ruleSize = (RuleSize) rule;
			BigDecimal num = null;
			if(value == null)
			{
				num = BigDecimal.valueOf(0);
			}
			else if(value instanceof Integer)
			{
				num = BigDecimal.valueOf((Integer) value);
			}
			else if(value instanceof Long)
			{
				num = BigDecimal.valueOf((Long) value);
			}
			else if(value instanceof Double)
			{
				num = BigDecimal.valueOf((Double) value);
			}
			else
			{
				num = BigDecimal.valueOf(Double.valueOf(String.valueOf(value)));
			}
			if (ruleSize.getMinValue() != null) {
				if (num.compareTo(ruleSize.getMinValue()) == -1) {
					return false;
				}
			}
			if (ruleSize.getMaxValue() != null) {
				if (num.compareTo(ruleSize.getMaxValue()) == 1) {
					return false;
				}
			}
		}
			break;
		case 7: // 预设规则（自定义规则）暂不支持
		{
			RuleCustom ruleCustom = (RuleCustom) rule;
		}
			break;
		default: {

		}
			break;
		}
		return true;
	}

	/**
	 * 验证邮箱
	 *
	 * @param 待验证的字符串
	 * @return 如果是符合的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsEmail(String str) {
		String regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return match(regex, str);
	}

	/**
	 * 验证电话号码
	 *
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsTelephone(String str) {
		// String regex = "^(\\d{3,4}-)?\\d{6,8}$";
		String regex = "^(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{7,14}$";
		return match(regex, str);
	}

	/**
	 * 验证输入手机号码
	 *
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsHandset(String str) {
		// String regex = "^[1]+[3,5]+\\d{9}$";
		String regex = "^1(3|4|5|6|7|8|9)\\d{9}$";
		return match(regex, str);
	}

	/**
	 * 验证网址Url
	 *
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
	public static boolean IsUrl(String str) {
		String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
		return match(regex, str);
	}

	/**
	 * 字母+数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean IsLetterNum(String str) {
		String regex = "^\\w+$";
		return match(regex, str);
	}

	// 省(直辖市)代码表
	private static String provinceCode[] = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34",
			"35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64",
			"65", "71", "81", "82", "91" };

	/**
	 * 身份证验证
	 * @param idStr
	 * @return
	 */
	public static boolean IsIDCard(String idStr)
	{
		String[] valCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
		String[] wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2" };
		String ai = "";
		// ================ 号码的长度 15位或18位 ================
		if (idStr.length() != 15 && idStr.length() != 18) {
			return false;//"身份证号长度只能是15位或18位"
		}
		// ================ 数字 除最后一位都为数字 ================
		if (idStr.length() == 18) {
			ai = idStr.substring(0, 17);
		} else if (idStr.length() == 15) {
			ai = idStr.substring(0, 6) + "19" + idStr.substring(6, 15);
		}
		if (IsNumber(ai) == false) {
			return false;//"证件号码输入有误"
		}
		// ================ 出生年月是否有效 ================
		String strYear = ai.substring(6, 10);// 年份
		String strMonth = ai.substring(10, 12);// 月份
		String strDay = ai.substring(12, 14);// 月份
		if (isDate(strYear + "-" + strMonth + "-" + strDay) == false) {
			return false;//"证件号码输入有误"
		}
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
				|| (gc.getTime().getTime() - s.parse(strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
				return false;//"证件号码输入有误"
			}
		} catch (NumberFormatException e) {
			return false;//"证件号码输入有误"Ï
		} catch (ParseException e) {
			return false;//"证件号码输入有误"
		}
		if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
			return false;//"证件号码输入有误"
		}
		if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
			return false;//"证件号码输入有误"
		}
		//省份信息是否正确
		if (!checkProvinceId(ai.substring(0, 2)))
		{
			return false;
		}
		// ================ 判断最后一位的值 ================
		int totalmulAiWi = 0;
		for (int i = 0; i < 17; i++) {
			totalmulAiWi = totalmulAiWi + Integer.parseInt(String.valueOf(ai.charAt(i))) * Integer.parseInt(wi[i]);
		}
		int modValue = totalmulAiWi % 11;
		String strVerifyCode = valCodeArr[modValue];
		ai = ai + strVerifyCode;
		if (idStr.length() == 18) {
			if (ai.equals(idStr) == false) {
				return false;//"身份证号输入有误,最后一位若是字母请大写"
			}
		}
		return true;
	}

    /**
     * 检查身份证的省份信息是否正确（使用与18/15位身份证）
     * @param provinceid
     * @return
     */
    private static boolean checkProvinceId(String provinceid){
        for (String id : provinceCode) {
            if (id.equals(provinceid)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * 将已经加上年份的15位身份证，按照规则由17位推算出第18位
	 *
	 * @param idCardNumber
	 * @return
	 */
    private static char getVerifyCode(String idCardNumber) {
		char[] Ai = idCardNumber.toCharArray();
		int[] Wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		char[] verifyCode = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
		int S = 0;
		int Y;
		for (int i = 0; i < Wi.length; i++) {
			S += (Ai[i] - '0') * Wi[i];
		}
		Y = S % 11;
		return verifyCode[Y];
	}

	/**
	 * 验证数字输入
	 *
	 * @param 待验证的字符串
	 * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
	 */
    private static boolean IsNumber(String str) {
		String regex = "^[0-9]*$";
		return match(regex, str);
	}

	/**
     * 功能：判断字符串是否为日期格式
     *
     * @param str
     * @return
     */
    private static boolean isDate(String strDate) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}
