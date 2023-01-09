package fr.ensisa.mathsmind.question;

public enum Operator
{
    PLUS ("+"),
    MINUS ("-"),
    TIME ("*"),
	DIVIDE ("/");

    private final String name;       

    private Operator(String s) 
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