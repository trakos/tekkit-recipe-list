package pl.trakos.TekkitRecipeList.sql;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.sql.SQLException;
import java.util.List;

public class CompositeDao<Entity> extends BaseDaoImpl<Entity, String>
{
    public CompositeDao(Class<Entity> dataClass) throws SQLException
    {
        super(dataClass);
    }

    public CompositeDao(ConnectionSource connectionSource, Class<Entity> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public CompositeDao(ConnectionSource connectionSource, DatabaseTableConfig<Entity> tableConfig) throws SQLException
    {
        super(connectionSource, tableConfig);
    }

    public CompositeKey getCompositeKey()
    {
        return this.getDataClass().getAnnotation(CompositeKey.class);
    }

    @Override
    public Entity queryForId(String s1) throws SQLException
    {
        CompositeKey compositeKey = getCompositeKey();
        if (compositeKey.key2() != null && !compositeKey.key2().equals(""))
        {
            throw new SQLException("CompositeKey has more than one field defined.");
        }
        return queryForFirst(queryBuilder().where().eq(compositeKey.key1(), s1).prepare());
    }

    public Entity queryForId(String s1, String s2) throws SQLException
    {
        CompositeKey compositeKey = getCompositeKey();
        if (compositeKey.key3() != null && !compositeKey.key3().equals(""))
        {
            throw new SQLException("CompositeKey has more than two field defined.");
        }
        return queryForFirst(queryBuilder().where().eq(compositeKey.key1(), s1).eq(compositeKey.key2(), s2).prepare());
    }

    public Entity queryForId(int s1) throws SQLException
    {
        return queryForId(Integer.toString(s1));
    }

    public Entity queryForId(int s1, int s2) throws SQLException
    {
        return queryForId(Integer.toString(s1), Integer.toString(s2));
    }

    public List<Entity> queryForEq(String field1, String value1, String field2, String value2) throws SQLException
    {
        return queryBuilder().where().eq(field1, value1).and().eq(field2, value2).query();
    }

    public String[] fetchAllColumn(List<Entity> entityList, String columnName) throws SQLException
    {
        String[] stringArray = new String[entityList.size()];
        for (int i = 0; i < entityList.size(); i++)
        {
            try
            {
                stringArray[i] = String.valueOf(entityList.get(i).getClass().getDeclaredField(columnName).get(entityList.get(i)));
            }
            catch (Exception e)
            {
                throw new SQLException("couldn't get column value", e);
            }
        }
        return stringArray;
    }

    public String[] fetchAllColumn(String columnName) throws SQLException
    {
        return fetchAllColumn(queryForAll(), columnName);
    }
}
