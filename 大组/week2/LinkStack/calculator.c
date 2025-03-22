#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "stack.h"

// 判断字符是否为运算符
int isOperator(char c)
{
    return c == '+' || c == '-' || c == '*' || c == '/';
}

// 获取运算符的优先级
int getPriority(char op)
{
    if (op == '+' || op == '-')
        return 1;
    if (op == '*' || op == '/')
        return 2;
    return 0;
}

// 中缀表达式转后缀表达式
void InfixToPostfix(char *infix, char *postfix)
{
    LinkStack S;
    initStack(&S);
    int i = 0, j = 0;
    while (infix[i] != '\0')
    {
        if (isdigit(infix[i]) || infix[i] == '.')
        {
            // 处理数字和小数点
            while (isdigit(infix[i]) || infix[i] == '.')
            {
                postfix[j++] = infix[i++];
            }
            postfix[j++] = ' '; // 添加空格分隔数字
        }
        else if (infix[i] == '(')
        {
            // 左括号直接压入栈中
            push(&S, infix[i++]);
        }
        else if (infix[i] == ')')
        {
            // 右括号弹出栈顶元素并输出，直到遇到左括号
            while (!isEmpty(&S) && getTop(&S) != '(')
            {
                postfix[j++] = pop(&S);
                postfix[j++] = ' '; // 添加空格分隔运算符
            }
            pop(&S); // 弹出左括号
            i++;
        }
        else if (isOperator(infix[i]))
        {
            // 处理运算符
            while (!isEmpty(&S) && getPriority(getTop(&S)) >= getPriority(infix[i]))
            {
                postfix[j++] = pop(&S);
                postfix[j++] = ' '; // 添加空格分隔运算符
            }
            push(&S, infix[i++]);
        }
        else if (infix[i] == ' ')
        {
            i++; // 跳过空格
        }
    }
    // 弹出栈中剩余的运算符
    while (!isEmpty(&S))
    {
        postfix[j++] = pop(&S);
        postfix[j++] = ' '; // 添加空格分隔运算符
    }
    postfix[j] = '\0'; // 添加字符串结束符
}

// 计算后缀表达式的值
double CalculatePostfix(char *postfix)
{
    LinkStack S;
    initStack(&S);
    int i = 0;
    while (postfix[i] != '\0')
    {
        if (isdigit(postfix[i]) || postfix[i] == '.')
        {
            // 处理数字和小数点
            double num = 0;
            int decimal = 0;
            double fraction = 1.0;
            while (isdigit(postfix[i]) || postfix[i] == '.')
            {
                if (postfix[i] == '.')
                {
                    decimal = 1; // 标记遇到小数点
                    i++;
                }
                else if (decimal)
                { // 计算整数部分
                    fraction /= 10.0;
                    // postfix[i] - '0'，利用ASCII将字符数字转换为整数数字
                    num += (postfix[i] - '0') * fraction;
                    i++;
                }
                else
                {
                    num = num * 10 + (postfix[i] - '0');
                    i++;
                }
            }
            push(&S, num);
        }
        else if (isOperator(postfix[i]))
        {
            // 处理运算符
            double b = pop(&S);
            double a = pop(&S);
            switch (postfix[i])
            {
            case '+':
                push(&S, a + b);
                break;
            case '-':
                push(&S, a - b);
                break;
            case '*':
                push(&S, a * b);
                break;
            case '/':
                push(&S, a / b);
                break;
            }
            i++;
        }
        else if (postfix[i] == ' ')
        {
            i++; // 跳过空格
        }
        else
            i++;
    }
    return pop(&S);
}