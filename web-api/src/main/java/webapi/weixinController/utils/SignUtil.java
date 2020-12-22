package webapi.weixinController.utils;

import tech.constant.Constant;

import java.security.MessageDigest;
import java.util.Arrays;

public class SignUtil {
    
        /**
         * 验证签名
         *
         * @param signature signature
         * @param timestamp timestamp
         * @param nonce nonce
         * @return boolean
         * @throws Exception
         */
        public static boolean checkSignature(String signature, String timestamp,
                                             String nonce) throws Exception {
            // 1.将token、timestamp、nonce三个参数进行字典序排序
            String[] arr = new String[] {Constant.token, timestamp, nonce};
            Arrays.sort(arr);
            // 2. 将三个参数字符串拼接成一个字符串进行sha1加密
            StringBuilder content = new StringBuilder();
            for (String anArr : arr) {
                content.append(anArr);
            }
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            String tmpStr = byteToStr(digest);
            // 3.将sha1加密后的字符串可与signature对比，标识该请求来源于微信
            return tmpStr.equals(signature.toUpperCase());
        }
    
        private static String byteToStr(byte[] byteArray) {
            StringBuilder strDigest = new StringBuilder();
            for (byte aByteArray : byteArray) {
                strDigest.append(byteToHexStr(aByteArray));
            }
            return strDigest.toString();
        }
    
        private static String byteToHexStr(byte mByte) {
            char[] digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                    'B', 'C', 'D', 'E', 'F'};
            char[] tempArr = new char[2];
            tempArr[0] = digit[(mByte >>> 4) & 0X0F];
            tempArr[1] = digit[mByte & 0X0F];
            return new String(tempArr);
        }
    }