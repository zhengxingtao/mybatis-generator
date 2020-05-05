package com.src.common.util;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.src.common.util.StringUtil.lineToHump;


/**
 * 代码生成类
 *
 * @author src
 * @date 2018/6/25
 */
public class MybatisGeneratorUtil {

    // generatorConfig模板路径
    private static String generatorConfig_vm = "/template/generatorConfig.vm";
    // Facade模板路径
    private static String facade_vm = "/template/Facade.vm";
    // ServiceMock模板路径
    private static String serviceMock_vm = "/template/ServiceMock.vm";
    // FacadeImpl模板路径
    private static String facadeImpl_vm = "/template/FacadeImpl.vm";

    //MapperExtJava 模板路径
    private static String mapperExtJava_vm = "/template/MapperExtjava.vm";
    //MapperExtXml 模板路径
    private static String mapperExtXml_vm = "/template/MapperExtXml.vm";

    //BaseMapper.vm 模板路径
    private static String baseMapper_vm = "/template/BaseMapper.vm";
    //BaseRepository.vm
    private static String baseRepository_vm = "/template/BaseRepository.vm";
    //Repository 模板路径
    private static String repository_vm = "/template/Repository.vm";

    /**
     * 根据模板生成generatorConfig.xml文件
     *
     * @param jdbcDriver   驱动路径
     * @param jdbcUrl      链接
     * @param jdbcUsername 帐号
     * @param jdbcPassword 密码
     * @param database     数据库
     * @param tablePrefix  表前缀 特别要注意 重新生成的时候会根据这个前缀去删除所有之前生成的文件
     */
    public static void generator(
            String rootClass,//是否需要继承某个类，此值是全路径类名
            boolean generateFacade,//是否生成Facade类
            String targetProjectDao,//当前执行此方法的模块用于确认base-path 例如 testgenerator/testgenerator-dao
            String targetProjectModel,//model生成路径
            String targetProjectSql,//sqlmap存放 目标项目路径 例如 testgenerator/testgenerator-rpc-service/src/main/resources/
            String targetProjectRpcApi,// rpc 接口项目模块  例如 testgenerator/testgenerator-rpc-facade
            String targetProjectRpcService,// rpc 接口 实现 项目模块  例如 testgenerator/testgenerator-rpc-service
            String targetRepository,//Repository 存放在哪个项目
            String modelPack,  //model包 例如： com.test.dao.model
            String mapperPack, //mapper文件包  com.test.dao.mapper
            String repositoryPack,//repository文件包  例如：com.test.dao.repository
            String sqlmapperPack, //mapper.xml文件包  mapper
            String rpcPack,//rpc 对外接口层的包
            String rpcServerPack,//rpc  实现层
            String jdbcDriver,
            String jdbcUrl,
            String jdbcUsername,
            String jdbcPassword,
            String database,//数据库
            String tablePrefix,//表前缀，主要是用来匹配需要生成那些表数据
            Map<String, String> lastInsertIdTables,//需要insert后返回主键的表配置，key:表名,value:主键名
            String author) throws Exception {
        if (StringUtils.isEmpty(targetProjectDao) || StringUtils.isEmpty(targetProjectSql) || StringUtils.isEmpty(targetProjectRpcApi)
                || StringUtils.isEmpty(targetProjectRpcService) || StringUtils.isEmpty(modelPack)) {
            throw new Exception("缺少参数！");
        }
        boolean generateExt = true;//是否需要生成 扩展Mapper.xml 和 Mapper.java

        String os = System.getProperty("os.name");
        String basePath = MybatisGeneratorUtil.class.getResource("/").getPath().replace(targetProjectDao, "");
        System.out.println("basePath" + basePath);
        if (os.toLowerCase().startsWith("win")) {
            generatorConfig_vm = MybatisGeneratorUtil.class.getResource(generatorConfig_vm).getPath().replaceFirst("/", "");
            facade_vm = MybatisGeneratorUtil.class.getResource(facade_vm).getPath().replaceFirst("/", "");
            serviceMock_vm = MybatisGeneratorUtil.class.getResource(serviceMock_vm).getPath().replaceFirst("/", "");
            facadeImpl_vm = MybatisGeneratorUtil.class.getResource(facadeImpl_vm).getPath().replaceFirst("/", "");
            mapperExtJava_vm = MybatisGeneratorUtil.class.getResource(mapperExtJava_vm).getPath().replaceFirst("/", "");
            mapperExtXml_vm = MybatisGeneratorUtil.class.getResource(mapperExtXml_vm).getPath().replaceFirst("/", "");

            baseMapper_vm = MybatisGeneratorUtil.class.getResource(baseMapper_vm).getPath().replaceFirst("/", "");
            baseRepository_vm = MybatisGeneratorUtil.class.getResource(baseRepository_vm).getPath().replaceFirst("/", "");
            repository_vm = MybatisGeneratorUtil.class.getResource(repository_vm).getPath().replaceFirst("/", "");


            basePath = basePath.replaceFirst("/", "");
        } else {
            generatorConfig_vm = MybatisGeneratorUtil.class.getResource(generatorConfig_vm).getPath();
            facade_vm = MybatisGeneratorUtil.class.getResource(facade_vm).getPath();
            serviceMock_vm = MybatisGeneratorUtil.class.getResource(serviceMock_vm).getPath();
            facadeImpl_vm = MybatisGeneratorUtil.class.getResource(facadeImpl_vm).getPath();
            mapperExtJava_vm = MybatisGeneratorUtil.class.getResource(mapperExtJava_vm).getPath();
            mapperExtXml_vm = MybatisGeneratorUtil.class.getResource(mapperExtXml_vm).getPath();
            baseMapper_vm = MybatisGeneratorUtil.class.getResource(baseMapper_vm).getPath().replaceFirst("/", "");
            baseRepository_vm = MybatisGeneratorUtil.class.getResource(baseRepository_vm).getPath().replaceFirst("/", "");
            repository_vm = MybatisGeneratorUtil.class.getResource(repository_vm).getPath().replaceFirst("/", "");

        }
        String ctime = new SimpleDateFormat("yyyy/M/d").format(new Date());
        String generatorConfigXml = MybatisGeneratorUtil.class.getResource("/").getPath().replace("/target/classes/", "") + "/src/main/resources/generatorConfig.xml";
//        targetProjectDao = basePath + targetProjectDao;
        targetRepository = basePath + targetRepository;
        String sql = "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = '" + database + "' AND table_name LIKE '" + tablePrefix + "_%';";

        System.out.println("========== 开始生成generatorConfig.xml文件 ==========");
        List<Map<String, Object>> tables = new ArrayList<>();
        String targetProjectSqlMap = targetProjectSql;
        try {
            VelocityContext context = new VelocityContext();
            Map<String, Object> table;

            // 查询定制前缀项目的所有表
            JdbcUtil jdbcUtil = new JdbcUtil(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);

            List<Map> result = jdbcUtil.selectByParams(sql, null);
            if (result == null || result.size() == 0) {
                System.out.println("========== 未找到匹配到的数据表,文件生成结束 ==========");
                return;
            }
            for (Map map : result) {
                System.out.println(map.get("TABLE_NAME"));
                table = new HashMap<>(2);
                table.put("table_name", map.get("TABLE_NAME"));
                table.put("model_name", lineToHump(ObjectUtils.toString(map.get("TABLE_NAME"))));
                tables.add(table);
            }
            jdbcUtil.release();


            context.put("tables", tables);
            context.put("generator_javaModelGenerator_targetPackage", modelPack);
            context.put("generator_javaMapperGenerator_targetPackage", mapperPack);
            context.put("generator_sqlMapperGenerator_targetPackage", sqlmapperPack);
            context.put("generator_javaClientGenerator_targetPackage", mapperPack);
            context.put("targetProject", targetProjectDao);
            context.put("targetProjectModel", targetProjectModel);
            context.put("targetProject_sqlMap", targetProjectSqlMap);
            context.put("generator_jdbc_password", jdbcPassword);
            context.put("last_insert_id_tables", lastInsertIdTables);
            context.put("author", author);
            context.put("generateExt", generateExt + "");
            context.put("ctime", ctime);
            context.put("rootClass", rootClass);
            context.put("driverClass", jdbcDriver);
            context.put("connectionURL", jdbcUrl);
            context.put("userId", jdbcUsername);
            context.put("password", jdbcPassword);


            VelocityUtil.generateJar(generatorConfig_vm, generatorConfigXml, context);

            /*VelocityUtil.generate(generatorConfig_vm, generatorConfigXml, context);
             */
            deleteDir(new File(targetProjectDao + "/src/main/java/" + modelPack.replaceAll("\\.", "/")), tablePrefix);
            deleteDir(new File(targetProjectDao + "/src/main/java/" + mapperPack.replaceAll("\\.", "/")), tablePrefix);
            deleteDir(new File(targetProjectSqlMap + "/src/main/java/" + mapperPack.replaceAll("\\.", "/")), tablePrefix);
            deleteDir(new File(targetProjectSqlMap + sqlmapperPack.replaceAll("\\.", "/")), tablePrefix);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("========== 结束生成generatorConfig.xml文件 ==========");

        System.out.println("========== 开始运行MybatisGenerator ==========");
        List<String> warnings = new ArrayList<>();
        File configFile = new File(generatorConfigXml);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);

        //Mybtais 生成器生成
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);


