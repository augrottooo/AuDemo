#ifndef CALCULATOR_H
#define CALCULATOR_H

// 判断字符是否为运算符
int isOperator(char c);

// 获取运算符的优先级
int getPriority(char op);

// 中缀表达式转后缀表达式
void InfixToPostfix(char *infix, char *postfix);

// 计算后缀表达式的值
double CalculatePostfix(char *postfix);

#endif