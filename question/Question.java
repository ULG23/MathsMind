package fr.ensisa.mathsmind.question;

import java.util.ArrayList;
import java.util.Random;

public abstract class Question
{
	protected String question;
	protected int answer;
	protected static Random r = new Random();
	public Question(String question, int answer)
	{
		this.question = question;
		this.answer = answer;
	}

	public abstract String getTitle();
	public String getQuestion()
	{
		return question;
	}
	public int getAnswer(){return answer;}

	@Override
	public String toString()
	{
		return this.question;
	}

	protected static boolean operatorIsPrioritary(Operator op)
	{
		return op == Operator.DIVIDE || op == Operator.TIME;
	}

	protected static int findMultipleOf(int num, Difficulty difficulty, int divideBy)
	{
		int min = difficulty.getMinTime();
		int max = difficulty.getMaxTime()+1;
		return r.nextInt(max-min) + min;
	}

	protected static int findDividerOf(int num, Difficulty difficulty)
	{
		ArrayList<Integer> dividers = new ArrayList<Integer>();
		dividers.add(1);
		for(int i = difficulty.getMinDivide(); i <= difficulty.getMaxDivide(); i++)
		{
			if(i != 0)
				if(num % i == 0)
					dividers.add(i);
		}
		int min = 0;
		int max = dividers.size();
		return dividers.get(r.nextInt(max-min) + min);
	}
	
	protected static int generateValue(Difficulty difficulty)
	{
		int min = difficulty.getNumberMin();
		int max = difficulty.getNumberMax() + 1;
		
		return r.nextInt(max-min) + min;
	}
	
	protected static Operator generateOperator(Difficulty difficulty)
	{
		int min = 0;
		int max = difficulty.getOperatorsUse().length;
		
		return difficulty.getOperatorsUse()[r.nextInt(max-min) + min];
	}
	
	public static double eval(final String str)
	{
		return new Object()
		{
			int pos = -1, ch;

			void nextChar()
			{
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat)
			{
				while (ch == ' ')
					nextChar();
				if (ch == charToEat)
				{
					nextChar();
					return true;
				}
				return false;
			}

			double parse()
			{
				nextChar();
				double x = parseExpression();
				if (pos < str.length())
					throw new RuntimeException("Unexpected: " + (char) ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)` | number
			// | functionName `(` expression `)` | functionName factor
			// | factor `^` factor

			double parseExpression()
			{
				double x = parseTerm();
				for (;;)
				{
					if (eat('+'))
						x += parseTerm(); // addition
					else if (eat('-'))
						x -= parseTerm(); // subtraction
					else
						return x;
				}
			}

			double parseTerm()
			{
				double x = parseFactor();
				for (;;)
				{
					if (eat('*'))
						x *= parseFactor(); // multiplication
					else if (eat('/'))
						x /= parseFactor(); // division
					else
						return x;
				}
			}

			double parseFactor()
			{
				if (eat('+'))
					return +parseFactor(); // unary plus
				if (eat('-'))
					return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('('))
				{ // parentheses
					x = parseExpression();
					if (!eat(')'))
						throw new RuntimeException("Missing ')'");
				} 
				else if ((ch >= '0' && ch <= '9') || ch == '.')
				{ // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.')
						nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} 
				else if (ch >= 'a' && ch <= 'z')
				{ // functions
					while (ch >= 'a' && ch <= 'z')
						nextChar();
					String func = str.substring(startPos, this.pos);
					if (eat('('))
					{
						x = parseExpression();
						if (!eat(')'))
							throw new RuntimeException("Missing ')' after argument to " + func);
					} 
					else
					{
						x = parseFactor();
					}
					if (func.equals("sqrt"))
						x = Math.sqrt(x);
					else if (func.equals("sin"))
						x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos"))
						x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan"))
						x = Math.tan(Math.toRadians(x));
					else
						throw new RuntimeException("Unknown function: " + func);
				} 
				else
				{
					throw new RuntimeException("Unexpected: " + (char) ch);
				}

				if (eat('^'))
					x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
