package com.src.common.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 修改命名空间
 * 去除 myabtis generator生成的注释
 * Created by src on 2018/6/25.
 */
public class CommentGenerator extends DefaultCommentGenerator {


    private Properties myPoperties = new Properties();

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        //本地保存一份properties
        this.myPoperties.putAll(properties);
    }

    //生成File 自定义的注释
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
//        if (introspectedColumn.getRemarks() != null && !"".equals(introspectedColumn.getRemarks())) {
//            field.addJavaDocLine("/**");
//            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
//            addJavadocTag(field, false);
//            field.addJavaDocLine(" */");
//        }

        if (introspectedColumn.getRemarks() != null && !"".equals(introspectedColumn.getRemarks())) {
            StringBuilder sb = new StringBuilder();

            field.addJavaDocLine("/**");
            sb.append(" * ");
            sb.append(introspectedColumn.getRemarks());
            field.addJavaDocLine(sb.toString());

            // addJavadocTag(field, false);

            field.addJavaDocLine(" */");
        }
    }


    //将 namespace修改掉
    @Override
    public void addRootComment(XmlElement rootElement) {
        super.addRootComment(rootElement);
        Object generateExt = myPoperties.get("generateExt");
        if (null == generateExt || !generateExt.toString().equals("true")) {
            return;
        }
        List<Attribute> lists = rootElement.getAttributes();
        int delIndex = -1;
        String orginNameSpace = "";
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).getName().equals("namespace")) {
                orginNameSpace = lists.get(i).getValue();
                if (orginNameSpace.endsWith("Ext")) break;
                delIndex = i;
                break;
            }
        }
        if (delIndex != -1) {
            lists.remove(delIndex);
            rootElement.getAttributes().add(new Attribute("namespace", orginNameSpace + "Ext"));
        }
    }

    //曾加 生成的java文件 注释
    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        /*super.addJavaFileComment(compilationUnit);*/
        //如果没有选择生成扩展类 则给JavaModel 加上 @Resource注解
        String generateExt = myPoperties.getProperty("generateExt");
        if (generateExt == null || !generateExt.equals("true")) {

            //生成的是 JavaModel 和 JavaModelExample 文件
            if (compilationUnit instanceof TopLevelClass) {
//                TopLevelClass topLevelClass = (TopLevelClass) compilationUnit;
//                String shortName = compilationUnit.getType().getShortName();
//                topLevelClass.addAnnotation("@Data");
//                topLevelClass.addImportedType("lombok.Data");
            }

            //生成的是Mapper.java 文件
            if (compilationUnit instanceof Interface) {
                Interface anInterface = (Interface) compilationUnit;
                //下面的可以给JavaFile 添加注释
                //topLevelClass.addFileCommentLine("/**generator by Shirc generator common.....**/");
                String shortName = compilationUnit.getType().getShortName();
                if (shortName == null || !shortName.endsWith("Mapper")) return;
                //只给JavaModel添加注解就行了，Example不需要
                anInterface.addAnnotation("@Resource");
                anInterface.addImportedType(new FullyQualifiedJavaType("javax.annotation.Resource"));
            }
        }

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        //生成的方法 可以添加注释
        super.addGeneralMethodComment(method, introspectedTable);
    }
//
//    @Override
//    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        StringBuilder sb = new StringBuilder();
//
//        topLevelClass.addJavaDocLine("/**");
//        sb.append(" * ");
//        sb.append(introspectedTable.getFullyQualifiedTable());
//        topLevelClass.addJavaDocLine(sb.toString());
//
//        sb.setLength(0);
//        sb.append(" * @author ");
//        sb.append("generator");
//        sb.append(" ");
//        topLevelClass.addJavaDocLine(" */");
//    }
}
