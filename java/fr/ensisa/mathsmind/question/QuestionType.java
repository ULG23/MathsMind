package fr.ensisa.mathsmind.question;

public enum QuestionType
{
    ANSWER_2V ("answer_2v"),
    ANSWER_3V ("answer_3v"),
    ANSWER_4V ("answer_4v"),
    ANSWER_SQUARE_ROOT("answer_square_root"),
    ANSWER_POWER("answer_power"),
    FILL_2V ("fill_2v"),
    FILL_3V ("fill_3v"),
    FILL_4V ("fill_4v"),
    FILL_SQUARE_ROOT("fill_square_root"),
    FILL_POWER("fill_power");

    private final String name;

    private QuestionType(String s)
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