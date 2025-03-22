#include <stdio.h>
#include <stdlib.h>
#include "stack.h"
// 实现了链栈的基本操作，初始化、入栈、出栈、获取栈顶元素和销毁栈。

// 初始化栈
void initStack(LinkStack *s)
{
    s->top = NULL;
}

// 判断栈是否为空
int isEmpty(LinkStack *s)
{
    return s->top == NULL; // 如果栈顶指针top为NULL，则栈为空，返回1；否则返回0
}

// 入栈
void push(LinkStack *s, double data)
{
    LinkStackPtr newNode = (LinkStackPtr)malloc(sizeof(StackNode));
    if (newNode == NULL)
    {
        printf("内存分配失败\n");
        exit(1); // 终止程序，并返回退出码 1，表示程序异常退出
    }
    newNode->data = data;   // 将data存储到新节点中
    newNode->next = s->top; // 将新节点的next指向当前栈顶节点
    s->top = newNode;       // 更新栈顶指针top为新节点
}

// 出栈
double pop(LinkStack *s)
{
    if (isEmpty(s))
    {
        printf("栈为空，无法出栈\n");
        exit(1); // 终止程序，并返回退出码 1，表示程序异常退出
    }
    LinkStackPtr temp = s->top; // 搞一个中间指针指向栈顶
    double data = temp->data;   // 记住要弹出的数据
    s->top = temp->next;        // 通过中间指针访问下一个元素，实现把栈顶指针后移一位，即弹出
    free(temp);                 // 释放原栈顶节点的内存
    return data;                // 返回弹出的数据data
}

// 获取栈顶元素
double getTop(LinkStack *s)
{
    if (isEmpty(s))
    {
        printf("栈为空，无法获取栈顶元素\n");
        exit(1); // 终止程序，并返回退出码 1，表示程序异常退出
    }
    return s->top->data;
}

// 销毁栈
void destroyStack(LinkStack *s)
{
    while (!isEmpty(s))
    {
        pop(s);
    }
}