package com.meiren.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @ClassName: StringUtils
 * @Description: TODO
 * @author William.jiang
 * @date 2016年12月20日 上午10:08:42
 *
 */
public class StringUtils {

    /**
     * 获取8为唯一标识符,准备内容
     */
    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
        "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
        "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
        "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
        "W", "X", "Y", "Z" };

    /**
     * 获取8为唯一标识符
     */
    public static String shortUuid() {
        StringBuilder builder = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            builder.append(chars[x % 0x3E]);
        }
        return builder.toString();

    }
    /**
     * 如果字符串为null转换为""如果不为空去掉左右的空格
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        try {
            if (str != null) {
                str = str.trim();
            } else if (str == null) {
                str = "";
            }
            return str;
        } catch (Exception ex) {
        }
        return "";

    }

    /**
     * 判断字符串是否为空或null
     *
     * @param strVal
     *            字符串
     * @return boolean 返回信息
     */
    public static boolean isEmpty(String strVal) {
        boolean bEmpty = true;

        if (null != strVal && !"".equals(strVal)) {
            bEmpty = false;
        }

        return bEmpty;
    }
    /**
     * 判断字符串是否为空或null
     *
     * @param strVal
     *            字符串
     * @return boolean 返回信息
     */
    public static boolean isNotEmpty(String strVal) {
        boolean bEmpty = false;

        if (null != strVal && !"".equals(strVal)) {
            bEmpty = true;
        }

        return bEmpty;
    }

    public static boolean isBlank(String strVal) {
        return isEmpty(strVal);
    }

    public static boolean isNotBlank(String strVal) {
        return isNotEmpty(strVal);
    }

    /**
     * 转换成int
     *
     * @param objVal
     *            输入对象
     * @return int 返回信息
     */
    public static int toInteger(Object objVal, int defaultValue) {
        try {
            return Integer.valueOf(objVal.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 转换成double
     *
     * @param objVal
     * @param defaultValue
     * @return
     */
    public static double toDouble(Object objVal, double defaultValue) {
        try {
            return Double.valueOf(objVal.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 获取Request Param Value
     *
     * @param map
     *            输入Request.getParameterMap对象
     * @param Key
     *            对象的Key
     * @return String 返回信息
     */
    public static String getRequestValue(Map<?, ?> map, String Key) {
        String paramValue = null;

        try {
            String[] paramArray = (String[]) map.get(Key);
            if (0 < paramArray.length) {
                paramValue = paramArray[0];
            }
        } catch (Exception e) {
            paramValue = "";
        }

        return paramValue;
    }

    public static String toString(Object objVal) {
        if (objVal != null) {
            return objVal.toString();
        } else {
            return "";
        }
    }

    /**
     * 返回字符串是为空
     *
     * @param strValue
     *            字符串
     * @return String 返回信息
     */
    public static String getBlankValue(String strValue) {

        if (null == strValue) {
            return "";
        }
        return strValue;
    }

    /**
     * 屏蔽关键信息
     *
     * @param strValue
     *            字符串[13701242046]
     * @return String 返回信息[137*****046]
     */
    public static String shieldKeyByMid(String strValue) {
        int keeplen = 3;
        if (isEmpty(strValue)) {
            return "";
        }
        String keepValue = strValue;
        StringBuilder replaceValue = new StringBuilder();
        if (keeplen > keepValue.length()) {
            return strValue;
        } else if (2 * keeplen >= keepValue.length()) {
            keeplen = 1;
        }
        for (int i = 0; i < (keepValue.length() - 2 * keeplen) && i < 6; i++) {
            replaceValue.append("*");
        }
        keepValue = keepValue.substring(0, keeplen)
            + replaceValue
            + keepValue.substring((keepValue.length() - keeplen),
            (keepValue.length()));
        return keepValue;
    }

    /**
     * 屏蔽关键信息
     *
     * @param strValue
     *            字符串[13701242046]
     * @param keeplen
     *            字符串前后位数(按字符截取,keeplen为偶数)
     * @return String 返回信息[137*****046]
     */
    public static String shieldKeyByMid(String strValue, int keeplen) {
        if (isEmpty(strValue)) {
            return "";
        }
        String keepValue = strValue;
        try {
            byte[] tempStrValues = strValue.getBytes("GB2312");
            StringBuilder replaceValue = new StringBuilder();

            if (keeplen > tempStrValues.length) {
                return strValue;
            } else if (2 * keeplen >= tempStrValues.length) {
                keeplen = 1;
            }
            for (int i = 0; i < (tempStrValues.length - 2 * keeplen) && i < 4; i++) {
                replaceValue.append("*");
            }
            String preKeepValue = new String(tempStrValues, 0, keeplen,
                "GB2312");
            String suKeepValue = new String(tempStrValues, tempStrValues.length
                - keeplen, keeplen, "GB2312");
            keepValue = preKeepValue + replaceValue + suKeepValue;
            return keepValue;
        } catch (Exception e) {
            return keepValue;
        }
    }

    /**
     * 屏蔽关键信息gin
     *
     * @param strValue
     *            字符串[13701242046@hichina.com]
     * @return String 返回信息[137*****046@hichina.com]
     */
    public static String shieldEmailKeyByMid(String strValue) {
        if (isEmpty(strValue)) {
            return "";
        }
        int index = strValue.lastIndexOf("@");
        String emailHead = strValue;
        String emailTail = "";
        if (index > 0) {
            emailHead = strValue.substring(0, index);
            emailTail = strValue.substring(strValue.indexOf(emailHead)
                + emailHead.length());
        }
        return shieldKeyByMid(2, emailHead) + emailTail;
    }

    /**
     * 屏蔽关键信息
     *
     * @param strValue
     *            字符串[13701242046@hichina.com]
     * @return String 返回信息[137*****046@hichina.com] 两边保留length长度
     */
    public static String shieldKeyByMid(int length, String strValue) {
        int keeplen = length;
        if (isEmpty(strValue)) {
            return "";
        }
        String keepValue = strValue;
        StringBuilder replaceValue = new StringBuilder();
        if (keeplen > keepValue.length()) {
            return strValue;
        } else if (2 * keeplen >= keepValue.length()) {
            keeplen = 1;
        }
        for (int i = 0; i < (keepValue.length() - 2 * keeplen) && i < 6; i++) {
            replaceValue.append("*");
        }
        keepValue = keepValue.substring(0, keeplen)
            + replaceValue
            + keepValue.substring((keepValue.length() - keeplen),
            (keepValue.length()));
        return keepValue;
    }

    public static boolean isNumber(String str) {
        if (isEmpty(str))
            return false;

        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 用于判定一个字符串每个元素是否都是正整数，这个方法必要（用于12位数电话号码检验，parseInt方法判定会出现异常）。
     *
     * @param number
     * @return
     */
    public static boolean isBuildByNumber(String number) {
        // 为空判断;
        if (number == null || number.equals("")) {
            return false;
        }
        // 克隆String的char[];
        char[] charArray = number.toCharArray();
        for (char aCharArray : charArray) {
            if (!Character.isDigit(aCharArray)) { // 非'0'-'9';
                return false;
            }
        }
        return true;
    }

    public static int isNumber(String str, int defaultValue) {
        if (isEmpty(str))
            return defaultValue;

        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String replaceString(String strData, String regex,
                                       String replacement) {
        if (strData == null) {
            return null;
        }
        int index;
        index = strData.indexOf(regex);
        StringBuilder strNew = new StringBuilder();
        if (index >= 0) {
            while (index >= 0) {
                strNew.append(strData.substring(0, index)).append(replacement);
                strData = strData.substring(index + regex.length());
                index = strData.indexOf(regex);
            }
            strNew.append(strData);
            return strNew.toString();
        }
        return strData;
    }

    public static String encodeString(String strData) {
        if (strData == null) {
            return "";
        }
        strData = replaceString(strData, "&", "&amp;");
        strData = replaceString(strData, "<", "&lt;");
        strData = replaceString(strData, ">", "&gt;");
        strData = replaceString(strData, "'", "&apos;");
        strData = replaceString(strData, "\"", "&quot;");
        return strData;
    }

    public static String decodeString(String strData) {
        if (strData == null) {
            return "";
        }
        strData = replaceString(strData, "&lt;", "<");
        strData = replaceString(strData, "&gt;", ">");
        strData = replaceString(strData, "&apos;", "'");
        strData = replaceString(strData, "&quot;", "\"");
        strData = replaceString(strData, "&amp;", "&");
        return strData;
    }

    /**
     * 判断两个对象是否相等
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean isEquals(String source, String target) {
        boolean result = false;
        if (source == null) {
            if (target == null) {
                result = true;
            }
        } else {
            if (target != null) {
                result = source.equals(target);
            }
        }

        return result;
    }

    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                    "Malformed   \\uxxxx   encoding.");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }

    public static String md5(String pwd) {
        try {
            // 创建加密对象
            MessageDigest digest = MessageDigest.getInstance("md5");

            // 调用加密对象的方法，加密的动作已经完成
            byte[] bs = digest.digest(pwd.getBytes());
            // 接下来，我们要对加密后的结果，进行优化，按照mysql的优化思路走
            // mysql的优化思路：
            // 第一步，将数据全部转换成正数：
            String hexString = "";
            for (byte b : bs) {
                int temp = b & 255;

                if (temp < 16 && temp >= 0) {
                    // 手动补上一个“0”
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            return hexString;
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String add(Object arg1,Object arg2) {
        return String.valueOf(arg1) + String.valueOf(arg2);
    }

}
