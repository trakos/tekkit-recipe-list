package pl.trakos.TekkitRecipeList.sql.dao;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.sql.SQLException;

public class ItemsDao extends CompositeDao<Item>
{
    public ItemsDao(Class<Item> dataClass) throws SQLException
    {
        super(dataClass);
    }

    public ItemsDao(ConnectionSource connectionSource, Class<Item> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public ItemsDao(ConnectionSource connectionSource, DatabaseTableConfig<Item> tableConfig) throws SQLException
    {
        super(connectionSource, tableConfig);
    }

    public String[] getModList() throws SQLException
    {
        QueryBuilder<Item,String> query = queryBuilder();
        query.selectColumns("item_mod");
        query.groupBy("item_mod");
        return fetchAllColumn(query.query(), "item_mod");
    }

    public String[] getCategoriesList(String modName) throws SQLException
    {
        QueryBuilder<Item,String> query = queryBuilder();
        query.selectColumns("item_category_name");
        query.groupBy("item_category_name");
        query.where().eq("item_mod", modName);
        return fetchAllColumn(query.query(), "item_category_name");
    }
}
