package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "category", daoClass = CompositeDao.class)
@CompositeKey(key1 = "category_name")
public class Category
{
    @DatabaseField()
    public String category_name;
}
