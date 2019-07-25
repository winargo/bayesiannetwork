package com.example.com.bayesiannetwork;

import java.util.Scanner;

public class Luhn
{
    public static boolean Check(String ccNumber)
    {
        int sum = 0;
        boolean alternate = false;
        ccNumber = ccNumber.replace(" ","");
        for (int i = ccNumber.length() - 1; i >= 0; i--)
        {
            int n = Integer.parseInt(ccNumber.substring(i, i + 1));
            if (alternate)
            {
                n *= 2;
                if (n > 9)
                {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}