package pl.trakos.TekkitRecipeList.sql.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import pl.trakos.TekkitRecipeList.sql.CompositeDao;
import pl.trakos.TekkitRecipeList.sql.CompositeKey;

@DatabaseTable(tableName = "handler", daoClass = CompositeDao.class)
@CompositeKey(key1 = "handler_id")
public class Handler
{
    @DatabaseField()
    public String handler_id;
    @DatabaseField()
    public String handler_name;
    @DatabaseField()
    public String handler_image;
}
