package com.daxton.rolemaster.application;

import com.daxton.rolemaster.api.objecthunter.exp4j.Expression;
import com.daxton.rolemaster.api.objecthunter.exp4j.ExpressionBuilder;

import java.util.Random;

public class NumberUtil {

    //給予指定範圍的隨機數字
    public static int generateRandomNumber(int in_min, int in_max) {
        int min = in_min;
        int max = in_max;
        if (in_min >= in_max) {
            min = in_max;
            max = in_min;
        }

        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    //計算(int)
    public static int countInteger(String input) {
        try {
            Expression expression = new ExpressionBuilder(input).build();
            return (int) expression.evaluate();
        }catch (IllegalArgumentException | ArithmeticException exception) {
            return  0;
        }
    }

    //計算(float)
    public static float countFloat(String input) {
        try {
            Expression expression = new ExpressionBuilder(input).build();
            return (float) expression.evaluate();
        }catch (IllegalArgumentException | ArithmeticException exception) {
            return  0;
        }
    }

    //計算(double)
    public static double countDouble(String input) {
        try {
            Expression expression = new ExpressionBuilder(input).build();
            return expression.evaluate();
        }catch (IllegalArgumentException | ArithmeticException exception) {
            return  0;
        }
    }

}
