/*
 * Copyright 2009 The Apache Software Foundation Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * @author Jeff Butler
 */
public class InsertSelectElementGenerator extends AbstractXmlElementGenerator {

    private boolean isSimple;

    public InsertSelectElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
        XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        if (tableName.endsWith("_HIS")) {
            answer.addAttribute(new Attribute("id", "insertHisByBasicInfo")); //$NON-NLS-1$
        }
        else {
            answer.addAttribute(new Attribute("id", "insertBasicByBasicInfo")); //$NON-NLS-1$
        }

        FullyQualifiedJavaType parameterType;
        if (isSimple) {
            parameterType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        }
        else {
            parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        }

        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
            parameterType.getFullyQualifiedName()));

        context.getCommentGenerator().addComment(answer);

        StringBuilder insertClause = new StringBuilder();
        StringBuilder valuesClause = new StringBuilder();
        insertClause.append("insert into "); //$NON-NLS-1$
        insertClause.append(tableName);
        insertClause.append("(");
        String fromTableName = tableName.endsWith("_HIS") ? tableName.substring(0,
            tableName.indexOf("_HIS")) : tableName;
        List<IntrospectedColumn> keyList = introspectedTable.getPrimaryKeyColumns();
      
        String where = " where 1 = 1";
        //判断表结构是否有status字段如果存在则加这个where，否则不加
        List<IntrospectedColumn> list = introspectedTable.getAllColumns();
        for (IntrospectedColumn in : list) {
            if (in.getActualColumnName().equals("STATUS")) {
                where = " where STATUS = 'E'";
            }
        }
        
        for (IntrospectedColumn introspectedColumn : keyList) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(
                introspectedColumn);
            if ("HIS_NO".equals(columnName) || "STATUS".equals(columnName)) {
                continue;
            }
            where = where + " and " + columnName + " = "
                    + MyBatis3FormattingUtilities.getParameterClause(introspectedColumn);
        }
        valuesClause.append("select "); //$NON-NLS-1$

        List<String> valuesClauses = new ArrayList<String>();
        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns().iterator();
        while (iter.hasNext()) {
            IntrospectedColumn introspectedColumn = iter.next();
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(
                introspectedColumn);
            if ("HIS_NO".equals(columnName)) {
                valuesClause.append("(select nvl(max(HIS_NO), 0) from ");
                valuesClause.append(tableName);
                valuesClause.append(where);
                valuesClause.append(") + 1 HIS_NO");
            }
            else if ("STATUS".equals(columnName) && !tableName.endsWith("_HIS")) {
                valuesClause.append("#{status,jdbcType=VARCHAR} STATUS");
            }
            else if ("WORKFLOW_ID".equals(columnName) && !tableName.endsWith("_HIS")) {
                valuesClause.append("#{workflowId,jdbcType=VARCHAR} WORKFLOW_ID");
            }
            else if ("UPDATE_USER".equals(columnName)) {
                valuesClause.append("#{updateUser,jdbcType=VARCHAR} UPDATE_USER");
            }
            else if ("UPDATE_TIME".equals(columnName)) {
                valuesClause.append("#{updateTime,jdbcType=TIMESTAMP} UPDATE_TIME");
            }
            else {
                valuesClause.append(columnName);
            }
            insertClause.append(columnName);
            if (iter.hasNext()) {
                valuesClause.append(", "); //$NON-NLS-1$
                insertClause.append(", "); //$NON-NLS-1$
            }
            if(insertClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                OutputUtilities.xmlIndent(insertClause, 1);
                insertClause.setLength(0);
                valuesClauses.add(valuesClause.toString());
                valuesClause.setLength(0);
                OutputUtilities.xmlIndent(valuesClause, 1);
            }
        }
        answer.addElement(new TextElement(insertClause.toString() + ")"));
        valuesClauses.add(valuesClause.toString());
        valuesClause.setLength(0);
        OutputUtilities.xmlIndent(valuesClause, 1);
        valuesClause.append(" from ");
        valuesClause.append(fromTableName);
        valuesClauses.add(valuesClause.toString());
        valuesClause.setLength(0);
        OutputUtilities.xmlIndent(valuesClause, 1);
        valuesClause.append(where);
        valuesClauses.add(valuesClause.toString());

        for (String clause : valuesClauses) {
            answer.addElement(new TextElement(clause));
        }

        parentElement.addElement(answer);
    }
}
