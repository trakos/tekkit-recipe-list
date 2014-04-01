package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "item_rawcost", daoClass = CompositeDao.class)
@CompositeKey(key1 = "rawcost_id")
public class ItemRawcost
{
    @DatabaseField()
    public int rawcost_id;
    @DatabaseField()
    public int rawcost_item_id;
    @DatabaseField()
    public int rawcost_damage_id;
}
