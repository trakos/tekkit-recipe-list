package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "item_atrribute", daoClass = CompositeDao.class)
@CompositeKey(key1 = "attribute_item_id", key2 = "attribute_damage_id", key3 = "attribute_name")
public class ItemAttribute
{
    @DatabaseField()
    public int attribute_item_id;
    @DatabaseField()
    public int attribute_damage_id;
    @DatabaseField()
    public String attribute_name;
    @DatabaseField()
    public String attribute_value;
}
