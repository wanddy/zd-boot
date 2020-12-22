//
// Source code recreated from GenUtils .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package smartcode.form.gen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartcode.form.utils.PathCodeUtils;
import smartcode.form.utils.bUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenUtils {
    private static final Logger a = LoggerFactory.getLogger(GenUtils.class);
    protected static String c = "UTF-8";
    protected List<String> d = new ArrayList();

    public GenUtils() {
    }

    protected void a(GenUtilsEx genUtilsEx, String str, Map<String, Object> map) throws Exception {
        a.debug("--------generate----projectPath--------" + str);

        for(int i = 0; i < genUtilsEx.b().size(); i++) {
            File file = genUtilsEx.b().get(i);
            this.a(str, file, map, genUtilsEx);
        }
    }

    protected void a(String str, File file, Map<String, Object> map, GenUtilsEx genUtilsEx) throws Exception {
        if (file == null) {
            throw new IllegalStateException("'templateRootDir' must be not null");
        } else {
            a.info("  load template from templateRootDir = '" + file.getAbsolutePath() + "',stylePath ='" + genUtilsEx.a() + "',  out GenerateRootDir:" + PathCodeUtils.g);
            List files = GenUtilsIm.a(file);
            a.debug("----srcFiles----size-----------" + files.size());
            a.debug("----srcFiles----list------------" + files.toString());

            for(int i = 0; i < files.size(); i++) {
                File file1 = (File)files.get(i);
                this.a(str, file, map, file1, genUtilsEx);
            }

        }
    }

    protected void a(String string, File file, Map<String, Object> map, File file1, GenUtilsEx genUtilsEx) throws Exception {
        a.debug("-------templateRootDir--" + file.getPath());
        a.debug("-------srcFile--" + file1.getPath());
        String var6 = GenUtilsIm.a(file, file1);

        try {
            a.debug("-------templateFile--" + var6);
            String var7 = a(map, var6, genUtilsEx);
            a.debug("-------outputFilepath--" + var7);
            String var8;
            if (var7.startsWith("java")) {
                var8 = string + File.separator + PathCodeUtils.i.replace(".", File.separator);
                var7 = var7.substring("java".length());
                var7 = var8 + var7;
                a.debug("-------java----outputFilepath--" + var7);
                this.a(var6, var7, map, genUtilsEx);
            } else if (var7.startsWith("webapp")) {
                var8 = string + File.separator + PathCodeUtils.j.replace(".", File.separator);
                var7 = var7.substring("webapp".length());
                var7 = var8 + var7;
                a.debug("-------webapp---outputFilepath---" + var7);
                this.a(var6, var7, map, genUtilsEx);
            }
        } catch (Exception var10) {
            a.error(var10.toString(), var10);
        }

    }

    protected void a(String var1, String var2, Map<String, Object> var3, GenUtilsEx var4) throws Exception {
        if (var2.endsWith("i")) {
            var2 = var2.substring(0, var2.length() - 1);
        }

        Template var5 = this.a(var1, var4);
        var5.setOutputEncoding(c);
        File var6 = GenUtilsIm.c(var2);
        a.info("[generate]\t template:" + var1 + " ==> " + var2);
        bUtils.a(var5, var3, var6, c);
        if (!this.a(var6)) {
            this.d.add("生成成功：" + var2);
        }

        if (this.a(var6)) {
            this.a(var6, "#segment#");
        }

    }

    protected Template a(String var1, GenUtilsEx var2) throws IOException {
        return bUtils.a(var2.b(), c, var1).getTemplate(var1);
    }

    protected boolean a(File var1) {
        return var1.getName().startsWith("[1-n]");
    }

    protected void a(File var1, String var2) {
        InputStreamReader var3 = null;
        BufferedReader var4 = null;
        ArrayList var5 = new ArrayList();
        boolean var20 = false;

        int var28;
        label341: {
            label342: {
                try {
                    var20 = true;
                    var3 = new InputStreamReader(new FileInputStream(var1), "UTF-8");
                    var4 = new BufferedReader(var3);
                    boolean var7 = false;
                    OutputStreamWriter var8 = null;

                    while(true) {
                        String var6;
                        while((var6 = var4.readLine()) != null) {
                            if (var6.trim().length() > 0 && var6.startsWith(var2)) {
                                String var9 = var6.substring(var2.length());
                                String var10 = var1.getParentFile().getAbsolutePath();
                                var9 = var10 + File.separator + var9;
                                a.info("[generate]\t split file:" + var1.getAbsolutePath() + " ==> " + var9);
                                var8 = new OutputStreamWriter(new FileOutputStream(var9), "UTF-8");
                                var5.add(var8);
                                this.d.add("生成成功：" + var9);
                                var7 = true;
                            } else if (var7) {
                                var8.append(var6 + "\r\n");
                            }
                        }

                        for(int var29 = 0; var29 < var5.size(); ++var29) {
                            ((Writer)var5.get(var29)).close();
                        }

                        var4.close();
                        var3.close();
                        a.debug("[generate]\t delete file:" + var1.getAbsolutePath());
                        b(var1);
                        var20 = false;
                        break label341;
                    }
                } catch (FileNotFoundException var25) {
                    var25.printStackTrace();
                    var20 = false;
                    break label342;
                } catch (IOException var26) {
                    var26.printStackTrace();
                    var20 = false;
                } finally {
                    if (var20) {
                        try {
                            if (var4 != null) {
                                var4.close();
                            }

                            if (var3 != null) {
                                var3.close();
                            }

                            if (var5.size() > 0) {
                                for(int var12 = 0; var12 < var5.size(); ++var12) {
                                    if (var5.get(var12) != null) {
                                        ((Writer)var5.get(var12)).close();
                                    }
                                }
                            }
                        } catch (IOException var21) {
                            var21.printStackTrace();
                        }

                    }
                }

                try {
                    if (var4 != null) {
                        var4.close();
                    }

                    if (var3 != null) {
                        var3.close();
                    }

                    if (var5.size() > 0) {
                        for(var28 = 0; var28 < var5.size(); ++var28) {
                            if (var5.get(var28) != null) {
                                ((Writer)var5.get(var28)).close();
                            }
                        }
                    }
                } catch (IOException var22) {
                    var22.printStackTrace();
                }

                return;
            }

            try {
                if (var4 != null) {
                    var4.close();
                }

                if (var3 != null) {
                    var3.close();
                }

                if (var5.size() > 0) {
                    for(var28 = 0; var28 < var5.size(); ++var28) {
                        if (var5.get(var28) != null) {
                            ((Writer)var5.get(var28)).close();
                        }
                    }
                }
            } catch (IOException var23) {
                var23.printStackTrace();
            }

            return;
        }

        try {
            if (var4 != null) {
                var4.close();
            }

            if (var3 != null) {
                var3.close();
            }

            if (var5.size() > 0) {
                for(var28 = 0; var28 < var5.size(); ++var28) {
                    if (var5.get(var28) != null) {
                        ((Writer)var5.get(var28)).close();
                    }
                }
            }
        } catch (IOException var24) {
            var24.printStackTrace();
        }

    }

    protected static String a(Map<String, Object> var0, String var1, GenUtilsEx var2) throws Exception {
        String var3 = var1;
        boolean var4 = true;
        int var9;
        if ((var9 = var1.indexOf(64)) != -1) {
            var3 = var1.substring(0, var9);
            String var5 = var1.substring(var9 + 1);
            Object var6 = var0.get(var5);
            if (var6 == null) {
                System.err.println("[not-generate] WARN: test expression is null by key:[" + var5 + "] on template:[" + var1 + "]");
                return null;
            }

            if (!"true".equals(String.valueOf(var6))) {
                a.error("[not-generate]\t test expression '@" + var5 + "' is false,template:" + var1);
                return null;
            }
        }

        Configuration var10 = bUtils.a(var2.b(), c, "/");
        var3 = bUtils.a(var3, var0, var10);
        String var11 = var2.a();
        if (var11 != null && var11 != "") {
            var3 = var3.substring(var11.length() + 1);
        }

        String var7 = var3.substring(var3.lastIndexOf("."));
        String var8 = var3.substring(0, var3.lastIndexOf(".")).replace(".", File.separator);
        var3 = var8 + var7;
        return var3;
    }

    protected static boolean b(File var0) {
        boolean var1 = false;

        for(int var2 = 0; !var1 && var2++ < 10; var1 = var0.delete()) {
            System.gc();
        }

        return var1;
    }

    protected static String a(String var0, String var1) {
        boolean var2 = true;
        boolean var3 = true;

        do {
            int var4 = var0.indexOf(var1) == 0 ? 1 : 0;
            int var5 = var0.lastIndexOf(var1) + 1 == var0.length() ? var0.lastIndexOf(var1) : var0.length();
            var0 = var0.substring(var4, var5);
            var2 = var0.indexOf(var1) == 0;
            var3 = var0.lastIndexOf(var1) + 1 == var0.length();
        } while(var2 || var3);

        return var0;
    }
}
