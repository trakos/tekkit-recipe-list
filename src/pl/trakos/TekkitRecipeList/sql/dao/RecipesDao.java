package pl.trakos.TekkitRecipeList.sql.dao;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.entities.Item;
import pl.trakos.TekkitRecipeList.sql.entities.Recipe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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


    public Collection<Recipe> whereItemIs(Item item, String type) throws SQLException
    {
        GenericRawResults<String[]> rawResults = queryRaw("SELECT\n"
                                                       + "  recipe_id, recipe_handler_id, recipe_visible\n"
                                                       + "FROM recipe\n"
                                                       + "  JOIN recipe_ingredient ON ingredient_recipe_id = recipe_id\n"
                                                       + "  JOIN recipe_ingredient_option ON option_ingredient_id = ingredient_id\n"
                                                       + "WHERE\n"
                                                       + "  option_item_id = ?\n"
                                                       + "  AND option_item_damage = ?\n"
                                                       + "  AND ingredient_type = ?\n"
                                                       + "  AND recipe_visible = 1",
                Integer.toString(item.item_id),
                Integer.toString(item.item_damage),
                type
        );
        ArrayList<Recipe> recipes = new ArrayList<Recipe>();
        for (String[] rawResult : rawResults)
        {
            Recipe recipe = new Recipe();
            recipe.recipe_id = Integer.parseInt(rawResult[0]);
            recipe.recipe_handler_id = rawResult[1];
            recipe.recipe_visible = Integer.parseInt(rawResult[2]);
            recipes.add(recipe);
        }
        return recipes;
    }
}
