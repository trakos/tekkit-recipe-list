package pl.trakos.TekkitRecipeList.navigation;

import pl.trakos.TekkitRecipeList.ListDataRow;

import java.io.Serializable;
import java.util.Stack;

public class NavigationHistory implements Serializable
{
    Stack<NavigationHistoryEntry> stack = new Stack<NavigationHistoryEntry>();

    public NavigationHistory()
    {
        super();
        push(getStartingPage());
    }

    private NavigationHistoryEntry getStartingPage()
    {
        return new NavigationHistoryEntry(NavigationLevels.LEVEL_MODS, 0, 0, new ListDataRow[NavigationLevels.values().length]);
    }

    public synchronized NavigationHistoryEntry peek()
    {
        if (stack.empty()) {
            return getStartingPage();
        }
        return stack.peek();
    }

    public synchronized NavigationHistoryEntry pop()
    {
        if (stack.empty()) {
            return getStartingPage();
        }
        return stack.pop();
    }

    public void push(NavigationHistoryEntry entry)
    {
        stack.push(entry);
    }


    public boolean empty()
    {
        return stack.empty();
    }

    public NavigationHistoryEntry findMostRecentOnLevel(NavigationLevels navigationLevel)
    {
        for (int i = stack.size() - 1; i >= 0; i--)
        {
            if (stack.get(i).navigationLevel == navigationLevel)
            {
                return stack.get(i);
            }
        }
        return null;
    }
}
