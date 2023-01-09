package fr.ensisa.mathsmind.question;

public enum DifficultyType
{
    EASY ("easy"),
    NORMAL ("normal"),
    HARD ("hard");

    private final String name;

    private DifficultyType(String s)
    {
        name = s;
    }

    public boolean equalsName(String otherName) 
    {
        return name.equals(otherName);
    }

    public String toString() 
    {
       return this.name;
    }
}