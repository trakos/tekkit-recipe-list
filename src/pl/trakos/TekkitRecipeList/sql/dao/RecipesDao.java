package pl.trakos.TekkitRecipeList.sql.dao;

import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.entities.Recipe;

import java.sql.SQLException;

public class RecipesDao extends CompositeDao<Recipe>
{

    public RecipesDao(Class<Recipe> dataClass) throws SQLException
    {
        super(dataClass);
    }

    public RecipesDao(ConnectionSource connectionSource, Class<Recipe> dataClass) throws SQLException
    {
        super(connectionSource, dataClass);
    }

    public RecipesDao(ConnectionSource connectionSource, DatabaseTableConfig<Recipe> tableConfig) throws SQLException
    {
        super(connectionSource, tableConfig);
    }


}
