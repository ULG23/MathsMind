package fr.ensisa.mathsmind.level;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import fr.ensisa.mathsmind.question.Difficulty;
import fr.ensisa.mathsmind.question.DifficultyType;
import fr.ensisa.mathsmind.question.Operator;
import fr.ensisa.mathsmind.question.Question;
import fr.ensisa.mathsmind.question.QuestionProblem;
import fr.ensisa.mathsmind.question.QuestionToAnswer;
import fr.ensisa.mathsmind.question.QuestionToFill;
import fr.ensisa.mathsmind.question.QuestionType;

public class Level
{
    private static final String FILE_LEVEL_PATH = "level.json";
    private static final String FILE_PROBLEMS_PATH = "problems.json";
    private static final int SCORE_EASY = 50;
    private static final int SCORE_NORMAL = 100;
    private static final int SCORE_HARD = 150;
    private int score = 0;
    private int numLevel;
    private int numberQuestion;
    private int numberSubBossQuestion;
    private int numberBossQuestion;
    private int numActualQuestion;
    private Difficulty difficultyEasy;
    private Difficulty difficultyNormal;
    private Difficulty difficultyHard;
    private ArrayList<QuestionProblem> bossQuestions;
    private ArrayList<Integer> bossQuestionAlreadyUsed = new ArrayList<Integer>();

    public Level(int numlevel, AssetManager manager)
    {
        this.numLevel = numlevel;
        this.bossQuestions = new ArrayList<QuestionProblem>();
        String jsonString;
        JSONObject json;
        try
        {
            InputStream is = manager.open(FILE_LEVEL_PATH);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);
            json = new JSONObject(jsonString).getJSONObject("level"+ String.valueOf(this.numLevel));

            this.numberQuestion = json.getInt("numberQuestion");
            this.numberSubBossQuestion = json.getInt("numberSubBossQuestion");
            this.numberBossQuestion = json.getInt("numberBossQuestion");
            this.numActualQuestion = 1;
            this.difficultyEasy = createDifficulty(DifficultyType.EASY.toString(), json);
            this.difficultyNormal = createDifficulty(DifficultyType.NORMAL.toString(), json);
            this.difficultyHard = createDifficulty(DifficultyType.HARD.toString(), json);
        }
        catch (IOException | JSONException ex)
        {
            ex.printStackTrace();
        }

        try {
            InputStream is = manager.open(FILE_PROBLEMS_PATH);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, StandardCharsets.UTF_8);

