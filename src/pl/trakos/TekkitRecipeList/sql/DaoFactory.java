package pl.trakos.TekkitRecipeList.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import pl.trakos.TekkitRecipeList.sql.dao.ItemsDao;
import pl.trakos.TekkitRecipeList.sql.entities.*;

import java.io.*;

public class DaoFactory
{
    static final String DB_FILE = "sqlite.db";
    static final int DATABASE_VERSION = 1;

    static public SQLiteDatabase createDatabase(Context context) throws IOException
    {
        File destination = context.getDatabasePath(DB_FILE);
        if (destination.exists() && PreferenceManager.getDefaultSharedPreferences(context).getInt("version", 0) < DATABASE_VERSION && !destination.delete())
        {
            throw new IOException("Couldn't delete database file!");
        }
        if (!destination.exists())
        {
            copyAsset(context, DB_FILE, destination);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("version", DATABASE_VERSION);
        }
        return SQLiteDatabase.openDatabase(destination.getPath(), null, SQLiteDatabase.OPEN_READONLY);
    }

    public static void copyAsset(Context context, String assetSource, File fileDestination) throws IOException
    {
        InputStream source = context.getAssets().open(assetSource);
        if (!fileDestination.getParentFile().exists() && !fileDestination.getParentFile().mkdirs())
        {
            throw new IOException("can't create parent directory");
        }
        OutputStream destination = new FileOutputStream(fileDestination.getPath());
        try
        {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0)
            {
                destination.write(buffer, 0, length);
            }
        }
        finally
        {
            destination.flush();
            destination.close();
            source.close();
        }
    }

    static ConnectionSource connectionSource = null;

    public static ConnectionSource getConnectionSource(Context context) throws IOException
    {
        if (connectionSource == null)
        {
            connectionSource = new AndroidConnectionSource(createDatabase(context));
        }
        return connectionSource;
    }

    static DaoFactory daoFactory;

    public static DaoFactory getDaoFactory(Context context)
    {
        if (daoFactory == null)
        {
            daoFactory = new DaoFactory(context);
        }
        return daoFactory;
    }

    public static DaoFactory getDaoFactory()
    {
        if (daoFactory == null)
        {
            throw new RuntimeException("No DaoFactory available and no context given!");
        }
        return daoFactory;
    }

    public CompositeDao<Category> categories;
    public CompositeDao<Handler> handlers;
    public CompositeDao<HandlerMachine> handlerMachines;
    public ItemsDao items;
    public CompositeDao<ItemAttribute> itemAttributes;
    public CompositeDao<ItemRawcost> itemRawcosts;
    public CompositeDao<ItemRawcostEntry> itemRawcostEntries;
    public CompositeDao<Recipe> itemRecipes;
    public CompositeDao<RecipeIngredient> itemRecipeIngredients;
    public CompositeDao<RecipeIngredientOption> itemRecipeIngredientOptions;

    public DaoFactory(Context context)
    {
        try
        {
            ConnectionSource connection = getConnectionSource(context);
            categories = DaoManager.createDao(connection, Category.class);
            handlers = DaoManager.createDao(connection, Handler.class);
            handlerMachines = DaoManager.createDao(connection, HandlerMachine.class);
            items = DaoManager.createDao(connection, Item.class);
            itemAttributes = DaoManager.createDao(connection, ItemAttribute.class);
            itemRawcosts = DaoManager.createDao(connection, ItemRawcost.class);
            itemRawcostEntries = DaoManager.createDao(connection, ItemRawcostEntry.class);
            itemRecipes = DaoManager.createDao(connection, Recipe.class);
            itemRecipeIngredients = DaoManager.createDao(connection, RecipeIngredient.class);
            itemRecipeIngredientOptions = DaoManager.createDao(connection, RecipeIngredientOption.class);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Couldn't initialize database", e);
        }
    }
}
