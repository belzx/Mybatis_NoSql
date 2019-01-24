package com.lizhi.orm.param;


import com.lizhi.orm.term.Term;
import com.lizhi.util.CommonAssert;
import com.lizhi.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author https://github.com/lizhixiong1994
 * @CreateTime 2019-01-23
 *
 * 使用于联表查询的参数
 * 如何使用：
 *  QueryJoinParam bt = QueryJoinParam.build("tb");      // 创建QueryJoinParam 对象,tb为tableName的缩写
 *                       bt.includes("id");              //主表的结果只保存id字段
 *                       bt.join("t_label", "tbl")       //联表 t_blog_label 别名为 tbl
 *                           .on("lable_id", "id")       // 联表的关键字
 *                           .joinCludes("name", "labelName")   //增加联表的返回字段
 *                           .group("name")              // group by tbl.name
 *  最后执行结果如下：
 *  select
 *      tb.id,
 *      tbl.name as labelName
 *  from t_blog tb
 *  join t_label tbl on tb.lable_id = tb.id
 *  where
 *  group by tbl.name
 *
 */
public class QueryJoinParam extends QueryParam {

    private static final long serialVersionUID = 2642815643884622464L;

    //当前的选择的表
    private JoinOn currentJoinOn;

    //主表
    private JoinOn masterJoinOn;

    //不知道描述
    private List<String> joinCludes;

    private List<JoinOn> joinOnList;

    private QueryJoinParam(String tableALias) {
        currentJoinOn = new JoinOn("", tableALias, null);
        masterJoinOn = new JoinOn("", tableALias, null);
    }

    public static QueryJoinParam build(String tableALias) {
        CommonAssert.notNullOrEmpty(tableALias, "tableALias can not be null or empty");
        return new QueryJoinParam(tableALias);
    }

    public List<String> getJoinCludes() {
        return joinCludes;
    }


    public JoinOn getMasterJoinOn() {
        return masterJoinOn;
    }

    public String buildJoinOnField() {
        StringBuilder fields = new StringBuilder();
        fields.append(getMasterTableAlias());
        if (joinOnList != null) {
            for (JoinOn joinOn : joinOnList) {
                fields.append(" join " + joinOn.getJoinTable() + " " + joinOn.getJoinTableAlias() + " on " + getMasterTableAlias() + "." + joinOn.getMasterTableColum() + "=" + joinOn.getJoinTableAlias() + "." + joinOn.getJoinTableColum() + "\n");
            }
        }
        return fields.toString();
    }

    public QueryJoinParam joinCludes(String... column) {
        for (String str : column) {
            joinCludes(packageColumn(str), null);
        }
        return this;
    }

    public QueryJoinParam joinCludes(String column, String returnAlias) {
        if (joinCludes == null) {
            joinCludes = new ArrayList<>();
        }
        joinCludes.add(packageColumn(column, returnAlias));
        return this;
    }


    /**
     *
     * @param joinTable      联表的表名
     * @param joinTableAlias 联表的别名,默认值为表名
     * @return
     */
    public JoinOn join(String joinTable, String joinTableAlias) {
        return new JoinOn(joinTable, (StringUtils.isNullOrEmpty(joinTableAlias)?joinTable:joinTableAlias), this);
    }

    public QueryJoinParam setCurrentJoinOn(int index) {
        JoinOn joinOn = joinOnList.get(index);
        CommonAssert.notNull(joinOn, "index = [" + index + "] subQueryJoinParam not exists in QueryJoinParam");
        this.currentJoinOn = joinOn;
        return this;
    }

    public String getMasterTableAlias() {
        return getMasterJoinOn().getJoinTableAlias();
    }

    private void addJoinOn(JoinOn joinOn) {
        if (this.joinOnList == null) {
            this.joinOnList = new ArrayList<>();
        }
        joinOnList.add(joinOn);
    }

    /**
     * 重写addWhere
     * @param term
     */
    @Override
    public void addWhere(Term term) {
        term.setColumn(packageColumn(term.getColumn()));
        super.addWhere(term);
    }

    @Override
    public <T extends QueryParam> T sortDesc(String column) {
        return super.sortDesc(packageColumn(column));
    }

    @Override
    public <T extends QueryParam> T sortAsc(String column) {
        return super.sortAsc(packageColumn(column));
    }

    @Override
    public <T extends QueryParam> T group(String column) {
        return super.group(packageColumn(column));
    }

    /**
     * 修饰where参数名称
     * 使得 where column = 1 变成 where tableAlias.column = 1
     * @param column
     * @return
     */
    private String packageColumn(String column) {
        return this.currentJoinOn.joinTableAlias + "." + column;
    }

    private String packageColumn(String column, String returnAlias) {
        return packageColumn(column) + " as " + returnAlias;
    }

    /**
     * 联表的信息
     */
    public class JoinOn {

        /**表名*/
        private String joinTable;

        /**别名*/
        private  String joinTableAlias;


        private String masterTableColum;

        private String joinTableColum;

        /**
         * masterTable的param
         */
        private QueryJoinParam queryJoinParam;

        public JoinOn(String joinTable, String joinTableAlias, QueryJoinParam queryJoinParam) {
            this.queryJoinParam = queryJoinParam;
            this.joinTable = joinTable;
            this.joinTableAlias = joinTableAlias;
        }

        /**
         * 如下
         * where  masterTable masterTableAlias
         * join   joinTable   joinTableAlias    on masterTableAlias.masterTableColum = joinTableAlias.joinTableColum
         *
         * @param masterTableColum 联表的关键字段 不能为空
         * @param joinTableColum   联表的关键字段 不能为空
         *
         */
        public QueryJoinParam on(String masterTableColum, String joinTableColum) {
            this.masterTableColum = masterTableColum;
            this.joinTableColum = joinTableColum;
            this.queryJoinParam.addJoinOn(this);
            this.queryJoinParam.currentJoinOn = this;
            return this.queryJoinParam;
        }

        public String getJoinTableAlias() {
            return joinTableAlias;
        }

        public String getJoinTable() {
            return joinTable;
        }

        public String getMasterTableColum() {
            return masterTableColum;
        }

        public String getJoinTableColum() {
            return joinTableColum;
        }
    }
}
