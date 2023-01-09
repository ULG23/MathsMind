package fr.ensisa.mathsmind.level;

public class LevelHolder
{
    private Level level;
    private static final LevelHolder holder = new LevelHolder();

    private LevelHolder()
    {}

    public static LevelHolder getInstance()
    {
        return holder;
    }

    public Level getLevel()
    {
        return level;
    }

    public void setLevel(Level level)
    {
        this.level = level;
    }
}
