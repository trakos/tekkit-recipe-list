package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "recipe_ingredient", daoClass = CompositeDao.class)
@CompositeKey(key1 = "ingredient_id")
public class RecipeIngredient
{
    @DatabaseField()
    public int ingredient_id;
    @DatabaseField()
    public String ingredient_recipe_id;
    @DatabaseField()
    public String ingredient_type;
    @DatabaseField()
    public int ingredient_x;
    @DatabaseField()
    public int ingredient_y;
    @DatabaseField()
    public float ingredient_amount;
}
