package crawler.db;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static crawler.util.Utils.Adaptor;
import static crawler.util.Utils.isNumeric;

/**
 * TODO: 新增SQLable接口來實現getSQL方法
 */
public class SQLBuilder {
    public static String query(String query) {
        return query;
    };

    public static Select select(String _select) {
        return new Select(_select);
    }

    public static Select select() {
        return new Select();
    }

    public static Update update(String _table) {
        return new Update(_table);
    }

    public static Insert insert(String _table) {
        return new Insert(_table);
    }

    public static class Select {
        private String _select = "*";
        private String _table = "";
        private String _where = "";
        private String _group = "";
        private String _order = "";
        private Integer _limit = 0;
        private Integer _offset = 0;

        public Select() {}

        public Select(String _select) {
            this._select = _select;
        }

        public Select from(String _from) {
            this._table = _from;
            return this;
        }

        public Select where(String _where) {
            this._where = _where;
            return this;
        }

        public Select order(String _order) {
            this._order = _order;
            return this;
        }

        public Select group(String _group) {
            this._group = _group;
            return this;
        }

        public Select limit(int _limit) {
            this._limit = _limit;
            return this;
        }

        public Select offset(int _offset) {
            this._offset = _offset;
            return this;
        }

        public String getSQL() {
            LinkedHashMap<String, Adaptor> map = new LinkedHashMap<>();
            if (_select != "") map.put("SELECT", new Adaptor<>(_select));
            if (_table != "") map.put("FROM", new Adaptor<>(_table));
            if (_where != "") map.put("WHERE", new Adaptor<>(_where));
            if (_group != "") map.put("GROUP BY", new Adaptor<>(_group));
            if (_order != "") map.put("ORDER BY", new Adaptor<>(_order));
            if (_limit > 0) map.put("LIMIT", new Adaptor<>(_limit));
            if (_offset > 0) map.put("OFFSET", new Adaptor<>(_offset));

            return parseKV(map);
        }

        private String parseKV(LinkedHashMap<String, Adaptor> map) {
            String[] tmp = new String[map.size()];
            int count = 0;

            for (String key : map.keySet()) {
                tmp[count] = key + " " + map.get(key).getObj();
                count++;
            }

            return String.join(" ", tmp);
        }
    }

    public static class Update {
        private String _table;
        private HashMap<String, Adaptor> _set = new HashMap<>();
        private ArrayList<String> _where = new ArrayList<>();

        public Update(String _table) {
            this._table = _table;
        }

        public <E> Update set(String a, E b) {
            this._set.put(a, new Adaptor<>(b));
            return this;
        }

        public Update where(String _where) {
            this._where.add(_where);
            return this;
        }

        public String getSQL() {
//            LinkedHashMap<String, Adaptor> map = new LinkedHashMap<>();
//            map.put("UPDATE", new Adaptor<>(_table));
//            map.put("SET", new Adaptor<>(_set));
//            map.put("WHERE", new Adaptor<>(_where));

            return "";
        }
    }

    public static class Insert {
        private String _table;
        private HashMap<String, Adaptor> _values = new HashMap<>();

        public Insert(String _table) {
            this._table = _table;
        }

        public <E> Insert values(String a, E b) {
            this._values.put(a, new Adaptor<>(b));
            return this;
        }

        /**
         * 以后要加上SQL防注入功能
         * 还有SQL更智能的构造SQL
         * @return
         */
        public String getSQL() {
//            LinkedHashMap<String, Adaptor> map = new LinkedHashMap<>();
//            map.put("INSERT INTO", new Adaptor<>(_table));
//            map.put("VALUES", new Adaptor<>(_values));
//            return new SQL(map);

            return "INSERT INTO " + _table + " " + parseKV(_values);
        }

        private String parseKV(HashMap<String, Adaptor> _values) {
            String keys = "";
            String values = "";
            int count = 0;

            keys += "(";
            values += "(";

            for (String key : _values.keySet()) {
                if (count < _values.size()-1) {
                    if(_values.get(key).getObj() instanceof String) {
                        keys += key + ", ";
                        values += "`" + _values.get(key).getObj() + '`' + ", ";
                    } else {
                        keys += key + ", ";
                        values += _values.get(key).getObj() + ", ";
                    }
                } else {
                    if(_values.get(key).getObj() instanceof String) {
                        keys += key + ")";
                        values += "`" + _values.get(key).getObj() + "`" + ")";
                    } else {
                        keys += key + ")";
                        values += _values.get(key).getObj() + ")";
                    }
                }
                count++;
            }

            return keys + " VALUES " + values;
        }
    }

    public static class Delete {
        private String _table;
        private String _where;

        public Delete from(String _table) {
            this._table = _table;
            return this;
        }

        public Delete where(String _where) {
            this._where = _where;
            return this;
        }

        public SQL getSQL() {
            LinkedHashMap<String, Adaptor> map = new LinkedHashMap<>();
            map.put("DELETE FROM", new Adaptor<>(_table));
            map.put("WHERE", new Adaptor<>(_where));
            return new SQL(map);
        }
    }
}