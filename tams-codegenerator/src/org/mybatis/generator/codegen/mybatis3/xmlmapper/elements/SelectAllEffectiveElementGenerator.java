/*
 * 文件名：SelectAllEffectiveElementGenerator.java 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All
 * Rights Reserved. 描述：中信银行资管系统 修改人：鏉滀繚鍐? 修改时间：2016年11月28日 修改内容：新建 系统名称：中信银行资管系统
 */

package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author 杜保军
 * @version 1.0 2016年11月28日
 * @see SelectAllEffectiveElementGenerator
 * @since 1.0
 */
public class SelectAllEffectiveElementGenerator extends AbstractXmlElementGenerator {

    private final boolean status;
    
    public SelectAllEffectiveElementGenerator(boolean status) {
        super();
        this.status = status;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("sql"); //$NON-NLS-1$

        if(status){
        answer.addAttribute(new Attribute(
                "id", "Sql_All_Effective_Data")); //$NON-NLS-1$
        } else {
            answer.addAttribute(new Attribute(
                "id", "Sql_All_Data")); //$NON-NLS-1$
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select "); //$NON-NLS-1$

        if (stringHasValue(introspectedTable
                .getSelectByPrimaryKeyQueryId())) {
            sb.append('\'');
            sb.append(introspectedTable.getSelectByPrimaryKeyQueryId());
            sb.append("' as QUERYID,"); //$NON-NLS-1$
        }
        answer.addElement(new TextElement(sb.toString()));
        answer.addElement(getBaseColumnListElement());
        if (introspectedTable.hasBLOBColumns()) {
            answer.addElement(new TextElement(",")); //$NON-NLS-1$
            answer.addElement(getBlobColumnListElement());
        }

        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        sb.append(tableName);
        answer.addElement(new TextElement(sb.toString()));
        IntrospectedColumn introspectedColumn = introspectedTable.getColumn("STATUS");
        if(introspectedColumn != null && status){
            sb.setLength(0);
            sb.append("where STATUS = \'E\'"); //$NON-NLS-1$
            answer.addElement(new TextElement(sb.toString()));
        }
        if (context.getPlugins()
                .sqlMapSelectByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

}
