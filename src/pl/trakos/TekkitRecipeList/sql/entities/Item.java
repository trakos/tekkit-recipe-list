package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;
import pl.trakos.TekkitRecipeList.sql.dao.ItemsDao;

@DatabaseTable(tableName = "item", daoClass = ItemsDao.class)
@CompositeKey(key1 = "item_id", key2 = "item_damage")
public class Item
{
    @DatabaseField()
    public int item_id;
    @DatabaseField()
    public int item_damage;
    @DatabaseField()
    public String item_icon;
    @DatabaseField()
    public String item_name;
    @DatabaseField()
    public String item_rawName;
    @DatabaseField()
    public String item_mod;
    @DatabaseField()
    public String item_type;
    @DatabaseField()
    public String item_category_name;
    @DatabaseField()
    public String item_description;
    @DatabaseField()
    public int item_showOnList;
    @DatabaseField()
    public int item_isBaseItem;
}
