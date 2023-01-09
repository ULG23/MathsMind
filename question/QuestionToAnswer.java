package fr.ensisa.mathsmind.question;

public class QuestionToAnswer extends Question
{
    private static final String TITLE = "Entrez le résultat de ce calcul :";
    public QuestionToAnswer(String question, int answer)
    {
        super(question, answer);
    }

    @Override
    public String getTitle()
    {
        return TITLE;
    }

    // a + b = c
    public static QuestionToAnswer createQuestionTwoValues(Difficulty difficulty)
    {
        Operator op = generateOperator(difficulty);
        int val1, val2;

        if(op != Operator.DIVIDE)
        {
            val1 = generateValue(difficulty);
            val2 = generateValue(difficulty);

            if(!difficulty.isNegativeNumbersAllow() && val1 < val2)
            {
                val1 = val1 ^ val2 ^ (val2 = val1);
            }
        }
        else
        {
            val1 = generateValue(difficulty);
            val2 = findDividerOf(val1, difficulty);
        }

        int res = (int)Question.eval(val1 + op.toString() + val2);

        String question = val1 + " " + op.toString() + " " + val2 + " = ?";

        return new QuestionToAnswer(question, res);
    }

    // a + b + c = d
    public static QuestionToAnswer createQuestionThreeValues(Difficulty difficulty)
    {
        Operator op1 = generateOperator(difficulty);
        Operator op2 = generateOperator(difficulty);
        int val1, val2, val3;

        // a + b * c
        if(!operatorIsPrioritary(op1) && operatorIsPrioritary(op2))
        {
            // b * c
            val2 = generateValue(difficulty);
            if (op2 == Operator.TIME)
                val3 = findMultipleOf(val2, difficulty,2);
            else
                val3 = findDividerOf(val2, difficulty);


            // a +
            int tmpRes = (int)Question.eval(val2 + op2.toString() + val3);

            //op1 can't be a divide or time
            val1 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val1 < tmpRes)
            {
                while(val1 < tmpRes )
                    val1 = generateValue(difficulty);
            }

        }
        else
        {
            // a + b
            val1 = generateValue(difficulty);
            if(op1 != Operator.DIVIDE && op1 != Operator.TIME)
            {
                val2 = generateValue(difficulty);
                if(!difficulty.isNegativeNumbersAllow() && val1 < val2)
                    val1 = val1 ^ val2 ^ (val2 = val1);
            }
            else if (op2 == Operator.TIME)
                val2 = findMultipleOf(val1, difficulty,2);
            else
                val2 = findDividerOf(val1, difficulty);


            // + c
            int tmpRes = (int)Question.eval(val1 + op1.toString() + val2);
            if(op2 != Operator.DIVIDE && op2 != Operator.TIME)
            {
                val3 = generateValue(difficulty);
                if(!difficulty.isNegativeNumbersAllow() && val3 > tmpRes)
                {
                    while(val3 > tmpRes)
                        val3 = generateValue(difficulty);
                }
            }
            else if (op2 == Operator.TIME)
                val3 = findMultipleOf(tmpRes, difficulty,2);
            else
                val3 = findDividerOf(tmpRes, difficulty);

        }


        int res = (int)Question.eval( String.valueOf(val1) + op1 + String.valueOf(val2)  + op2 + String.valueOf(val3) );
        int valueHide = 0;
        int numHide = r.nextInt(3)+1;
        String question = val1 + " " + op1.toString() + " " + val2 + " " + op2 + " " + val3 + " =  ?";

