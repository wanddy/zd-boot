//
// Source code recreated from GenUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.gen;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.form.gen.service.IGenerate;
import smartcode.form.utils.DbReadTableUtil;
import smartcode.form.utils.PathCodeUtils;
import smartcode.form.vo.ColumnVo;
import smartcode.form.vo.TableVo;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CodeGenerateOne extends GenUtils implements IGenerate {
    private static final Logger a = LoggerFactory.getLogger(CodeGenerateOne.class);
    private TableVo b;
    private List<ColumnVo> e;
    private List<ColumnVo> f;

    public CodeGenerateOne(TableVo tableVo) {
        this.b = tableVo;
    }

    public CodeGenerateOne(TableVo tableVo, List<ColumnVo> columns, List<ColumnVo> originalColumns) {
        this.b = tableVo;
        this.e = columns;
        this.f = originalColumns;
    }

    public Map<String, Object> a() throws Exception {
        HashMap var1 = new HashMap();
        var1.put("bussiPackage", PathCodeUtils.h);
        var1.put("entityPackage", this.b.getEntityPackage());
        var1.put("entityName", this.b.getEntityName());
        var1.put("tableName", this.b.getTableName());
        var1.put("primaryKeyField", PathCodeUtils.m);
        if (this.b.getFieldRequiredNum() == null) {
            this.b.setFieldRequiredNum(StringUtils.isNotEmpty(PathCodeUtils.n) ? Integer.parseInt(PathCodeUtils.n) : -1);
        }

        if (this.b.getSearchFieldNum() == null) {
            this.b.setSearchFieldNum(StringUtils.isNotEmpty(PathCodeUtils.o) ? Integer.parseInt(PathCodeUtils.o) : -1);
        }

        if (this.b.getFieldRowNum() == null) {
            this.b.setFieldRowNum(Integer.parseInt(PathCodeUtils.q));
        }

        var1.put("tableVo", this.b);

        try {
            if (this.e == null || this.e.size() == 0) {
                this.e = DbReadTableUtil.a(this.b.getTableName());
            }

            var1.put("columns", this.e);
            if (this.f == null || this.f.size() == 0) {
                this.f = DbReadTableUtil.b(this.b.getTableName());
            }

            var1.put("originalColumns", this.f);
            Iterator var2 = this.f.iterator();

            while(var2.hasNext()) {
                ColumnVo var3 = (ColumnVo)var2.next();
                if (var3.getFieldName().toLowerCase().equals(PathCodeUtils.m.toLowerCase())) {
                    var1.put("primaryKeyPolicy", var3.getFieldType());
                }
            }
        } catch (Exception var4) {
            throw var4;
        }

        long var5 = NonceUtils.c() + NonceUtils.g();
        var1.put("serialVersionUID", String.valueOf(var5));
        a.info("load template data: " + var1.toString());
        return var1;
    }

    public List<String> generateCodeFile(String stylePath) throws Exception {
        a.debug("----jeecg---Code----Generation----[单表模型:" + this.b.getTableName() + "]------- 生成中。。。");
        String var2 = PathCodeUtils.g;
        Map var3 = this.a();
        String var4 = PathCodeUtils.k;
        if (a(var4, "/").equals("jeecg/code-template")) {
            var4 = "/" + a(var4, "/") + "/one";
            PathCodeUtils.b(var4);
        }

        GenUtilsEx var5 = new GenUtilsEx(var4);
        var5.a(stylePath);
        this.a(var5, var2, var3);
        a.info(" ----- jeecg-boot ---- generate  code  success =======> 表名：" + this.b.getTableName() + " ");
        return this.d;
    }

    public List<String> generateCodeFile(String projectPath, String templatePath, String stylePath) throws Exception {
        if (projectPath != null && !"".equals(projectPath)) {
            PathCodeUtils.a(projectPath);
        }

        if (templatePath != null && !"".equals(templatePath)) {
            PathCodeUtils.b(templatePath);
        }

        this.generateCodeFile(stylePath);
        return this.d;
    }

    public static void main(String[] args) {
        TableVo var1 = new TableVo();
        var1.setTableName("demo");
        var1.setPrimaryKeyPolicy("uuid");
        var1.setEntityPackage("test");
        var1.setEntityName("JeecgDemo");
        var1.setFtlDescription("jeecg 测试demo");

        try {
            (new CodeGenerateOne(var1)).generateCodeFile((String)null);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
