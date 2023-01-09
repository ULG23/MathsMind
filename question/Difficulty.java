package fr.ensisa.mathsmind.question;

import java.util.Arrays;

public class Difficulty
{
	private int numberMin;
	private int numberMax;
	private Operator[] operatorsUse;
	private int minDivide = 1;
	private int maxDivide = 1;
	private int minTime = 1;
	private int maxTime = 1;
	private int maxPow = 2;
	private boolean negativeNumbers;
	private QuestionType[] questionTypes;
	
	public Difficulty(int numberMin, int numberMax, Operator[] operatorsUse, boolean negativeNumbers)
	{
		this.numberMin = numberMin;
		this.numberMax = numberMax;
		this.operatorsUse = operatorsUse;
		this.negativeNumbers = negativeNumbers;
	}
	
	public Difficulty(int numberMin, int numberMax, Operator[] operatorsUse, boolean negativeNumbers, int minTime, int maxTime, int minDivide, int maxDivide, int maxPow, QuestionType[] questionTypes)
	{
		this.numberMin = numberMin;
		this.numberMax = numberMax;
		this.operatorsUse = operatorsUse;
		this.negativeNumbers = negativeNumbers;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.minDivide = minDivide;
		this.maxDivide = maxDivide;
		this.maxPow = maxPow;
		this.questionTypes = questionTypes;
	}

	@Override
	public String toString() {
		return "Difficulty{" +
				"numberMin=" + numberMin +
				", numberMax=" + numberMax +
				", operatorsUse=" + Arrays.toString(operatorsUse) +
				", minDivide=" + minDivide +
				", maxDivide=" + maxDivide +
				", minTime=" + minTime +
				", maxTime=" + maxTime +
				", maxPow=" + maxPow +
				", negativeNumbers=" + negativeNumbers +
				", questionTypes=" + Arrays.toString(questionTypes) +
				'}';
	}

	public int getNumberMin()
	{
		return numberMin;
	}

	public int getNumberMax()
	{
		return numberMax;
	}

	public Operator[] getOperatorsUse()
	{
		return operatorsUse;
	}
	
	public int getMinDivide()
	{
		return minDivide;
	}

	public int getMaxDivide()
	{ return maxDivide; }

	public int getMinTime()
	{
		return minTime;
	}

	public int getMaxTime()
	{
		return maxTime;
	}

	public int getMaxPow()
	{
		return maxPow;
	}

	public boolean isNegativeNumbersAllow(){return negativeNumbers;}

	public  QuestionType[] getQuestionTypes() {return this.questionTypes;}
}
