//
// Source code recreated from ColumnMetaUtils .class file by IntelliJ IDEA
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
import smartcode.form.vo.MainTableVo;
import smartcode.form.vo.SubTableVo;
import java.util.*;

public class CodeGenerateOneToMany extends GenUtils implements IGenerate {
    private static final Logger e = LoggerFactory.getLogger(CodeGenerateOneToMany.class);
    private static String f;
    public static String a = "A";
    public static String b = "B";
    private MainTableVo g;
    private List<ColumnVo> h;
    private List<ColumnVo> i;
    private List<SubTableVo> j;
    private static DbReadTableUtil k = new DbReadTableUtil();

    public CodeGenerateOneToMany(MainTableVo mainTableVo, List<SubTableVo> subTables) {
        this.j = subTables;
        this.g = mainTableVo;
    }

    public CodeGenerateOneToMany(MainTableVo mainTableVo, List<ColumnVo> mainColums, List<ColumnVo> originalMainColumns, List<SubTableVo> subTables) {
        this.g = mainTableVo;
        this.h = mainColums;
        this.i = originalMainColumns;
        this.j = subTables;
    }

    public Map<String, Object> a() throws Exception {
        HashMap var1 = new HashMap();
        var1.put("bussiPackage", PathCodeUtils.h);
        var1.put("entityPackage", this.g.getEntityPackage());
        var1.put("entityName", this.g.getEntityName());
        var1.put("tableName", this.g.getTableName());
        var1.put("ftl_description", this.g.getFtlDescription());
        var1.put("primaryKeyField", PathCodeUtils.m);
        if (this.g.getFieldRequiredNum() == null) {
            this.g.setFieldRequiredNum(StringUtils.isNotEmpty(PathCodeUtils.n) ? Integer.parseInt(PathCodeUtils.n) : -1);
        }

        if (this.g.getSearchFieldNum() == null) {
            this.g.setSearchFieldNum(StringUtils.isNotEmpty(PathCodeUtils.o) ? Integer.parseInt(PathCodeUtils.o) : -1);
        }

        if (this.g.getFieldRowNum() == null) {
            this.g.setFieldRowNum(Integer.parseInt(PathCodeUtils.q));
        }

        var1.put("tableVo", this.g);

        try {
            if (this.h == null || this.h.size() == 0) {
                this.h = DbReadTableUtil.a(this.g.getTableName());
            }

            if (this.i == null || this.i.size() == 0) {
                this.i = DbReadTableUtil.b(this.g.getTableName());
            }

            var1.put("columns", this.h);
            var1.put("originalColumns", this.i);
            Iterator var2 = this.i.iterator();

            while(var2.hasNext()) {
                ColumnVo var3 = (ColumnVo)var2.next();
                if (var3.getFieldName().toLowerCase().equals(PathCodeUtils.m.toLowerCase())) {
                    var1.put("primaryKeyPolicy", var3.getFieldType());
                }
            }

            var2 = this.j.iterator();

            while(var2.hasNext()) {
                SubTableVo var12 = (SubTableVo)var2.next();
                List var4;
                if (var12.getColums() == null || var12.getColums().size() == 0) {
                    var4 = DbReadTableUtil.a(var12.getTableName());
                    var12.setColums(var4);
                }

                if (var12.getOriginalColumns() == null || var12.getOriginalColumns().size() == 0) {
                    var4 = DbReadTableUtil.b(var12.getTableName());
                    var12.setOriginalColumns(var4);
                }

                String[] var13 = var12.getForeignKeys();
                ArrayList var5 = new ArrayList();
                String[] var6 = var13;
                int var7 = var13.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    String var9 = var6[var8];
                    var5.add(DbReadTableUtil.d(var9));
                }

                var12.setForeignKeys((String[])var5.toArray(new String[0]));
                var12.setOriginalForeignKeys(var13);
            }

            var1.put("subTables", this.j);
        } catch (Exception var10) {
            throw var10;
        }

        long var11 = NonceUtils.c() + NonceUtils.g();
        var1.put("serialVersionUID", String.valueOf(var11));
        e.info("code template data: " + var1.toString());
        return var1;
    }

    public List<String> generateCodeFile(String stylePath) throws Exception {
        String var2 = PathCodeUtils.g;
        Map var3 = this.a();
        String var4 = PathCodeUtils.k;
        if (a(var4, "/").equals("jeecg/code-template")) {
            var4 = "/" + a(var4, "/") + "/onetomany";
            PathCodeUtils.b(var4);
        }

        GenUtilsEx var5 = new GenUtilsEx(var4);
        var5.a(stylePath);
        this.a(var5, var2, var3);
        e.info("----- jeecg-boot ---- generate  code  success =======> 主表名：" + this.g.getTableName());
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
}
