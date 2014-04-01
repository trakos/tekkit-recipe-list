package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "handler_machine", daoClass = CompositeDao.class)
@CompositeKey(key1 = "machine_handler_id")
public class HandlerMachine
{
    @DatabaseField()
    public String machine_handler_id;
    @DatabaseField()
    public int machine_item_id;
    @DatabaseField()
    public int machine_item_damage;
}
