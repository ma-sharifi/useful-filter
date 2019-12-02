package com.example.panache.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 26/11/2019
 */
public class Sort {
    private List<Column> columns = new ArrayList();

    private Sort() {
    }

    public static Sort by(String column) {
        return (new Sort()).and(column);
    }

    public static Sort by(String column, Sort.Direction direction) {
        return (new Sort()).and(column, direction);
    }

    public static Sort by(String... columns) {
        Sort sort = new Sort();
        String[] var2 = columns;
        int var3 = columns.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String column = var2[var4];
            sort.and(column);
        }

        return sort;
    }

    public static Sort ascending(String... columns) {
        return by(columns);
    }

    public static Sort descending(String... columns) {
        Sort sort = new Sort();
        String[] var2 = columns;
        int var3 = columns.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String column = var2[var4];
            sort.and(column, Sort.Direction.Descending);
        }

        return sort;
    }

    public Sort descending() {
        return this.direction(Sort.Direction.Descending);
    }

    public Sort ascending() {
        return this.direction(Sort.Direction.Ascending);
    }

    public Sort direction(Sort.Direction direction) {
        Iterator var2 = this.columns.iterator();

        while(var2.hasNext()) {
            Sort.Column column = (Sort.Column)var2.next();
            column.direction = direction;
        }

        return this;
    }

    public Sort and(String name) {
        this.columns.add(new Sort.Column(name));
        return this;
    }

    public Sort and(String name, Sort.Direction direction) {
        this.columns.add(new Sort.Column(name, direction));
        return this;
    }

    public List<Sort.Column> getColumns() {
        return this.columns;
    }

    /** @deprecated */
    @Deprecated
    public String toOrderBy() {
        StringBuilder sb = new StringBuilder(" ORDER BY ");

        for(int i = 0; i < this.columns.size(); ++i) {
            Sort.Column column = (Sort.Column)this.columns.get(i);
            if (i > 0) {
                sb.append(" , ");
            }

            sb.append(column.name);
            if (column.direction != Sort.Direction.Ascending) {
                sb.append(" DESC");
            }
        }

        return sb.toString();
    }

    public class Column {
        private String name;
        private Sort.Direction direction;

        public Column(String name) {
            this(name, Sort.Direction.Ascending);
        }

        public Column(String name, Sort.Direction direction) {
            this.name = name;
            this.direction = direction;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Sort.Direction getDirection() {
            return this.direction;
        }

        public void setDirection(Sort.Direction direction) {
            this.direction = direction;
        }
    }

    public static enum Direction {
        Ascending,
        Descending;

        private Direction() {
        }
    }
}
