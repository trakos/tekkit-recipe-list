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

    public Item getModRepresentative(String modName) throws SQLException
    {
        QueryBuilder<Item,String> query = queryBuilder();
        query.where().eq("item_mod", modName);
        query.limit(1L);
        if (modName.equals("CalclaviaCore"))
        {
            query.where().eq("item_name", "Tin Plate");
        }
        else if (modName.equals("AppliedEnergistics"))
        {
            query.where().eq("item_name", "ME Controller");
        }
        else if (modName.equals("ComputerCraft"))
        {
            query.where().eq("item_name", "Computer");
        }
        else if (modName.startsWith("Galacticraft"))
        {
            query.where().like("item_name", "%Rocket");
            query.where().eq("item_showOnList", 1);
        }
        else if (modName.equals("GregsLighting"))
        {
            query.where().eq("item_name", "Floodlight");
        }
        else if (modName.equals("ImmibisCore"))
        {
            query.where().eq("item_name", "Hacksaw");
        }
        return query.queryForFirst();
    }

    public Item getCategoryModRepresentative(String modName, String categoryName) throws SQLException
    {
        QueryBuilder<Item,String> query = queryBuilder();
        query.where().eq("item_category_name", categoryName);//.and().eq("item_mod", modName);
        query.limit(1L);
        return query.queryForFirst();
    }
}
