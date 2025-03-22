#include <stdio.h>
#include <stdlib.h> // 提供内存分配和释放函数，如malloc和free。
#include <ctype.h>  // 提供字符处理函数，如isdigit用于判断字符是否为数字。
#include <string.h> // 提供字符串处理函数，如strlen。
#include "calculator.h"
#include "stack.h"
int main()
{
    char infix[1000], postfix[1000];
    printf("请输入您要运算的表达式(表达式中不要有空格): ");
    scanf("%s", infix);
    InfixToPostfix(infix, postfix);
    printf("转化后的后缀表达式为: %s\n", postfix);
    double result = CalculatePostfix(postfix);
    printf("结果: %.2f\n", result);
    return 0;
}

/*
测试样例
test 1
6+(4-2)*3+9/3
结果：15.00
test 2
(3.5+4.2)*2.1-1.0/(2.0+3.0)
结果：15.97
*/

/*  我将链栈的data设置为double类型，输入输出使用char数组，
    在处理运算符时，有将运算符压入栈道操作，这会将它们强转为double类型，
    但在中缀表达式转后缀表达式时，数字直接输出到 postfix 中，而运算符通过栈进行管理；
    在后缀表达式求值时，数字直接压入栈中，而运算符从栈中弹出操作数并执行运算。
    也就是说，数字和运算符不会同时在栈中，所以不会产生与+-等运算符ASCII码相同数字，
    导致运算错误的问题。
    而我在进行中缀表达式转后缀表达式，及后缀表达式求值时，是分别读取infix、postfix，
    进行计算，所以也不需要考虑运算符弹出栈时类型要转换为char的问题。
*/