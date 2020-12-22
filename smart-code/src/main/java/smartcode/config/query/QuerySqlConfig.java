//
// Source code recreated from QuerySqlConfig .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.config.query;

import lombok.Data;

@Data
public class QuerySqlConfig {
    private String table;
    private String txt;
    private String key;
    private String linkField;
    private String idField;
    private String pidField;
    private String pidValue;
    private String condition;

    private String getQuerySql() {
        StringBuffer stringBuffer = new StringBuffer();
        String str = " ";
        stringBuffer.append("SELECT ");
        return null;
    }

}
