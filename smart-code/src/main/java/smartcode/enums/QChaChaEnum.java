package smartcode.enums;

/**
 * @Author: LiuHongYan
 * @Date: 2020/9/16 15:39
 * @Description: 企查查枚举
 **/
public class QChaChaEnum {

    public enum KeyAndSecKey{
        keyAndSecKey("5adbac20bfd84bd4ba57a26ee13f69c0","EF0900CEF318CCA77ACC87034E0A0B2A");
        private String key;
        private String secKey;

        public String getKey() {
            return key;
        }

        public String getSecKey() {
            return secKey;
        }

        KeyAndSecKey(String key, String secKey) {
            this.key = key;
            this.secKey = secKey;
        }
    }

    public enum QiChaChaPort{
        port(" http://api.qichacha.com/ECIV4/Search","http://api.qichacha.com/ECIV4/GetBasicDetailsByName");
        private String like; //企业模糊查询接口
        private String details; //企业详情接口

        public String getLike() {
            return like;
        }

        public String getDetails() {
            return details;
        }

        QiChaChaPort(String like, String details) {
            this.like = like;
            this.details = details;
        }
    }
}