            String level;
            switch (numlevel)
            {
                case 1:
                    level = "beginner";
                    break;
                case 2:
                    level = "intermediary";
                    break;
                default:
                    level = "advanced";
                    break;
            }
            JSONArray bossQuestionsArray = new JSONObject(jsonString).getJSONArray(level);
            for(int i = 0; i < bossQuestionsArray.length(); i++)
            {
                JSONObject object = bossQuestionsArray.getJSONObject(i);
                String question = object.getString("question");
                JSONArray propArray = object.getJSONArray("proposition");
                int[] prop = new int[propArray.length()];
                for (int j = 0; j < propArray.length(); j++) {
                    prop[j] = propArray.getInt(j);
                }
                int answer = object.getInt("answer");
                this.bossQuestions.add(new QuestionProblem(question, answer, prop));
            }
        }
        catch (IOException | JSONException ex)
        {
            ex.printStackTrace();
        }
    }

    public int getScore(){return this.score;}
    public void setScore(int score){this.score = score;}
    public int getNumLevel() {return this.numLevel;}
    public int getNumberQuestion() {return numberQuestion;}
    public int getNumActualQuestion() {return numActualQuestion;}
    public void setNumActualQuestion(int numActualQuestion){this.numActualQuestion = numActualQuestion;}
    public void incrementNumActualQuestion() {this.numActualQuestion++;}
    public int getNumberSubBossQuestion() {return numberSubBossQuestion;}
    public int getNumberBossQuestion() {return numberBossQuestion;}
    public int getTotalQuestion(){return numberQuestion+numberSubBossQuestion+numberBossQuestion;}
    public int getMaxScorePossible(){return getTotalQuestion()*SCORE_HARD;}
    public boolean isPreBossQuestion(){return numActualQuestion > numberQuestion && numActualQuestion < getTotalQuestion();}
    public boolean isBossQuestion(){return numActualQuestion > numberQuestion+this.numberSubBossQuestion;}

    public void incrementScore(DifficultyType type)
    {
        switch (type)
        {
            case EASY:
                score += SCORE_EASY;
                break;
            case NORMAL:
                score += SCORE_NORMAL;
                break;
            case HARD:
                score += SCORE_HARD;

        }
    }

    private Difficulty createDifficulty(String diffName, JSONObject json) throws JSONException
    {
        JSONObject jsonDifficulty = json.getJSONObject(diffName);
        int numberMin = (int)jsonDifficulty.get("numberMin");
        int numberMax = (int)jsonDifficulty.get("numberMax");
        boolean negativeNumbers = (boolean)jsonDifficulty.get("negativeNumbers");
        int minTime = (int)jsonDifficulty.get("minTime");
        int maxTime = (int)jsonDifficulty.get("maxTime");
        int minDivide = (int)jsonDifficulty.get("minDivide");
        int maxDivide = (int)jsonDifficulty.get("maxDivide");
        int maxPow = (int)jsonDifficulty.get("maxPow");

        ArrayList<Operator> listOp = new ArrayList<Operator>();
        JSONArray operatorsString = jsonDifficulty.getJSONArray("operatorsUse");
        int opArrayLength = operatorsString.length();
        for(int i = 0; i < opArrayLength; i++)
        {
            if(Operator.PLUS.equalsName((String)operatorsString.get(i)))
                listOp.add(Operator.PLUS);
            else if(Operator.MINUS.equalsName((String)operatorsString.get(i)))
                listOp.add(Operator.MINUS);
            else if(Operator.TIME.equalsName((String)operatorsString.get(i)))
                listOp.add(Operator.TIME);
            else if(Operator.DIVIDE.equalsName((String)operatorsString.get(i)))
                listOp.add(Operator.DIVIDE);
        }
        Operator[] opArray = listOp.toArray(new Operator[listOp.size()]);

        ArrayList<QuestionType> listQuest = new ArrayList<QuestionType>();
        JSONArray questionString = jsonDifficulty.getJSONArray("questionTypes");
        int questArrayLength = questionString.length();
        for(int i = 0; i < questArrayLength; i++)
        {
            if(QuestionType.ANSWER_2V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.ANSWER_2V);
            else if(QuestionType.ANSWER_3V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.ANSWER_3V);
            else if(QuestionType.ANSWER_4V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.ANSWER_4V);
            else if(QuestionType.ANSWER_POWER.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.ANSWER_POWER);
            else if(QuestionType.ANSWER_SQUARE_ROOT.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.ANSWER_SQUARE_ROOT);
            else if(QuestionType.FILL_2V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.FILL_2V);
            else if(QuestionType.FILL_3V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.FILL_3V);
            else if(QuestionType.FILL_4V.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.FILL_4V);
            else if(QuestionType.FILL_POWER.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.FILL_POWER);
            else if(QuestionType.FILL_SQUARE_ROOT.equalsName((String)questionString.get(i)))
                listQuest.add(QuestionType.FILL_SQUARE_ROOT);
        }
        QuestionType[] questArray = listQuest.toArray(new QuestionType[listQuest.size()]);
        Difficulty difficulty = new Difficulty(numberMin, numberMax, opArray, negativeNumbers, minTime, maxTime, minDivide, maxDivide, maxPow, questArray);
        return difficulty;
    }

    public Question createQuestion(DifficultyType difficultyType)
    {
        if(isBossQuestion() || isPreBossQuestion())
        {
            return getRandomBossQuestion();
        }
        Difficulty difficultyQuestion;
        switch (difficultyType)
        {
            case EASY:
                difficultyQuestion = difficultyEasy;
                break;
            case NORMAL:
                difficultyQuestion = difficultyNormal;
                break;
            case HARD:
                difficultyQuestion = difficultyHard;
                break;
            default:
                throw new IllegalArgumentException();
        }

        Random r = new Random();

        QuestionType questionType =  difficultyQuestion.getQuestionTypes()[r.nextInt(difficultyQuestion.getQuestionTypes().length)];

        return generateQuestionFromType(questionType, difficultyQuestion);
    }

    private Question generateQuestionFromType(QuestionType questionType, Difficulty difficulty)
    {
        Question question;
        switch (questionType)
        {
            case ANSWER_2V:
                question = QuestionToAnswer.createQuestionTwoValues(difficulty);
                break;
            case ANSWER_3V:
                question = QuestionToAnswer.createQuestionThreeValues(difficulty);
                break;
            case ANSWER_4V:
                question = QuestionToAnswer.createQuestionFourValues(difficulty);
                break;
            case ANSWER_POWER:
                question = QuestionToAnswer.createQuestionPow(difficulty);
                break;
            case ANSWER_SQUARE_ROOT:
                question = QuestionToAnswer.createQuestionSquareRoot(difficulty);
                break;
            case FILL_2V:
                question = QuestionToFill.createQuestionTwoValues(difficulty);
                break;
            case FILL_3V:
                question = QuestionToFill.createQuestionThreeValues(difficulty);
                break;
            case FILL_4V:
                question = QuestionToFill.createQuestionFourValues(difficulty);
                break;
            case FILL_POWER:
                question = QuestionToFill.createQuestionPow(difficulty);
                break;
            case FILL_SQUARE_ROOT:
                question = QuestionToFill.createQuestionSquareRoot(difficulty);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return question;
    }

    private QuestionProblem getRandomBossQuestion()
    {
        if(bossQuestionAlreadyUsed.size() == bossQuestions.size())
            bossQuestionAlreadyUsed.clear();

        Random r = new Random();
        boolean out = false;
        int index = r.nextInt(bossQuestions.size());

        while (bossQuestionAlreadyUsed.contains(index))
        {
            index = r.nextInt(bossQuestions.size());
        }
        bossQuestionAlreadyUsed.add(index);
        return bossQuestions.get(index);
    }

    public static Level recreateLevel(int numLevel, AssetManager manager, int score, int numActualQuestion)
    {
        Level level = new Level(numLevel, manager);
        level.setScore(score);
        level.setNumActualQuestion(numActualQuestion);
        return level;
    }

    public static int getMaxScoreForNumLevel(int numLevel, AssetManager assetManager)
    {
        Level l = new Level(numLevel,assetManager);
        return l.getMaxScorePossible();
    }
}