        return new QuestionToAnswer(question, res);
    }

    //a + b + c + d = e
    public static QuestionToAnswer createQuestionFourValues(Difficulty difficulty)
    {
        Operator op1 = generateOperator(difficulty);
        Operator op2 = generateOperator(difficulty);
        Operator op3 = generateOperator(difficulty);
        int val1, val2, val3, val4;

        //a + b + c * d
        if(!operatorIsPrioritary(op1) && !operatorIsPrioritary(op2) && operatorIsPrioritary(op3))
        {
            //c * d
            val3 = generateValue(difficulty);
            if (op3 == Operator.TIME)
                val4 = findMultipleOf(val3, difficulty,3);
            else
                val4 = findDividerOf(val3, difficulty);

            // b +
            int tmpRes = (int)eval(String.valueOf(val3) + op3 + val4);
            val2 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val2 < tmpRes)
                if(difficulty.getNumberMax() > tmpRes)
                    while(val2 < tmpRes)
                        val2 = generateValue(difficulty);
                else
                    op2 = Operator.PLUS;

            // a +
            tmpRes = (int)eval(String.valueOf(val2) + op2 + val3 + op3 + val4);
            val1 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val1 < tmpRes)
                if(difficulty.getNumberMax() > tmpRes)
                    while(val1 < tmpRes)
                        val1 = generateValue(difficulty);
                else
                    op1 = Operator.PLUS;
        }
        //a + b * c + d
        else if(!operatorIsPrioritary(op1) && operatorIsPrioritary(op2))
        {
            //b * c
            val2 = generateValue(difficulty);
            if (op2 == Operator.TIME)
                val3 = findMultipleOf(val2, difficulty,3);
            else
                val3 = findDividerOf(val2, difficulty);

            // a +
            int tmpRes = (int)eval(String.valueOf(val2) + op2 + val3);
            val1 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val1 < tmpRes)
                if(difficulty.getNumberMax() > tmpRes)
                    while(val1 < tmpRes)
                        val1 = generateValue(difficulty);
                else
                    op1 = Operator.PLUS;

            // + d
            tmpRes = (int)eval(String.valueOf(val1) + op1 + val2 + op2 + val3);
            val4 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val4 < tmpRes)
                if(difficulty.getNumberMax() > tmpRes)
                    while(val4 < tmpRes)
                        val4 = generateValue(difficulty);
                else
                    op3 = Operator.PLUS;
        }
        //a * b + c * d
        else if(operatorIsPrioritary(op1) && !operatorIsPrioritary(op2) && operatorIsPrioritary(op3))
        {
            val1 = generateValue(difficulty);
            if (op1 == Operator.TIME)
                val2 = findMultipleOf(val1, difficulty,3);
            else
                val2 = findDividerOf(val1, difficulty);

            val3 = generateValue(difficulty);
            if (op3 == Operator.TIME)
                val4 = findMultipleOf(val3, difficulty,3);
            else
                val4 = findDividerOf(val3, difficulty);

            int tmpRes1 = (int)eval(String.valueOf(val1) + op1 + val2);
            int tmpRes2 = (int)eval(String.valueOf(val3) + op3 + val4);

            if(!difficulty.isNegativeNumbersAllow() && tmpRes1 < tmpRes2)
            {
                int valTmp = val1;
                val1 = val3;
                val3 = valTmp;

                valTmp = val2;
                val2 = val4;
                val4 = valTmp;

                Operator opTmp = op1;
                op1 = op3;
                op3 = op1;
            }
        }
        // a + b * c * d
        else if(!operatorIsPrioritary(op1) && operatorIsPrioritary(op2) && operatorIsPrioritary(op3))
        {
            val2 = generateValue(difficulty);
            if (op2 == Operator.TIME)
                val3 = findMultipleOf(val2, difficulty,3);
            else
                val3 = findDividerOf(val2, difficulty);

            int tmpRes = (int)eval(String.valueOf(val2) + op2 + val3);
            if (op3 == Operator.TIME)
                val4 = findMultipleOf(tmpRes, difficulty,3);
            else
                val4 = findDividerOf(tmpRes, difficulty);

            tmpRes = (int)eval(String.valueOf(tmpRes) + op3 + val4);
            val1 = generateValue(difficulty);
            if(!difficulty.isNegativeNumbersAllow() && val1 < tmpRes)
                if(difficulty.getNumberMax() > tmpRes)
                    while(val1 < tmpRes)
                        val1 = generateValue(difficulty);
                else
                    op1 = Operator.PLUS;
        }
        else
        {
            val1 = generateValue(difficulty);
            if(op1 != Operator.DIVIDE && op1 != Operator.TIME)
            {
                val2 = generateValue(difficulty);
                if(!difficulty.isNegativeNumbersAllow() && val1 < val2)
                    val1 = val1 ^ val2 ^ (val2 = val1);
            }
            else if (op1 == Operator.TIME)
                val2 = findMultipleOf(val1, difficulty,2);
            else
                val2 = findDividerOf(val1, difficulty);

            int tmpRes = (int)eval(String.valueOf(val1) + op1 + val2);
            if(op2 != Operator.DIVIDE && op2 != Operator.TIME)
            {
                val3 = generateValue(difficulty);
                if(!difficulty.isNegativeNumbersAllow() && tmpRes < val3)
                    if(difficulty.getNumberMax() > tmpRes)
                        while(val3 > tmpRes)
                            val3 = generateValue(difficulty);
                    else
                        op2 = Operator.PLUS;
            }
            else if (op2 == Operator.TIME)
                val3 = findMultipleOf(tmpRes, difficulty,2);
            else
                val3 = findDividerOf(tmpRes, difficulty);

            tmpRes = (int)eval(String.valueOf(tmpRes) + op2 + val3);
            if(op3 != Operator.DIVIDE && op3 != Operator.TIME)
            {
                val4 = generateValue(difficulty);
                if(!difficulty.isNegativeNumbersAllow() && tmpRes < val4)
                    if(difficulty.getNumberMax() > tmpRes)
                        while(val4 > tmpRes)
                            val4 = generateValue(difficulty);
                    else
                        op3 = Operator.PLUS;
            }
            else if (op3 == Operator.TIME)
                val4 = findMultipleOf(tmpRes, difficulty,2);
            else
                val4 = findDividerOf(tmpRes, difficulty);
        }
        String question = val1 + " " + op1 + " " + val2 + " " + op2 + " "+ val3 + " " + op3 + " " + val4 + " = ?";
        int res = (int)eval(String.valueOf(val1)+op1+val2+op2+val3+op3+val4);
        return new QuestionToAnswer(question, res);
    }

    public static QuestionToAnswer createQuestionSquareRoot(Difficulty difficulty)
    {
        int square = generateValue(difficulty);
        int pow = square * square;
        String question = "√"+ (pow) + " = ?";

        return new QuestionToAnswer(question, square);
    }

    public static QuestionToAnswer createQuestionPow(Difficulty difficulty)
    {
        int val = generateValue(difficulty);
        int pow = r.nextInt(difficulty.getMaxPow()+1-2) + 2;
        String question = val + "^" + (pow) + " = ?";

        return new QuestionToAnswer(question, (int)eval( val + "^" + (pow)));
    }
}
