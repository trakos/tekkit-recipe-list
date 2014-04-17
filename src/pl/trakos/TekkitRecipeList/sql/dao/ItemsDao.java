package pl.trakos.TekkitRecipeList.sql.dao;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.entities.Item;

import java.sql.SQLException;
import java.util.List;

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
        Where<Item,String> where = query.where().eq("item_mod", modName);
        query.limit(1L);
        if (modName.equals("CalclaviaCore"))
        {
            where.and().eq("item_name", "Tin Plate");
        }
        else if (modName.equals("AppliedEnergistics"))
        {
            where.and().eq("item_name", "ME Controller");
        }
        else if (modName.equals("ComputerCraft"))
        {
            where.and().eq("item_name", "Computer");
        }
        else if (modName.startsWith("Galacticraft"))
        {
            where.and().like("item_name", "%Rocket").and().eq("item_showOnList", 1);
        }
        else if (modName.equals("GregsLighting"))
        {
            where.and().eq("item_name", "Floodlight");
        }
        else if (modName.equals("ImmibisCore"))
        {
            where.and().eq("item_name", "Hacksaw");
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

    public List<Item> search(String text) throws SQLException
    {
        QueryBuilder<Item, String> query = queryBuilder();
        query.where().like("item_name", "%" + text + "%").and().eq("item_showOnList", 1);
        query.orderBy("item_name", true);
        query.limit(100L);
        return query.query();
    }

    public List<Item> getItemList(String modName, String categoryName) throws SQLException
    {
        Where<Item, String> query = queryBuilder().where().eq("item_mod", modName).and().eq("item_category_name", categoryName);
        query.and().eq("item_showOnList", 1);
        return query.query();
    }
}