        if (generateExt) {
            String mapperjavaExtPath = targetProjectDao + "/src/main/java/" + mapperPack.replaceAll("\\.", "/");
            String mapperxmlExtPath = targetProjectSqlMap + sqlmapperPack.replaceAll("\\.", "/");
            String baseMapperJava = mapperjavaExtPath + "/base/BaseMapper.java";

            // 生成BaseMapperJava
            File baseMapperJavaFile = new File(baseMapperJava);
            if (!baseMapperJavaFile.exists()) {
                VelocityContext context = new VelocityContext();
                context.put("mapperPack", mapperPack);
                context.put("ctime", ctime);
                context.put("author", author);
                VelocityUtil.generateJar(baseMapper_vm, baseMapperJava, context);
/*
					VelocityUtil.generate(mapperExtJava_vm, mapperExtJava, context);
*/
                System.out.println(baseMapper_vm);
            }

            for (int i = 0; i < tables.size(); i++) {
                String model = lineToHump(ObjectUtils.toString(tables.get(i).get("table_name")));
                String mapperExtJava = mapperjavaExtPath + "/" + model + "MapperExt.java";
                String mapperExtXml = mapperxmlExtPath + "/" + model + "MapperExt.xml";

                // 生成MapperjavaExt
                File mapperExtFile = new File(mapperExtJava);
                if (!mapperExtFile.exists()) {
                    VelocityContext context = new VelocityContext();
                    context.put("mapperPack", mapperPack);
                    context.put("model", model);
                    context.put("modelPack", modelPack);
                    context.put("ctime", ctime);
                    context.put("author", author);
                    VelocityUtil.generateJar(mapperExtJava_vm, mapperExtJava, context);
/*
					VelocityUtil.generate(mapperExtJava_vm, mapperExtJava, context);
*/
                    System.out.println(mapperExtJava_vm);
                }
                //生成 MapperExt.xml
                File mapperExtXmlFile = new File(mapperExtXml);
                if (!mapperExtXmlFile.exists()) {
                    VelocityContext context = new VelocityContext();
                    context.put("namespace", mapperPack + "." + model + "MapperExt");
                    context.put("ctime", ctime);
                    context.put("author", author);
                    VelocityUtil.generateJar(mapperExtXml_vm, mapperExtXml, context);
/*
					VelocityUtil.generate(mapperExtXml_vm, mapperExtXml, context);
*/
                    System.out.println(mapperExtXml_vm);
                }
            }
        }

