package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "item_rawcost_entry", daoClass = CompositeDao.class)
@CompositeKey(key1 = "entry_rawcost_id")
public class ItemRawcostEntry
{
    @DatabaseField()
    public int entry_rawcost_id;
    @DatabaseField()
    public int entry_item_id;
    @DatabaseField()
    public int entry_damage_id;
    @DatabaseField()
    public float entry_amount;
}
