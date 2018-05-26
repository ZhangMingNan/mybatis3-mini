package com.ly.zmn48644.scripting.xmltags;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by zhangmingnan on 2018/5/13.
 */
public class TrimSqlNode implements SqlNode {


    private String prefix;

    private List<String> prefixsToOverride;

    private String suffix;

    private List<String> suffixsToOverride;

    private SqlNode contents;

    public TrimSqlNode(String prefix, List<String> prefixsToOverride, String suffix, List<String> suffixsToOverride, SqlNode contents) {
        this.prefix = prefix;
        this.prefixsToOverride = prefixsToOverride;
        this.suffix = suffix;
        this.suffixsToOverride = suffixsToOverride;
        this.contents = contents;
    }

    @Override
    public void apply(DynamicContext context) {
        FilteredDynamicContext filteredDynamicContext = new FilteredDynamicContext(context);
        contents.apply(filteredDynamicContext);
        filteredDynamicContext.applyAll();
    }


    private class FilteredDynamicContext extends DynamicContext {
        public FilteredDynamicContext(DynamicContext delegate) {
            this.delegate = delegate;
            this.sqlBuffer = new StringBuilder();
        }

        private DynamicContext delegate;

        private StringBuilder sqlBuffer;

        @Override
        public void bind(String name, Object value) {
            this.delegate.bind(name, value);

        }

        @Override
        public Map<String, Object> getBindings() {
            return delegate.getBindings();
        }

        @Override
        public String getSql() {
            return delegate.getSql();
        }

        @Override
        public void appendSql(String sql) {
            sqlBuffer.append(sql);
        }

        public void applyAll() {
            String trimmedUppercaseSql = sqlBuffer.toString().toUpperCase(Locale.ENGLISH);
            if (trimmedUppercaseSql != null && trimmedUppercaseSql.length() != 0) {
                applyPrefix(sqlBuffer, trimmedUppercaseSql);
                applySuffix(sqlBuffer, trimmedUppercaseSql);
                delegate.appendSql(sqlBuffer.toString());
            }

        }

        private void applySuffix(StringBuilder sql, String trimmedUppercaseSql) {
            if (suffixsToOverride != null) {
                for (String toRemove : suffixsToOverride) {
                    if (trimmedUppercaseSql.endsWith(toRemove)) {
                        sql.delete(sql.length() - toRemove.trim().length(), sql.length());
                        break;
                    }
                }
                if (suffix != null) {
                    sql.insert(0, " ");
                    sql.insert(0, suffix);
                }

            }
        }

        private void applyPrefix(StringBuilder sql, String trimmedUppercaseSql) {
            if (prefixsToOverride != null) {
                for (String toRemove : prefixsToOverride) {
                    if (trimmedUppercaseSql.startsWith(toRemove)) {
                        sql.delete(0, toRemove.trim().length());
                        break;
                    }
                }
                if (prefix != null) {
                    sql.insert(0, " ");
                    sql.insert(0, prefix);
                }
            }
        }
    }
}













