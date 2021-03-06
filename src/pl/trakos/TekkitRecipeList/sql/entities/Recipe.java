package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;
import pl.trakos.TekkitRecipeList.sql.dao.RecipesDao;

@DatabaseTable(tableName = "recipe", daoClass = RecipesDao.class)
@CompositeKey(key1 = "recipe_id")
public class Recipe
{
    @DatabaseField()
    public int recipe_id;
    @DatabaseField()
    public String recipe_handler_id;
    @DatabaseField()
    public int recipe_visible;
}