        for (String warning : warnings) {
            System.out.println(warning);
        }
        System.out.println("========== 结束运行MybatisGenerator ==========");

        System.out.println("========== 开始生成Service ==========");

        String servicePath = basePath + targetProjectRpcApi + "/src/main/java/" + rpcPack.replaceAll("\\.", "/");
        String serviceImplPath = basePath + targetProjectRpcService + "/src/main/java/" + rpcServerPack.replaceAll("\\.", "/");

        if (generateFacade) {
            for (int i = 0; i < tables.size(); i++) {
                String model = lineToHump(ObjectUtils.toString(tables.get(i).get("table_name")));
                String service = servicePath + "/" + model + "Service.java";
                String serviceMock = servicePath + "/" + model + "FacadeMock.java";
                String serviceImpl = serviceImplPath + "/" + model + "ServiceImpl.java";
                // 生成service
                File serviceFile = new File(service);
                if (!serviceFile.exists()) {
                    VelocityContext context = new VelocityContext();
                    context.put("repositoryPack", repositoryPack);
                    context.put("modelPack", modelPack);
                    context.put("rpcPack", rpcPack);
                    context.put("model", model);
                    context.put("ctime", ctime);
                    context.put("author", author);
                    VelocityUtil.generateJar(facade_vm, service, context);
/*
				VelocityUtil.generate(facade_vm, service, context);
*/
                    System.out.println(service);
                }
                if (false) {//删除
                    // 生成serviceMock
                    File serviceMockFile = new File(serviceMock);
                    if (!serviceMockFile.exists()) {
                        VelocityContext context = new VelocityContext();
                        context.put("modelPack", modelPack);
                        context.put("mapperPack", mapperPack);
                        context.put("rpcPack", rpcPack);
                        context.put("model", model);
                        context.put("ctime", ctime);
                        context.put("author", author);
                        VelocityUtil.generateJar(serviceMock_vm, serviceMock, context);
                        //VelocityUtil.generate(serviceMock_vm, serviceMock, context);
                        System.out.println(serviceMock);
                    }
                }

                // 生成FacadeImpl
                File serviceImplFile = new File(serviceImpl);
                if (!serviceImplFile.exists()) {
                    VelocityContext context = new VelocityContext();
                    context.put("modelPack", modelPack);
                    context.put("mapperPack", mapperPack);
                    context.put("rpcServerPack", rpcServerPack);
                    context.put("repositoryPack", repositoryPack);
                    context.put("rpcPack", rpcPack);
                    context.put("model", model);
                    context.put("mapper", StringUtil.toLowerCaseFirstOne(model));
                    context.put("ctime", ctime);
                    context.put("author", author);
                    context.put("generateExt", generateExt + "");
                    VelocityUtil.generateJar(facadeImpl_vm, serviceImpl, context);
/*
				VelocityUtil.generate(facadeImpl_vm, serviceImpl, context);
*/
                    System.out.println(serviceImpl);
                }
            }
            System.out.println("========== 结束生成Service ==========");
        }
    }


    // 递归删除非空文件夹
    public static void deleteDir(File dir, String tablePrefix) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDir(files[i], tablePrefix);
            }
        }
        //只删除匹配到的&&不删除扩展文件  Ext结尾的
        if (dir.getName().startsWith(delTablePrefix(tablePrefix)) &&
                !endWithExt(dir.getName())) {
            dir.delete();
        }
    }

    //
    private static String delTablePrefix(String tablePrefix) {
        String s = lineToHump(tablePrefix);
        if (s.endsWith("_")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static boolean endWithExt(String fileName) {
        String s = fileName.substring(0, fileName.lastIndexOf("."));
        return s.endsWith("Ext");
    }

}
