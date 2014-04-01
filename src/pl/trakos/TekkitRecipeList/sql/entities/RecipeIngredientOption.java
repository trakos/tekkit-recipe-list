package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "recipe_ingredient_option", daoClass = CompositeDao.class)
@CompositeKey(key1 = "option_ingredient_id", key2 = "option_item_id", key3 = "option_item_damage")
public class RecipeIngredientOption
{
    @DatabaseField()
    public int option_ingredient_id;
    @DatabaseField()
    public int option_item_id;
    @DatabaseField()
    public int option_item_damage;
}
