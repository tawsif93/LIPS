import java.io.*;

import java.util.*;

class TowerOfHanoi
{














    static public int SolveTOH(int nDiscs, Stack source, Stack temp, Stack dest)
    {System.out.println("\nORBS: " + nDiscs + "\n");
        if (nDiscs <= 4)
        {
            if ((nDiscs % 2) == 0)
            {

                nDiscs = nDiscs - 1;
                if (nDiscs == 1)
                    return 1;










                SolveTOH(nDiscs, temp, source, dest);











            }
            return 1;
        }

        {
            SolveTOH(nDiscs - 2, source, temp, dest);



            SolveTOH(nDiscs - 2, dest, source, temp);



            SolveTOH(nDiscs - 1, temp, source, dest);
        }
        return 1;
    }

    static public Stack A = new Stack();
    static public Stack B = new Stack();
    static public Stack C = new Stack();





















































    public static void main(String[] args) 
    {
		try
		{

        {


			int maxdisc = 0;
			String inpstring = "";

			InputStreamReader input = new InputStreamReader(System.in);
			BufferedReader reader = new BufferedReader(input);
			inpstring = args[0];


			maxdisc = Integer.parseInt(inpstring);










            for (int i = maxdisc; i >= 1; i--)
                A.push(i);




            SolveTOH(maxdisc, A, B, C);



        }
	}
	catch (Exception e)
	{
        e.printStackTrace();
	}
}
}
