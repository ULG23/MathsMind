package fr.ensisa.mathsmind.question;

public class QuestionProblem extends Question
{
    private static final String TITLE = "Sélectionner la réponse répondant au problème :";


    private int[] propositions;

    public QuestionProblem(String question, int answer, int[] propositions)
    {
        super(question, answer);
        this.propositions = propositions;
    }

    public int[] getPropositions() {
        return propositions;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }
}
